package sokochan;

import sokochan.GridObjects.Crate;
import sokochan.GridObjects.Diamond;
import sokochan.GridObjects.Wall;
import sokochan.GridObjects.WarehouseKeeper;

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
    private SokochanGrid sokochanGrid;
    private WarehouseKeeper warehouseKeeper;
    private int cratesCount;
    private int cratesInCornerCount;
    private int cratesOnDiamondCount;
    private int movesCount;
    private int pushesCount;
    private List<MapLoader.Level> levels;
    private int levelIndex;
    private HistoryStack historyStack;
    private String mapName;

    /**
     * Loads the default map
     */
    public SokochanEngine() {
        MapLoader loader = new MapLoader();

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
    public SokochanEngine(File file) throws IOException {
        MapLoader loader = new MapLoader();

        loader.loadMap(file);

        loadGame(loader);
    }

    public int getCratesInCornerCount() {
        return cratesInCornerCount;
    }

    public int getCratesOnDiamondCount() {
        return cratesOnDiamondCount;
    }

    private void loadGame(MapLoader loader) {
        levels = loader.getLevels();
        mapName = loader.getName();

        if (loader.getInProgressLevel() != null) {
            this.levelIndex = loader.getInProgressLevelIndex();
            loadLevel(loader.getInProgressLevel());
        } else {
            loadLevel(0);
        }
    }

    public void loadLevel(int levelIndex) {
        this.levelIndex = levelIndex;

        // TODO Inspect possible Out of Bound error
        loadLevel(levels.get(levelIndex));
    }

    private void loadLevel(MapLoader.Level level) {
        this.movesCount = 0;
        this.pushesCount = 0;
        this.cratesCount = 0;
        this.cratesInCornerCount = 0;
        this.cratesOnDiamondCount = 0;
        this.historyStack = new HistoryStack();

        sokochanGrid = new SokochanGrid(level.getX(), level.getY());

        sokochanGrid.populateWithTiles();

        MapLoader.Level.MapIterator i = level.iterator();

        while (i.hasNext()) {
            char c = i.next();
            c = Character.toLowerCase(c);

            switch (c) {
                case 'w':
                    new Wall(sokochanGrid, i.getPosition());
                    break;
                case 'c':
                    if (new Crate(sokochanGrid, i.getPosition()).isOnDiamond())
                        cratesOnDiamondCount++;
                    cratesCount++;
                    break;
                case 's':
                    warehouseKeeper = new WarehouseKeeper(sokochanGrid, i.getPosition());
                    break;
                case 'd':
                    new Diamond(sokochanGrid, i.getPosition());
                    break;
                case 'p': // Crate on  Diamond
                    new Diamond(sokochanGrid, i.getPosition());
                    if (new Crate(sokochanGrid, i.getPosition()).isOnDiamond())
                        cratesOnDiamondCount++;
                    cratesCount++;
                    break;
                case 'r': // WarehouseKeeper on Diamond
                    new Diamond(sokochanGrid, i.getPosition());
                    warehouseKeeper = new WarehouseKeeper(sokochanGrid, i.getPosition());
            }

        }
    }

    public void saveGame(File file) throws IOException {
        MapLoader loader = new MapLoader();

        loader.saveMap(file, this);
    }

    public boolean movePlayer(Direction direction) {
        boolean pushed = false;
        boolean moved = false;

        int status = warehouseKeeper.movePushing(direction);

        if (status >= 1) {
            pushed = true;
            moved = true;
            pushesCount++;
            if (status == 2) {
                cratesOnDiamondCount++;
            } else if (status == 3) {
                cratesOnDiamondCount--;
            }
        } else if (status == 0) {
            pushed = false;
            moved = true;

        }

        if (moved) {
            movesCount++;
            historyStack.push(new HistoryElement(direction, pushed));
        }

        return moved;
    }

    public void undo() {
        if (historyStack == null || historyStack.isEmpty())
            return;

        final HistoryElement pop = historyStack.pop();

        if (pop.pushedCrate) {
            Crate crate = warehouseKeeper.getCrateInDirection(pop.direction);

            warehouseKeeper.move(pop.direction.getOppositeDirection());

            assert crate != null;
            crate.move(pop.direction.getOppositeDirection());
        } else {
            warehouseKeeper.move(pop.direction.getOppositeDirection());
        }

        movesCount--;
    }

    //<editor-fold desc="Getters" defaultstate="collapsed">
    public SokochanGrid getSokochanGrid() {
        return sokochanGrid;
    }

    public boolean isComplete() {
        return cratesOnDiamondCount == cratesCount;
    }

    public int getMovesCount() {
        return movesCount;
    }

    public int getPushesCount() {
        return pushesCount;
    }

    public int getLevelIndex() {
        return levelIndex;
    }

    public MapLoader.Level getCurrentLevel() {
        return levels.get(levelIndex);
    }

    public int getLevelsCount() {
        return levels.size();
    }

    public int getCratesCount() {
        return cratesCount;
    }

    public String getMapName() {
        return mapName;
    }

    List<MapLoader.Level> getLevels() {
        return levels;
    }
    //</editor-fold>

    private class HistoryElement {
        private final Direction direction;
        private final boolean pushedCrate;

        private HistoryElement(Direction direction, boolean pushedCrate) {
            this.direction = direction;
            this.pushedCrate = pushedCrate;
        }
    }

    private class HistoryStack extends Stack<HistoryElement> {
        final int MAX_SIZE = 64;

        @Override
        public HistoryElement push(HistoryElement item) {
            if (this.size() > MAX_SIZE) {
                removeRange(0, size() - MAX_SIZE - 1);
            }
            return super.push(item);
        }
    }
}
