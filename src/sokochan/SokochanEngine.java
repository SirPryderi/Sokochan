package sokochan;

import sokochan.GridObjects.Crate;
import sokochan.GridObjects.Diamond;
import sokochan.GridObjects.Wall;
import sokochan.GridObjects.WarehouseKeeper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
    private Crate[] crates;
    private int movesCount;
    private int pushesCount;
    private List<MapLoader.Level> levels;
    private int levelIndex;
    private HistoryStack historyStack;
    private String mapName;

    public SokochanEngine() throws IOException {
        loadGame(new File("C:\\Users\\Vittorio\\Projects\\JavaProjects\\Sokochan\\src\\fx\\SampleGame.skb"));
    }

    public SokochanEngine(File file) throws IOException {
        loadGame(file);
    }

    private boolean loadGame(File file) throws IOException {
        return loadGame(file, 0);
    }

    private boolean loadGame(File file, int levelIndex) throws IOException {
        MapLoader loader = new MapLoader(file.getAbsolutePath());

        levels = loader.getLevels();
        mapName = loader.getName();

        loadLevel(levelIndex);

        return true;
    }

    public void loadLevel(int levelIndex) {
        this.levelIndex = levelIndex;
        this.movesCount = 0;
        this.historyStack = new HistoryStack();

        MapLoader.Level level;
        try {
            level = levels.get(levelIndex);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        sokochanGrid = new SokochanGrid(level.getX(), level.getY());

        sokochanGrid.populateWithTiles();

        List<Crate> crates = new ArrayList<>();

        MapLoader.Level.MapIterator i = level.iterator();

        while (i.hasNext()) {
            char c = i.next();
            c = Character.toLowerCase(c);

            switch (c) {
                case 'w':
                    new Wall(sokochanGrid, i.getPosition());
                    break;
                case 'c':
                    crates.add(new Crate(sokochanGrid, i.getPosition()));
                    break;
                case 's':
                    warehouseKeeper = new WarehouseKeeper(sokochanGrid, i.getPosition());
                    break;
                case 'd':
                    new Diamond(sokochanGrid, i.getPosition());
                    break;
            }

        }

        this.crates = Arrays.copyOf(crates.toArray(), crates.size(), Crate[].class);
    }

    public boolean saveGame(File file) {
        return false;
    }

    public boolean movePlayer(Direction direction) {
        boolean pushed = false;
        boolean moved = false;

        int status = warehouseKeeper.movePushing(direction);

        switch (status) {
            case 1:
                pushed = true;
                moved = true;
                pushesCount++;
                break;
            case 0:
                pushed = false;
                moved = true;
                break;
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
        for (Crate crate : crates) {
            if (!crate.isOnDiamond())
                return false;
        }

        return true;
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
        return crates.length;
    }

    public String getMapName() {
        return mapName;
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
        final int MAX_SIZE = 16;

        @Override
        public HistoryElement push(HistoryElement item) {
            if (this.size() > MAX_SIZE) {
                removeRange(0, size() - MAX_SIZE - 1);
            }
            return super.push(item);
        }
    }
}