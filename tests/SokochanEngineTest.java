package tests;

import org.junit.Test;
import sokochan.Direction;
import sokochan.GridObjects.WarehouseKeeper;
import sokochan.SokochanEngine;

import java.awt.*;
import java.io.File;
import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Sokochan engine test
 * Created by Vittorio on 03/12/2016.
 */
@SuppressWarnings("JavaDoc")
public class SokochanEngineTest {
    @Test
    public void getCratesOnDiamondCount() throws Exception {
        SokochanEngine engine = new SokochanEngine(new File("tests/testlevels/test1.skb"));

        assertEquals("Initial crate count", 3, engine.getCratesOnDiamondCount());

        engine.movePlayer(Direction.WEST);

        assertEquals(4, engine.getCratesOnDiamondCount());

        engine.movePlayer(Direction.WEST);

        assertEquals(3, engine.getCratesOnDiamondCount());

        engine.undo();

        assertEquals(4, engine.getCratesOnDiamondCount());

        engine.undo();

        assertEquals(3, engine.getCratesOnDiamondCount());
    }

    @Test
    public void loadLevel() throws Exception {
        SokochanEngine engine = new SokochanEngine();

        engine.loadLevel(2);

        assertEquals("Load third level", 2, engine.getLevelIndex());

        engine.loadLevel(0);

        assertEquals("Load first level", 0, engine.getLevelIndex());
    }

    @Test
    public void saveGame() throws Exception {
        SokochanEngine engine = new SokochanEngine(new File("tests/testlevels/test1.skb"));

        engine.movePlayer(Direction.WEST);
        engine.movePlayer(Direction.SOUTH);
        engine.movePlayer(Direction.WEST);
        engine.movePlayer(Direction.EAST);
        engine.movePlayer(Direction.SOUTH);
        engine.movePlayer(Direction.WEST);

        File file = new File("tests/testlevels/savetest.skb");

        engine.saveGame(file);

        engine = new SokochanEngine(file);

        assertEquals("Restored the game state after load", 2, engine.getCratesOnDiamondCount());

        file.deleteOnExit();
    }

    private WarehouseKeeper extractWarehouseKeeper(SokochanEngine engine) throws NoSuchFieldException, IllegalAccessException {
        Field warehouseKeeperField = SokochanEngine.class.getDeclaredField("warehouseKeeper");
        warehouseKeeperField.setAccessible(true);

        return (WarehouseKeeper) warehouseKeeperField.get(engine);
    }

    @Test
    public void undo() throws Exception {
        SokochanEngine engine = new SokochanEngine();

        WarehouseKeeper keeper = extractWarehouseKeeper(engine);

        Point position1 = keeper.getPosition();

        engine.movePlayer(Direction.WEST);
        engine.movePlayer(Direction.NORTH);

        assertNotEquals("The object moved", position1, keeper.getPosition());

        engine.undo();
        engine.undo();

        assertEquals("The object position was restored", position1, keeper.getPosition());
    }

    @Test
    public void getSokochanGrid() throws Exception {
        SokochanEngine engine = new SokochanEngine(new File("tests/testlevels/test1.skb"));

        assertNotNull(engine.getSokochanGrid());
    }

    @Test
    public void isComplete() throws Exception {
        SokochanEngine engine = new SokochanEngine(new File("tests/testlevels/test2.skb"));
        engine.movePlayer(Direction.WEST);

        assertEquals("Level complete", true, engine.isComplete());
    }

    @Test
    public void getMovesCount() throws Exception {
        SokochanEngine engine = new SokochanEngine();

        assertEquals("Initial count", 0, engine.getMovesCount());

        engine.movePlayer(Direction.WEST);
        engine.movePlayer(Direction.NORTH);
        engine.movePlayer(Direction.NORTH);

        assertEquals("Third move", 3, engine.getMovesCount());

        engine.undo();

        assertEquals("After undo", 2, engine.getMovesCount());

        engine.movePlayer(Direction.NORTH);

        assertEquals("Move after undo", 3, engine.getMovesCount());
    }

    @Test
    public void getPushesCount() throws Exception {
        SokochanEngine engine = new SokochanEngine(new File("tests/testlevels/test2.skb"));

        engine.movePlayer(Direction.WEST);
        engine.movePlayer(Direction.WEST);
        engine.movePlayer(Direction.WEST);

        assertEquals("Pushes", 3, engine.getPushesCount());

        // Random number of undos
        engine.undo();
        assertEquals("Pushes", 2, engine.getPushesCount());

        engine.undo();
        engine.undo();
        engine.undo();
        engine.undo();
        engine.undo();
        engine.undo();
        engine.undo();

        // Should be 0 again
        assertEquals("Initial count", 0, engine.getPushesCount());

    }

    @Test
    public void getLevelIndex() throws Exception {
        SokochanEngine engine = new SokochanEngine();

        assertEquals("First level", 0, engine.getLevelIndex());

        engine.loadLevel(3);

        assertEquals("Fourth level lodaed", 3, engine.getLevelIndex());
    }

    @Test
    public void getLevelsCount() throws Exception {
        SokochanEngine engine = new SokochanEngine();

        assertEquals("Levels in the default map", 6, engine.getLevelsCount());

        engine = new SokochanEngine(new File("tests/testlevels/test3.skb"));

        assertEquals("Levels in the default map with a game in progress", 5, engine.getLevelsCount());

        engine = new SokochanEngine(new File("tests/testlevels/test1.skb"));

        assertEquals("Levels in test1.skb", 1, engine.getLevelsCount());
    }

    @Test
    public void getCratesCount() throws Exception {
        SokochanEngine engine = new SokochanEngine();

        assertEquals("Crates in default level", 1, engine.getCratesCount());

        engine = new SokochanEngine(new File("tests/testlevels/test1.skb"));

        assertEquals("Crates in test1.sbk", 4, engine.getCratesCount());
    }

    @Test
    public void getMapName() throws Exception {
        SokochanEngine engine = new SokochanEngine();

        assertEquals("Default map name", "Example Game!", engine.getMapName());

        engine = new SokochanEngine(new File("tests/testlevels/test1.skb"));

        assertEquals("Default map name", "test 1", engine.getMapName());
    }

    @Test
    public void getHistoryElementsCount() throws Exception {
        SokochanEngine engine = new SokochanEngine(new File("tests/testlevels/test2.skb"));

        // Moving towards wall
        engine.movePlayer(Direction.NORTH);
        engine.movePlayer(Direction.SOUTH);

        assertEquals("No valid move were made", 0, engine.getHistoryElementsCount());

        // valid moves
        engine.movePlayer(Direction.WEST);
        engine.movePlayer(Direction.WEST);
        engine.movePlayer(Direction.WEST);

        assertEquals("History Count", 3, engine.getHistoryElementsCount());

        // Random number of undos
        engine.undo();
        assertEquals("Pushes", 2, engine.getHistoryElementsCount());

        engine.undo();
        engine.undo();
        engine.undo();
        engine.undo();
        engine.undo();
        engine.undo();
        engine.undo();

        // Should be 0 again
        assertEquals("Initial count", 0, engine.getHistoryElementsCount());
    }

}