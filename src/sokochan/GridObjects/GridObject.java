package sokochan.GridObjects;

import sokochan.SokochanGrid;

import java.awt.*;

/**
 * This abstract class aims to provide methods and fields for every kind of object placed in a {@link SokochanGrid} (both tiles and movable objects).
 * It includes a reference to the parent {@link SokochanGrid} object to facilitate movement routines.
 * Created by Vittorio on 05-Oct-16.
 */
@SuppressWarnings("WeakerAccess")
public abstract class GridObject{
    private Point position;
    private final SokochanGrid grid;

    GridObject(SokochanGrid grid, Point Position) {
        this.grid = grid;
        this.position = Position;
        place();
    }

    protected abstract void place();

    Point getPosition() {
        return position;
    }

    void setPosition(Point position) {
        this.position = position;
    }

    SokochanGrid getGrid() {
        return grid;
    }
}
