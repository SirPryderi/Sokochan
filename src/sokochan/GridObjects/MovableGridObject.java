package sokochan.GridObjects;

import sokochan.Direction;
import sokochan.SokochanGrid;

import java.awt.*;

/**
 * Another abstract class, that will provide methods for moving {@link GridObject}s.
 * <p>
 * Created by Vittorio on 05-Oct-16.
 */
public abstract class MovableGridObject extends GridObject {
    /**
     * This constructor will place the object on the grid
     *
     * @param grid     A grid reference, should be not null
     * @param Position A valid position where the object should be placed
     */
    MovableGridObject(SokochanGrid grid, Point Position) {
        super(grid, Position);
    }

    /**
     * Moves an object in the {@link SokochanGrid},
     * returning {@code true} or {@code false} according to the success of the movement
     *
     * @param direction where the object is moving
     * @return {@code true} if moved | {@code false} if not moved
     */
    public boolean move(Direction direction) {
        if (!canMove(direction)) {
            return false;
        }

        remove();

        Point newPosition = getDisplacement(direction);

        setPosition(newPosition);

        place();

        return true;
    }

    /**
     * Removes and object from the {@link SokochanGrid}
     */
    private void remove() {
        getGrid().removeGridObject(getPosition());
    }

    /**
     * Gives the position the object will be, if successfully moved in one direction
     *
     * @param direction a valid direction where moving the object
     * @return end position after successfully moving in direction.
     */
    private Point getDisplacement(Direction direction) {
        Point position = new Point(getPosition().x, getPosition().y);

        Point translation = new Point(0, 0);

        switch (direction) {
            case NORTH:
                translation.y = -1;
                break;
            case EAST:
                translation.x = 1;
                break;
            case SOUTH:
                translation.y = 1;
                break;
            case WEST:
                translation.x = -1;
                break;
        }

        position.translate(translation.x, translation.y);

        return position;
    }

    /**
     * Tells whether an object can be moved in a given {@link Direction}
     *
     * @param direction where moving
     * @return true if can move | false if cannot move
     */
    private boolean canMove(Direction direction) {
        try {
            return getNeighbourTile(direction).isWalkable();
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * Gives the object next to another, given a {@link Direction}. May return {@code null} if not found.
     *
     * @param direction a valid direction
     * @return the neighbour object in a current direction | {@code null} if not found
     */
    MovableGridObject getNeighbour(Direction direction) {
        return getGrid().getGridObject(getDisplacement(direction));
    }

    /**
     * Gives the tile next to the current {@link MovableGridObject}, given a {@link Direction}. May return {@code null} if not found.
     *
     * @param direction a valid direction
     * @return the neighbour tile in a current direction | {@code null} if not found
     */
    @SuppressWarnings("WeakerAccess")
    TileGridObject getNeighbourTile(Direction direction) {
        return getGrid().getTile(getDisplacement(direction));
    }

    // not used yet
//    TileGridObject[] getNeighbourTiles() {
//        TileGridObject[] neighbours = new TileGridObject[4];
//
//        int i = 0;
//        for (Direction d : Direction.values()) {
//            neighbours[i] = getNeighbourTile(d);
//            i++;
//        }
//
//        return neighbours;
//    }

    @Override
    protected void place() {
        getGrid().getTile(getPosition()).setPlacedObject(this);
    }
}
