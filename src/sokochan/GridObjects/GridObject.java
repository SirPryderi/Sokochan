package sokochan.GridObjects;

import sokochan.SokochanGrid;

import java.awt.*;
import java.security.InvalidParameterException;

/**
 * This abstract class aims to provide methods and fields for every kind of object placed in a {@link SokochanGrid} (both tiles and movable objects).
 * It includes a reference to the parent {@link SokochanGrid} object to facilitate movement routines.
 * Created by Vittorio on 05-Oct-16.
 */
@SuppressWarnings("WeakerAccess")
public abstract class GridObject {
    // A reference to the parent grid must always be provided
    private final SokochanGrid grid;
    // A Point representing the position. Must be kept consistent
    private Point position;

    /**
     * This constructor will place the object on the grid
     *
     * @param grid     A grid reference, should be not null
     * @param Position A valid position where the object should be placed
     */
    GridObject(SokochanGrid grid, Point Position) {
        if (grid == null) {
            throw new InvalidParameterException("The grid cannot be null");
        }

        this.grid = grid;
        this.position = Position;
        place();
    }

    /**
     * Children  class are responsible to place themselves on the grid, either as a tile or an object placed on it
     */
    protected abstract void place();

    /**
     * Gives the coordinates of an object
     *
     * @return a {@link Point} representing the coordinates of the object in the grid
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Sets the internal position  of the object. Note: won't automatically update the grid, just the internal value
     *
     * @param position the new position
     */
    void setPosition(Point position) {
        this.position = position;
    }

    /**
     * Gives the {@link SokochanGrid} object where the object is placed in
     *
     * @return reference to the parent {@link SokochanGrid}
     */
    SokochanGrid getGrid() {
        return grid;
    }
}
