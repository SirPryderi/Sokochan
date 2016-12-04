package sokochan;

import sokochan.GridObjects.Crate;
import sokochan.GridObjects.Diamond;
import sokochan.GridObjects.Wall;
import sokochan.GridObjects.WarehouseKeeper;
import sun.plugin.dom.exception.InvalidStateException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

/**
 * This class organises all the other core classes of the Sokoban engine.
 * The parsing of levels has been implemented here,
 * but the loading from files is developed in the {@link MapLoader} Class.
 * <p>
 * Created by Vittorio on 05-Oct-16.
 */
public final class SokochanEngine {
    @SuppressWarnings("FieldCanBeLocal")
    // Max number of possible un-dos to be stored
    private final int MAX_UNDO = 64;
    // The grid for the game, will be initialised when a level is loaded
    private SokochanGrid sokochanGrid;
    // An instance of the warehouse keeper stored somewhere in the grid when the game is loaded
    private WarehouseKeeper warehouseKeeper;
    // Stats
    private int cratesCount;
    //private int cratesInCornerCount;
    private int cratesOnDiamondCount;
    private int movesCount;
    private int pushesCount;
    // Level Info
    private List<MapLoader.Level> levels;
    private int levelIndex;
    // History
    private HistoryStack historyStack;
    private String mapName;

    /**
     * Loads the default map, as defined in the {@link MapLoader} class
     *
     * @throws IOException                  if the file cannot be loaded
     * @throws MapLoader.MapLoaderException if the map is invalid
     */
    public SokochanEngine() throws MapLoader.MapLoaderException, IOException {
        MapLoader loader = new MapLoader();

        // Loads the default map
        loader.loadMap();

        loadGame(loader);
    }

    /**
     * Loads a user defined map file
     *
     * @param file the .skb file containing the map
     * @throws IOException                           in case the file is not reachable
     * @throws sokochan.MapLoader.MapLoaderException in case it is not a valid map
     */
    public SokochanEngine(File file) throws IOException, MapLoader.MapLoaderException {
        MapLoader loader = new MapLoader();

        loader.loadMap(file);

        loadGame(loader);
    }

//    /**
//     * @return the number of crates stuck in the corner
//     */
//    // TODO actually use this thing
//    public int getCratesInCornerCount() {
//        return cratesInCornerCount;
//    }

    /**
     * @return the number of crates placed in the final position
     */
    public int getCratesOnDiamondCount() {
        return cratesOnDiamondCount;
    }

    /**
     * Initialise the game from a loaded MapLoader object.
     * The provided {@link MapLoader} needs a loaded map before being passed, or an exception will be thrown.
     *
     * @param loader a properly loaded game
     * @throws InvalidStateException if the loader has not a loaded map
     */
    private void loadGame(MapLoader loader) {
        if (!loader.isMapLoaded()) {
            throw new RuntimeException("Map is not loaded");
        }

        levels = loader.getLevels();
        mapName = loader.getName();

        // If there is a level in progress in the save game loads it
        if (loader.getInProgressLevel() != null) {
            this.levelIndex = loader.getInProgressLevelIndex();
            loadLevel(loader.getInProgressLevel());
        } else {
            loadLevel(0);
        }
    }

    /**
     * Loads a specific level from a map, given its index
     *
     * @param levelIndex the level index
     * @throws IndexOutOfBoundsException if the index is not not in the level array
     */
    public void loadLevel(int levelIndex) {
        this.levelIndex = levelIndex;

        loadLevel(levels.get(levelIndex));
    }

    /**
     * Loads a level given a {@link sokochan.MapLoader.Level} object
     *
     * @param level a correctly loaded {@link sokochan.MapLoader.Level} to be loaded
     */
    private void loadLevel(MapLoader.Level level) {
        // Reset Stats
        this.movesCount = 0;
        this.pushesCount = 0;
        this.cratesCount = 0;
        //this.cratesInCornerCount = 0;
        this.cratesOnDiamondCount = 0;
        this.historyStack = new HistoryStack();

        sokochanGrid = new SokochanGrid(level.getX(), level.getY());

        MapLoader.Level.MapIterator i = level.iterator();

        while (i.hasNext()) {
            char c = i.next();

            Letters letter = Letters.valueOf(c);

            if (letter == null) {
                // TODO throw a more accurate exception
                throw new RuntimeException("Invalid letter '" + c + "' provided.");
            }

            switch (letter) {
                case WALL:
                    new Wall(sokochanGrid, i.getPosition());
                    break;
                case CRATE:
                    new Crate(sokochanGrid, i.getPosition());
                    cratesCount++;
                    break;
                case WAREHOUSE_KEEPER:
                    warehouseKeeper = new WarehouseKeeper(sokochanGrid, i.getPosition());
                    break;
                case DIAMOND:
                    new Diamond(sokochanGrid, i.getPosition());
                    break;
                case CRATE_ON_DIAMOND: // Crate on  Diamond
                    new Diamond(sokochanGrid, i.getPosition());
                    if (new Crate(sokochanGrid, i.getPosition()).isOnDiamond())
                        cratesOnDiamondCount++;
                    cratesCount++;
                    break;
                case WAREHOUSE_KEEPER_ON_DIAMOND: // WarehouseKeeper on Diamond
                    new Diamond(sokochanGrid, i.getPosition());
                    warehouseKeeper = new WarehouseKeeper(sokochanGrid, i.getPosition());
            }

        }
    }

    /**
     * Saves a game to a file
     *
     * @param file the file where the game will be saved
     * @throws IOException if it is impossible to write the file
     */
    public void saveGame(File file) throws IOException {
        MapLoader loader = new MapLoader();

        loader.saveMap(file, this);
    }

    /**
     * Moves the player, trying to push crates if possible
     *
     * @param direction the direction where to move the player
     * @return {@code true} if successfully moved | {@code false} if failed to move
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean movePlayer(Direction direction) {
        boolean pushed = false;
        boolean moved = false;

        int status = warehouseKeeper.movePushing(direction);

        if (status >= WarehouseKeeper.MOVE_PUSHED) {
            pushed = true;
            moved = true;
            pushesCount++;
            if (status == WarehouseKeeper.MOVE_PUSHED_ON_DIAMOND) {
                cratesOnDiamondCount++;
            } else if (status == WarehouseKeeper.MOVE_PUSHED_OFF_DIAMOND) {
                cratesOnDiamondCount--;
            }
        } else if (status == WarehouseKeeper.MOVE_NOT_PUSHED) {
            moved = true;
        }

        if (moved) {
            movesCount++;
            historyStack.push(new HistoryElement(direction, pushed));
        }

        return moved;
    }

    /**
     * Restores the state of the grid before the last move
     */
    public void undo() {
        if (historyStack == null || historyStack.isEmpty())
            return;

        final HistoryElement pop = historyStack.pop();

        if (pop.pushedCrate) { // If a crate has been pushed
            Crate crate = warehouseKeeper.getCrateInDirection(pop.direction);
            assert crate != null;

            boolean wasOnDiamond = crate.isOnDiamond();

            warehouseKeeper.move(pop.direction.getOppositeDirection());

            crate.move(pop.direction.getOppositeDirection());

            if (wasOnDiamond != crate.isOnDiamond())
                if (wasOnDiamond) cratesOnDiamondCount--;
                else cratesOnDiamondCount++;

            pushesCount--;
        } else { // No crate pushed
            warehouseKeeper.move(pop.direction.getOppositeDirection());
        }

        movesCount--;
    }

    /**
     * @return the grid of the game
     */
    //<editor-fold desc="Getters" defaultstate="collapsed">
    public SokochanGrid getSokochanGrid() {
        return sokochanGrid;
    }

    /**
     * @return whether the games has been completed or not
     */
    public boolean isComplete() {
        return cratesOnDiamondCount == cratesCount;
    }

    /**
     * @return the total moves made by the {@link WarehouseKeeper}
     */
    public int getMovesCount() {
        return movesCount;
    }

    /**
     * @return the number of times a {@link Crate} has been pushed
     */
    public int getPushesCount() {
        return pushesCount;
    }

    /**
     * @return the current level that is being played. Range: 0-n.
     */
    public int getLevelIndex() {
        return levelIndex;
    }

    /**
     * @return returns the {@link sokochan.MapLoader.Level} that is being played now
     */
    public MapLoader.Level getCurrentLevel() {
        return levels.get(levelIndex);
    }

    /**
     * @return the total number of levels in  the map
     */
    public int getLevelsCount() {
        return levels.size();
    }

    /**
     * @return the total number of {@link Crate}s in the map
     */
    public int getCratesCount() {
        return cratesCount;
    }

    /**
     * @return the name of the map, as specified on the file
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * @return a list of levels
     */
    List<MapLoader.Level> getLevels() {
        return levels;
    }

    /**
     * @return the number of possible undos
     */
    public int getHistoryElementsCount() {
        return historyStack.size();
    }
    //</editor-fold>

    //<editor-fold desc="Moves History" defaultstate="collapsed">

    /**
     * A container class that wraps an history entry
     */
    private class HistoryElement {
        private final Direction direction;
        private final boolean pushedCrate;

        /**
         * @param direction   the direction where the played has been moved
         * @param pushedCrate whether a {@link Crate} has been pushed or not
         */
        private HistoryElement(Direction direction, boolean pushedCrate) {
            this.direction = direction;
            this.pushedCrate = pushedCrate;
        }
    }

    /**
     * This class extends a {@link Stack} of {@link HistoryElement}, with a fixed size
     * This allows a fixed number of un-dos
     */
    private class HistoryStack extends Stack<HistoryElement> {
        @Override
        public HistoryElement push(HistoryElement item) {
            if (this.size() > MAX_UNDO) {
                removeRange(0, size() - MAX_UNDO - 1);
            }
            return super.push(item);
        }
    }
    //</editor-fold>
}
