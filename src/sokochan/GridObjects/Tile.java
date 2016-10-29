package sokochan.GridObjects;

import sokochan.SokochanGrid;

import java.awt.*;

/**
 * A {@link Tile} represents the normal "floor" of the grid, and can be walked upon, if it is empty.
 * Created by Vittorio on 12-Oct-16.
 */
public class Tile extends TileGridObject {
    public Tile(SokochanGrid grid, Point position) {
        super(grid, position, true);
    }

    @Override
    public boolean isWalkable() {
        return getPlacedObject() == null;
    }
}
