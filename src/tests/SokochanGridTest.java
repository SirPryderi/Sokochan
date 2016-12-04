package tests;

import org.junit.Test;
import sokochan.GridObjects.Crate;
import sokochan.GridObjects.Tile;
import sokochan.SokochanGrid;

import java.awt.*;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * {@link sokochan.SokochanGrid} tests
 * Created by Vittorio on 04/12/2016.
 */
@SuppressWarnings("JavaDoc")
public class SokochanGridTest {
    @Test
    public void getTile() throws Exception {
        SokochanGrid grid = new SokochanGrid(5, 5);

        Tile tile = new Tile(grid, new Point(4, 4));

        assertSame("Tile is the same", tile, grid.getTile(4, 4));
    }

    @Test
    public void getGridObject() throws Exception {
        SokochanGrid grid = new SokochanGrid(5, 5);

        Crate crate = new Crate(grid, new Point(4, 4));

        assertSame("Crate is the same", crate, grid.getGridObject(4, 4));

        assertSame("Empty tile", null, grid.getGridObject(2, 2));

        assertSame("Out of bound tile", null, grid.getGridObject(5, 5));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void addOutOfBound1() throws Exception {
        SokochanGrid grid = new SokochanGrid(5, 5);
        Crate crate2 = new Crate(grid, new Point(-1, -1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void addOutOfBound2() throws Exception {
        SokochanGrid grid = new SokochanGrid(5, 5);
        Crate crate2 = new Crate(grid, new Point(5, 5));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void addOutOfBound3() throws Exception {
        SokochanGrid grid = new SokochanGrid(5, 5);
        Crate crate2 = new Crate(grid, new Point(100, 100));
    }

    @Test
    public void removeGridObject() throws Exception {
        SokochanGrid grid = new SokochanGrid(5, 5);

        Crate crate = new Crate(grid, new Point(4, 4));

        grid.removeGridObject(new Point(4, 4));

        assertNull("Object should not be there", grid.getGridObject(4, 4));
    }


}