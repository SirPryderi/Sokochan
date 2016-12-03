package tests;

import org.junit.Test;
import sokochan.Direction;
import sokochan.GridObjects.Crate;
import sokochan.GridObjects.WarehouseKeeper;
import sokochan.SokochanGrid;

import java.awt.*;

import static org.junit.Assert.assertEquals;

/**
 * Warehouse keeper test
 * Created by Vittorio on 03/12/2016.
 */
@SuppressWarnings("JavaDoc")
public class WarehouseKeeperTest {


    @Test
    public void getCrateInDirection() throws Exception {
        SokochanGrid grid = new SokochanGrid(5, 5);
        Crate crate1 = new Crate(grid, new Point(0, 0));
        Crate crate2 = new Crate(grid, new Point(0, 2));
        WarehouseKeeper keeper = new WarehouseKeeper(grid, new Point(0, 1));

        assertEquals("North crate", crate1, keeper.getCrateInDirection(Direction.NORTH));

        assertEquals("South crate", crate2, keeper.getCrateInDirection(Direction.SOUTH));

        assertEquals("Nothing", null, keeper.getCrateInDirection(Direction.EAST));
        assertEquals("Outside boundaries", null, keeper.getCrateInDirection(Direction.WEST)); // outside boundaries
    }

    @Test
    public void movePushing() throws Exception {
        SokochanGrid grid = new SokochanGrid(5, 5);
        Crate crate1 = new Crate(grid, new Point(0, 0));
        Crate crate2 = new Crate(grid, new Point(0, 2));
        WarehouseKeeper keeper = new WarehouseKeeper(grid, new Point(0, 1));

        assertEquals("Moving up   - not movable crate", WarehouseKeeper.MOVE_FAILED, keeper.movePushing(Direction.NORTH));
        assertEquals("Moving down - movable crate", WarehouseKeeper.MOVE_PUSHED, keeper.movePushing(Direction.SOUTH));
        assertEquals("Moving west - boundaries", WarehouseKeeper.MOVE_FAILED, keeper.movePushing(Direction.WEST));
        assertEquals("Moving east - nothing", WarehouseKeeper.MOVE_NOT_PUSHED, keeper.movePushing(Direction.EAST));

        // add two crates next to each other they will be pushed later
        new Crate(grid, new Point(2, 2));
        new Crate(grid, new Point(3, 2));

        assertEquals("Moving east - Pushing two crates", WarehouseKeeper.MOVE_FAILED, keeper.movePushing(Direction.EAST));
    }

}