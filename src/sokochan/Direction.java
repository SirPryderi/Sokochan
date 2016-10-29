package sokochan;

/**
 * An enumeration containing all the possible movements in the {@link SokochanGrid}.
 *
 * Created by Vittorio on 05-Oct-16.
 */
public enum Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    static {
        NORTH.opposite = SOUTH;
        SOUTH.opposite = NORTH;
        EAST.opposite = WEST;
        WEST.opposite = EAST;
    }

    private Direction opposite;

    public Direction getOppositeDirection() {
        return opposite;
    }
}
