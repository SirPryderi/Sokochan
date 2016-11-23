package sokochan;

import sokochan.GridObjects.MovableGridObject;
import sokochan.GridObjects.Tile;
import sokochan.GridObjects.TileGridObject;

import java.awt.*;
import java.util.Iterator;

/**
 * This class wraps a two dimensional array of {@link Tile}s
 * (that will be implemented as an array of arrays in Java, because of the lack of thereof)
 * and provides a series of methods to retrieve {@link sokochan.GridObjects.GridObject}s and {@link Tile}s placed in a position.
 * The class implements {@link Iterable}, in order to simplify the reading of all the objects,
 * for such purpose it contains an inner class called {@link GridIterator}.
 * Note: in the {@link SokochanGrid} are placed only {@link Tile}s,
 * {@link sokochan.GridObjects.Crate}s and {@link sokochan.GridObjects.WarehouseKeeper} are paced inside the {@link Tile}.
 * Created by Vittorio on 05-Oct-16.
 */
public final class SokochanGrid implements Iterable<TileGridObject> {
    /**
     * The width of the grid
     */
    public final int X_SIZE;

    /**
     * the height of the grid
     */
    public final int Y_SIZE;

    private final TileGridObject[][] tileGridObjects;

    /**
     * Constructor for the grid. Creates a grid of {@code null}s
     *
     * @param x width of the grid
     * @param y height of the grid
     */
    SokochanGrid(int x, int y) {
        X_SIZE = x;
        Y_SIZE = y;
        tileGridObjects = new TileGridObject[X_SIZE][Y_SIZE];
    }

    /**
     * * Gives the tile at a given position
     *
     * @param position a {@link Point} representing the position
     * @return The tile contained in a position or {@code null} if out of bound
     */
    public TileGridObject getTile(Point position) {
        return getTile(position.x, position.y);
    }

    /**
     * Gives the tile at a given position
     *
     * @param x position
     * @param y position
     * @return The tile contained in a position or {@code null} if out of bound
     */
    public TileGridObject getTile(int x, int y) {
        if (x < 0 || x >= X_SIZE || y < 0 || y >= Y_SIZE) {
            return null;
        }
        return tileGridObjects[x][y];
    }

    /**
     * Gives the object placed on a {@link TileGridObject} at a given position
     *
     * @param x position
     * @param y position
     * @return the object placed on a tile at the given position
     * @throws NullPointerException if there there is not {@link TileGridObject} at the give position
     */
    public MovableGridObject getGridObject(int x, int y) {
        if (x < 0 || x >= X_SIZE || y < 0 || y >= Y_SIZE) {
            return null;
        }
        return tileGridObjects[x][y].getPlacedObject();
    }

    /**
     * Gives the object placed on a {@link TileGridObject} at a given position
     *
     * @param position the given position
     * @return the object placed on a tile at the given position
     * @throws NullPointerException if there there is no {@link TileGridObject} at the give position
     */
    public MovableGridObject getGridObject(Point position) {
        return getGridObject(position.x, position.y);
    }

    /**
     * Removes an {@link MovableGridObject} on a {@link sokochan.GridObjects.GridObject} at the given position.
     *
     * @param position position
     *                 {@code false} if out of grid bound or already occupied
     * @throws NullPointerException if there there is no {@link TileGridObject} at the give position
     */
    public void removeGridObject(Point position) {
        if (!(position.x < 0 || position.x >= X_SIZE || position.y < 0 || position.y >= Y_SIZE))
            tileGridObjects[position.x][position.y].setPlacedObject(null);
    }

    /**
     * Sets a {@link TileGridObject} at a given position in the {@link SokochanGrid}
     *
     * @param position the position {@link Point}
     * @param tile     the tile to place
     */
    public void setGridTile(Point position, TileGridObject tile) {
        tileGridObjects[position.x][position.y] = tile;
    }

    /**
     * Populates the whole grid with empty {@link Tile}
     */
    void populateWithTiles() {
        GridIterator iterator = iterator();
        while (iterator.hasNext()) {
            iterator.next();
            new Tile(this, iterator.getPosition());
        }
    }

    //<editor-fold desc="Iterator" defaultstate="collapsed">
    @Override
    public GridIterator iterator() {
        return new GridIterator();
    }

    /**
     * An iterator class that will iterate every {@link TileGridObject} in the {@link SokochanGrid}
     * getPosition can be used to retrieve the current {@link Point} in the {@link SokochanGrid}
     */
    private class GridIterator implements Iterator<TileGridObject> {
        private int x = -1;
        private int y = 0;

        @Override
        public boolean hasNext() {
            return !(x == X_SIZE - 1 && y == Y_SIZE - 1);
        }

        @Override
        public TileGridObject next() {
            increment();
            return tileGridObjects[x][y];
        }

        /**
         * Gives the current coordinates of the iterator
         *
         * @return the {@link Point} representing the current coordinates
         */
        public Point getPosition() {
            return new Point(x, y);
        }

        /**
         * Increments the position in the grid by one, moving to the next row if the column is over
         */
        private void increment() {
            if (x < X_SIZE - 1) {
                x++;
            } else {
                x = 0;
                y++;
            }
        }
    }
    //</editor-fold>
}
