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
    MovableGridObject(SokochanGrid grid, Point position) {
        super(grid, position);
    }

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

    private void remove() {
        getGrid().setGridObject(getPosition(), null);
    }

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

    private boolean canMove(Direction direction) {
        try {
            return getNeighbourTile(direction).isWalkable();
        } catch (NullPointerException e) {
            return false;
        }
    }

    MovableGridObject getNeighbour(Direction direction) {
        return getGrid().getGridObject(getDisplacement(direction));
    }

    TileGridObject getNeighbourTile(Direction direction) {
        return getGrid().getTile(getDisplacement(direction));
    }

    public TileGridObject[] getNeighbourTiles() {
        TileGridObject[] neighbours = new TileGridObject[4];

        int i = 0;
        for (Direction d : Direction.values()) {
            neighbours[i] = getNeighbourTile(d);
        }

        return neighbours;
    }

    @Override
    protected void place() {
        getGrid().getTile(getPosition()).setPlacedObject(this);
    }
}
