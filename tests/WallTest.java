package tests;

import org.junit.Test;
import sokochan.GridObjects.Wall;
import sokochan.SokochanGrid;

import java.awt.*;

import static org.junit.Assert.assertEquals;

/**
 * Wall tests
 * Created by Vittorio on 03/12/2016.
 */
@SuppressWarnings("JavaDoc")
public class WallTest {
    @Test
    public void wallNotWalkableTest() throws Exception {
        assertEquals("Wall is walkable", false, new Wall(new SokochanGrid(1, 1), new Point(0, 0)).isWalkable());
    }
}