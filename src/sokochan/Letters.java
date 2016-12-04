package sokochan;

/**
 * An enumeration use to store the letters used in save files to represent a tile
 * Created by Vittorio on 04/12/2016.
 */
public enum Letters {
    /**
     * {@link sokochan.GridObjects.Wall}
     */
    WALL('w'),
    /**
     * {@link sokochan.GridObjects.Tile}
     */
    TILE(' '),
    /**
     * {@link sokochan.GridObjects.Crate}
     */
    CRATE('c'),
    /**
     * {@link sokochan.GridObjects.WarehouseKeeper}
     */
    WAREHOUSE_KEEPER('s'),

    /**
     * {@link sokochan.GridObjects.Diamond}
     */
    DIAMOND('d'),

    /**
     * {@link sokochan.GridObjects.Crate} on {@link sokochan.GridObjects.Diamond}
     */
    CRATE_ON_DIAMOND('p'),

    /**
     * {@link sokochan.GridObjects.WarehouseKeeper} on {@link sokochan.GridObjects.Diamond}
     */
    WAREHOUSE_KEEPER_ON_DIAMOND('r');

    private char code;

    /**
     * @param code letter code of the object
     */
    Letters(char code) {
        this.code = code;
    }

    /**
     * Converts a character to a {@link Letters} object, returs {@code null} if not found
     *
     * @param c the
     * @return the Letter or null if failed to parse
     */
    public static Letters valueOf(char c) {
        c = Character.toLowerCase(c);

        for (Letters l : values()) {
            if (c == l.getCode())
                return l;
        }

        return null;
    }

    public final char getCode() {
        return code;
    }

}
