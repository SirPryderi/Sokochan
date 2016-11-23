package sokochan.GridObjects;

import sokochan.SokochanGrid;

import java.awt.*;

/**
 * An abstract class for a tile, i.e. an object that is directly part of the {@link TileGridObject} in the {@link SokochanGrid}.
 * <p>
 * May contain a reference to the object placed on the tile, or {@code null} if its empty.
 * <p>
 * Created by Vittorio on 12-Oct-16.
 */
public abstract class TileGridObject extends GridObject {
    private final boolean WALKABLE;
    private MovableGridObject placedObject = null;

    /**
     * Creates and places a {@link TileGridObject} in the {@link SokochanGrid}
     *
     * @param grid       parent grid
     * @param position   where to put the crate
     * @param isWalkable whether object can be placed upon that tile
     */
    TileGridObject(SokochanGrid grid, Point position, boolean isWalkable) {
        super(grid, position);
        this.WALKABLE = isWalkable;
    }

    /**
     * Checks if a {@link TileGridObject} is walkable.
     * <p>A tile is walkable if:</p>
     * <ul>
     * <li>Is not a {@link sokochan.GridObjects.Wall}</li>
     * <li>It's inside the grid boundaries</li>
     * <li>Has no {@link MovableGridObject} on top of it</li>
     * </ul>
     *
     * @return {@code false} if out of bound or non walkable
     * or {@code true} if walkable, i.e. an empty tile
     */
    public boolean isWalkable() {
        return WALKABLE;
    }

    /**
     * Returns the object placed on the current tile. May be {@code null} if it is empty
     *
     * @return the object | {@code null} if empty
     */
    public MovableGridObject getPlacedObject() {
        return placedObject;
    }

    /**
     * Will attempt to place an object to a tile. It will fail if the tile has already something or is non-walkable.
     *
     * @param object the object to place
     * @return {@code true} if successfully placed | {@code false} if the tile is not free
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean setPlacedObject(MovableGridObject object) {
        if (!isWalkable() && object != null) {
            return false;
        }
        this.placedObject = object;
        return true;
    }

    @Override
    protected void place() {
        this.getGrid().setGridTile(this.getPosition(), this);
    }
}
