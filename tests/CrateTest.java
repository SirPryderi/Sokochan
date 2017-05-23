package tests;

import org.junit.Test;
import sokochan.Direction;
import sokochan.GridObjects.*;
import sokochan.SokochanGrid;

import java.awt.*;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Crate tests
 * Created by Vittorio on 03/12/2016.
 */
@SuppressWarnings("JavaDoc")
public class CrateTest {
    private SokochanGrid grid = new SokochanGrid(5, 5);
    private Crate crate = new Crate(grid, new Point(0, 0));
    private Crate crateOnDiamond = new Crate(grid, new Point(4, 4));
    private Diamond diamond = new Diamond(grid, new Point(4, 4));

    @Test
    public void isOnDiamond() throws Exception {
        assertEquals("Crate not on diamond", false, crate.isOnDiamond());
        assertEquals("Crate non diamond", true, crateOnDiamond.isOnDiamond());
    }

    @Test
    public void move() throws Exception {
        crate.move(Direction.NORTH);
        crate.move(Direction.WEST);
        assertEquals("Move towards edge - north and west", new Point(0, 0), crate.getPosition());

        crate.move(Direction.EAST);
        assertEquals("Move towards tile - east", new Point(1, 0), crate.getPosition());

        crate.move(Direction.EAST);
        crate.move(Direction.SOUTH);
        assertEquals("Move towards tile - east and north", new Point(2, 1), crate.getPosition());

        new Wall(grid, new Point(3, 1));
        crate.move(Direction.EAST);
        assertEquals("Move towards wall", new Point(2, 1), crate.getPosition());
    }

    @Test
    public void getNeighbour() throws Exception {
        assert crate.getPosition().equals(new Point(0, 0));

        Crate crate2 = new Crate(grid, new Point(1, 0));

        Method method = crate.getClass().getSuperclass().getDeclaredMethod("getNeighbour", Direction.class);

        method.setAccessible(true);

        assertEquals("Northern neighbour", null, method.invoke(crate, Direction.NORTH));
        assertEquals("Southern neighbour", null, method.invoke(crate, Direction.SOUTH));

        assertEquals("Eastern neighbour", crate2, method.invoke(crate, Direction.EAST));
    }

    @Test
    public void getNeighbourTile() throws Exception {
        Method method = crate.getClass().getSuperclass().getDeclaredMethod("getNeighbourTile", Direction.class);
        method.setAccessible(true);

        assertEquals("Out of boundaries tile", null, method.invoke(crate, Direction.NORTH));

        assertSame("Proper tile", Tile.class, (method.invoke(crate, Direction.EAST)).getClass());
    }

    @Test
    public void place() throws Exception {
        WarehouseKeeper keeper = new WarehouseKeeper(grid, new Point(3, 3));

        assertEquals("Correctly placed keeper", keeper, grid.getGridObject(3, 3));
    }

    @Test
    public void getPosition() throws Exception {
        assertEquals("Gets crate2 position", new Point(4, 4), crateOnDiamond.getPosition());
    }

    @Test
    public void setPosition() throws Exception {
        Point position = new Point(3, 3);

        Method method = crate.getClass().getSuperclass().getSuperclass().getDeclaredMethod("setPosition", Point.class);
        method.setAccessible(true);

        method.invoke(crate, position);

        assertEquals("Changes object position", position, crate.getPosition());
    }

    @Test
    public void getGrid() throws Exception {
        Method method = crate.getClass().getSuperclass().getSuperclass().getDeclaredMethod("getGrid");
        method.setAccessible(true);

        assertSame("Gets the grid", grid, method.invoke(crate));
    }

}