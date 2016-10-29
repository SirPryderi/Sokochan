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
        MovableGridObject object = getGrid().getGridObject(getDisplacement(direction));

        // If is a Crate, return it, otherwise return null
        return object instanceof Crate ? (Crate) object : null;
    }

    /**
     * A custom method for moving the {@link WarehouseKeeper}
     *
     * @param direction where moving
     * @return -1 if failed, 0 if moved, 1 if moved and pushed
     */
    public int movePushing(Direction direction) {
        Crate crate = getCrateInDirection(direction);
        boolean pushed = false;

        if (crate != null) {
            pushed = push(crate, direction);

            if (!pushed)
                return -1;
        }

        if (move(direction)) {
            return pushed ? 1 : 0;
        }

        return -1;
    }
}
