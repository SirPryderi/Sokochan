package sokochan;

import sokochan.GridObjects.MovableGridObject;
import sokochan.GridObjects.Tile;
import sokochan.GridObjects.TileGridObject;

import java.awt.*;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

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
public class SokochanGrid implements Iterable<TileGridObject> {
    public final int X_SIZE, Y_SIZE;

    private final TileGridObject[][] tileGridObjects;

    SokochanGrid(int x, int y) {
        X_SIZE = x;
        Y_SIZE = y;
        tileGridObjects = new TileGridObject[X_SIZE][Y_SIZE];
    }

    public TileGridObject getTile(Point position) {
        return getTile(position.x, position.y);
    }

    public TileGridObject getTile(int x, int y) {
        if (x < 0 || x >= X_SIZE || y < 0 || y >= Y_SIZE) {
            return null;
        }
        return tileGridObjects[x][y];
    }

    public MovableGridObject getGridObject(int x, int y) {
        if (x < 0 || x >= X_SIZE || y < 0 || y >= Y_SIZE) {
            return null;
        }
        return tileGridObjects[x][y].getPlacedObject();
    }

    public MovableGridObject getGridObject(Point position) {
        return getGridObject(position.x, position.y);
    }


    private boolean setGridObject(int x, int y, MovableGridObject object) {
        return !(x < 0 || x >= X_SIZE || y < 0 || y >= Y_SIZE) && tileGridObjects[x][y].setPlacedObject(object);
    }

    public boolean setGridObject(Point position, MovableGridObject object) {
        return setGridObject(position.x, position.y, object);
    }

    private void setGridTile(int x, int y, TileGridObject tile) {
        tileGridObjects[x][y] = tile;
    }

    public void setGridTile(Point position, TileGridObject tile) {
        setGridTile(position.x, position.y, tile);
    }

    private boolean isTileWalkable(int x, int y) {
        if (x < 0 || x >= X_SIZE || y < 0 || y >= Y_SIZE) {
            return false;
        }

        TileGridObject tile = getTile(x, y);

        return tile != null && tile.isWalkable();
    }

    public boolean isTileWalkable(Point pos) {
        return isTileWalkable(pos.x, pos.y);
    }

    @Override
    public Iterator<TileGridObject> iterator() {
        return new GridIterator();
    }

    @SuppressWarnings("WeakerAccess")
    Point randomPosition() {
        // TODO fix potential stack overflow

        int x = ThreadLocalRandom.current().nextInt(0, X_SIZE);
        int y = ThreadLocalRandom.current().nextInt(0, Y_SIZE);

        if (!isTileWalkable(x, y)) {
            return randomPosition();
        }

        return new Point(x, y);
    }

    void populateWithTiles() {
        // Populates the grid with free tiles
        for (int x = 0; x < X_SIZE; x++) {
            for (int y = 0; y < Y_SIZE; y++) {
                new Tile(this, new Point(x, y));
            }
        }
    }

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

        private void increment() {
            if (x < X_SIZE - 1) {
                x++;
            } else {
                x = 0;
                y++;
            }
        }
    }
}
