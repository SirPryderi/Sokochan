package sokochan.GridObjects;

import sokochan.SokochanGrid;

import java.awt.*;

/**
 * Represent a crate in the {@link SokochanGrid}. A collection of Crates is stored in the {@link sokochan.SokochanEngine} for easier retrieval.
 * Created by Vittorio on 05-Oct-16.
 */
public class Crate extends MovableGridObject {
    public Crate(SokochanGrid grid, Point position) {
        super(grid, position);
    }

    public boolean isOnDiamond() {
        return getGrid().getTile(getPosition()) instanceof Diamond;
    }
}
