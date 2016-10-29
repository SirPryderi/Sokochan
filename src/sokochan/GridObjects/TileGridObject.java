package sokochan.GridObjects;

import sokochan.SokochanGrid;

import java.awt.*;

/**
 * An abstract class for a tile, i.e. an object that is directly part of the {@link TileGridObject} in the {@link SokochanGrid}.
 * <p>
 * May contain a reference to the object placed on the tile, or <code>null</code> if its empty.
 * <p>
 * Created by Vittorio on 12-Oct-16.
 */
public abstract class TileGridObject extends GridObject {
    private final boolean WALKABLE;
    private MovableGridObject placedObject = null;

    TileGridObject(SokochanGrid grid, Point position, boolean isWalkable) {
        super(grid, position);
        this.WALKABLE = isWalkable;
    }

    public boolean isWalkable() {
        return WALKABLE;
    }

    public MovableGridObject getPlacedObject() {
        return placedObject;
    }

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
