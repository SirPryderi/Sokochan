package sokochan.GridObjects;

import sokochan.SokochanGrid;

import java.awt.*;

/**
 * Extends {@link Tile}, and represents the position in the grid where the crates should be taken to.
 * Created by Vittorio on 12-Oct-16.
 */
public class Diamond extends Tile {
    /**
     * Creates and places a {@link Diamond} in the {@link SokochanGrid}
     *
     * @param grid     parent grid
     * @param position where to put the {@link Diamond}
     */
    public Diamond(SokochanGrid grid, Point position) {
        super(grid, position);
    }
}
