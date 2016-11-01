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

    /**
     * @return <code>true</code> if on the same Position of a {@link Diamond}, <code>false</code> otherwise.
     */
    public boolean isOnDiamond() {
        return getGrid().getTile(getPosition()) instanceof Diamond;
    }

    /**
     * @return true if stuck between two {@link Wall} objects, i.e. in a corner, <code>false</code> otherwise.
     */
    public boolean isStuck() {
        TileGridObject[] neighbourTiles = getNeighbourTiles();

        for (int i = 0; i < neighbourTiles.length; i++) {
            int next = i + 1;

            if (i == neighbourTiles.length - 1) {
                next = 0;
            }

            if (neighbourTiles[i] instanceof Wall && neighbourTiles[next] instanceof Wall) {
                return true;
            }
        }

        return false;
    }
}
