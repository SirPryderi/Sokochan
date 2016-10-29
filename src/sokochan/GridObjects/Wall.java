package sokochan.GridObjects;

import sokochan.SokochanGrid;

import java.awt.*;

/**
 * A {@link Tile} child that is always non-walkable.
 * Created by Vittorio on 12-Oct-16.
 */
public class Wall extends TileGridObject {
    public Wall(SokochanGrid grid, Point position) {
        super(grid, position, false);
    }
}
