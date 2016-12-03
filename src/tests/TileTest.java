package tests;

import org.junit.Test;
import sokochan.GridObjects.Crate;
import sokochan.GridObjects.TileGridObject;
import sokochan.SokochanGrid;

import java.awt.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tile tests
 * Created by Vittorio on 03/12/2016.
 */
@SuppressWarnings("JavaDoc")
public class TileTest {
    @Test
    public void isWalkable() throws Exception {
        SokochanGrid grid = new SokochanGrid(2, 2);

        new Crate(grid, new Point(0, 1));

        TileGridObject tile1 = grid.getTile(0, 0);
        TileGridObject tile2 = grid.getTile(0, 1);

        assertNotNull(tile1);
        assertNotNull(tile2);

        assertEquals("Tile1 should be walkable", true, tile1.isWalkable());
        assertEquals("Tile2 should not be walkable", false, tile2.isWalkable());
    }

}