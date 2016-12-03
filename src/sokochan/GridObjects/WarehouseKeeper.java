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
    /**
     * Return code for failed movement
     */
    public final static int MOVE_FAILED = -1;
    /**
     * Return code for successful movement without any pushing
     */
    public final static int MOVE_NOT_PUSHED = 0;
    /**
     * Return code for successful movement with a pushing that didn't change the number of {@link Crate}s on {@link Diamond}
     */
    public final static int MOVE_PUSHED = 1;
    /**
     * Return code for successful movement with a pushing that resulted in another {@link Crate} moved on diamond {@link Diamond}
     */
    public final static int MOVE_PUSHED_ON_DIAMOND = 2;
    /**
     * Return code for successful movement with a pushing that resulted in fewer {@link Crate}s moved on diamond {@link Diamond}
     */
    public final static int MOVE_PUSHED_OFF_DIAMOND = 3;

    /**
     * Creates and places a {@link WarehouseKeeper} in the {@link SokochanGrid}
     *
     * @param grid     parent grid
     * @param position where to put the {@link WarehouseKeeper}
     */
    public WarehouseKeeper(SokochanGrid grid, Point position) {
        super(grid, position);
    }

    /**
     * Attempts to push a {@link Crate} in a given {@link Direction}. Returns a boolean accordingly.
     *
     * @param crate     the crate to move
     * @param direction where pushing the crate
     * @return {@code true} if pushed | {@code false} if failed to push (crate is blocked)
     */
    @SuppressWarnings("UnusedReturnValue")
    private boolean push(Crate crate, Direction direction) {
        return crate.move(direction);

    }

    /**
     * Returns a {@link Crate} adjacent to the {@link WarehouseKeeper} in a given direction, {@code null} otherwise.
     *
     * @param direction where to find the {@link Crate}
     * @return the crate or {@code null}
     */
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

        int pushed_status = MOVE_PUSHED;

        if (crate != null) {
            boolean wasOnDiamond = crate.isOnDiamond();
            pushed = push(crate, direction);

            if (!pushed)
                return MOVE_FAILED;

            // If the crate has changed the onDiamond status
            if (wasOnDiamond != crate.isOnDiamond())
                pushed_status = wasOnDiamond ? MOVE_PUSHED_OFF_DIAMOND : MOVE_PUSHED_ON_DIAMOND;
        }

        if (move(direction)) {
            return pushed ? pushed_status : MOVE_NOT_PUSHED;
        }

        return -1;
    }
}
