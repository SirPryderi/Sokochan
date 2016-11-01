package sokochan.GridObjects;

import sokochan.Direction;
import sokochan.SokochanGrid;

import java.awt.*;

/**
 * Extends from {@link MovableGridObject} and allows movement of the player.
 * It’s the only object that is moved directly from the {@link sokochan.SokochanEngine}.
 * Contains a method to push a {@link Crate}.
 * <p>
 * Created by Vittorio on 05-Oct-16.
 */
public class WarehouseKeeper extends MovableGridObject {
    public WarehouseKeeper(SokochanGrid grid, Point position) {
        super(grid, position);
    }

    @SuppressWarnings("UnusedReturnValue")
    private boolean push(Crate crate, Direction direction) {
        return crate.move(direction);

    }

    public Crate getCrateInDirection(Direction direction) {
        MovableGridObject object = getNeighbour(direction);

        // If is a Crate, return it, otherwise return null
        return object instanceof Crate ? (Crate) object : null;
    }

    /**
     * A custom method for moving the {@link WarehouseKeeper}
     *
     * @param direction where moving the {@link WarehouseKeeper} to
     * @return <ul>
     * <li>-1 if failed</li>
     * <li>0 if moved</li>
     * <li>1 if moved and pushed</li>
     * <li>2 if moved and pushed and a crate is now on diamond</li>
     * <li>3 if moved and pushed and a crate is no longer on diamond</li>
     * </ul>
     */
    public int movePushing(Direction direction) {
        Crate crate = getCrateInDirection(direction);
        boolean pushed = false;

        int pushed_status = 1;

        if (crate != null) {
            boolean wasOnDiamond = crate.isOnDiamond();
            pushed = push(crate, direction);

            if (!pushed)
                return -1;

            // If the crate has changed the onDiamond status
            if (wasOnDiamond != crate.isOnDiamond()) pushed_status = wasOnDiamond ? 3 : 2;
        }

        if (move(direction)) {
            return pushed ? pushed_status : 0;
        }

        return -1;
    }
}
