package sokochan;

import sokochan.GridObjects.*;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Loads levels from a *.skb file and provides them to the {@link SokochanEngine}
 * Created by Vittorio on 25-Oct-16.
 */
public final class MapLoader {
    private final List<Level> levels;
    private String name;
    private Level inProgressLevel;
    private int inProgressLevelIndex;
    private boolean mapLoaded;

    /**
     * Default constructor for the MapLoader
     */
    public MapLoader() {
        levels = new ArrayList<>();
    }

    /**
     * Loads the default game into the {@link MapLoader}
     *
     * @throws MapLoaderException if the SaveFile is invalid
     * @throws IOException        if the file is not found
     */
    void loadMap() throws MapLoaderException, IOException {
        InputStream systemResourceAsStream = ClassLoader.getSystemResourceAsStream("maps/SampleGame.skb");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(systemResourceAsStream));

        loadMap(bufferedReader.lines());

        bufferedReader.close();
    }

    /**
     * Loads a SaveFile into the {@link MapLoader}
     *
     * @param file path to the SaveFile
     * @throws IOException        if the file is not found
     * @throws MapLoaderException if the SaveFile is invalid
     */
    void loadMap(File file) throws IOException, MapLoaderException {
        Path file1 = file.toPath();

        Stream<String> lines = Files.lines(file1);

        loadMap(lines);
    }

    /**
     * Loads a game given a {@link Stream} {@link String}s, for each line.
     *
     * @param lines A Stream containing a line in each string
     * @throws MapLoaderException if the SaveFile is invalid
     */
    private void loadMap(Stream<String> lines) throws MapLoaderException {
        mapLoaded = false;

        Iterator<String> i = lines.iterator();

        boolean isInProgressLevel = false;

        // I think I have no clue of what is happening here anymore
        // Just the result of poor planning, I guess. I hope you like spaghetti.
        while (i.hasNext()) {
            String s = i.next();

            if (s.contains("MapSetName: ")) {
                setName(s.replace("MapSetName: ", ""));
            } else if (s.contains("LevelName: ")) {
                String name = s.replace("LevelName: ", "");
                levels.add(new Level(name));
            } else if (s.contains("CurrentLevel: ")) {
                try {
                    inProgressLevelIndex = Integer.valueOf(s.replace("CurrentLevel: ", ""));
                    isInProgressLevel = true;
                    inProgressLevel = new Level(levels.get(inProgressLevelIndex).getName());
                } catch (NumberFormatException e) {
                    throw new MapLoaderException("Invalid in progress index provided");
                }
            } else if (!s.isEmpty()) {
                Level level;

                if (isInProgressLevel) {
                    level = inProgressLevel;
                } else {
                    int lastIndex = levels.size() - 1;
                    if (lastIndex > -1)
                        level = levels.get(lastIndex);
                    else {
                        level = null;
                    }
                }

                if (level != null)
                    level.content.add(s);
            }
        }

        mapLoaded = true;
    }

    /**
     * Saves the current state of the {@link SokochanEngine} into a SaveFile
     *
     * @param file   the path where to save the game
     * @param engine the {@link SokochanEngine}, to save the state from
     * @throws IOException if the file is not accessible
     */
    void saveMap(File file, SokochanEngine engine) throws IOException {
        StringBuilder builder = new StringBuilder();

        builder.append("MapSetName: ");
        builder.append(engine.getMapName());
        builder.append("\n");

        for (Level level : engine.getLevels()) {
            builder.append("LevelName: ");
            builder.append(level.getName());
            builder.append("\n");
            level.getContent().forEach(s -> {
                builder.append(s);
                builder.append("\n");
            });
            builder.append("\n");
        }
        // The normal levels have been saved now

        // Now append the progress of the current level
        builder.append("CurrentLevel: ");
        builder.append(engine.getLevelIndex());
        builder.append("\n");

        // Foreach tile in the grid
        engine.getSokochanGrid().forEach(tileGridObject -> {
            MovableGridObject content = tileGridObject.getPlacedObject();

            if (tileGridObject instanceof Wall)
                builder.append(Letters.WALL.getCode());
            else if (tileGridObject instanceof Diamond) {
                if (content == null)
                    builder.append(Letters.DIAMOND.getCode());
                else if (content instanceof Crate)
                    builder.append(Letters.CRATE_ON_DIAMOND.getCode());
                else if (content instanceof WarehouseKeeper)
                    builder.append(Letters.WAREHOUSE_KEEPER_ON_DIAMOND.getCode());
            } else {
                if (content == null)
                    builder.append(Letters.TILE.getCode());
                else if (content instanceof Crate)
                    builder.append(Letters.CRATE.getCode());
                else if (content instanceof WarehouseKeeper)
                    builder.append(Letters.WAREHOUSE_KEEPER.getCode());
            }

            // If it's the last in the row, send a new line
            if (tileGridObject.getPosition().getX() == engine.getSokochanGrid().X_SIZE - 1)
                builder.append('\n');
        });

        // At last write the file
        Files.write(file.toPath(), builder.toString().getBytes());
    }

    /**
     * @return all levels in a map
     */
    //<editor-fold desc="Getters and Setters" defaultstate="collapsed">
    List<Level> getLevels() {
        return levels;
    }

    /**
     * @return the name of the map as specified in the save file
     */
    public String getName() {
        return name;
    }

    /**
     * @param name sets the name of the map
     */
    private void setName(String name) {
        this.name = name;
    }

    /**
     * @return gets the level that has been saved while being played, in order to restore it's status when reloaded
     */
    Level getInProgressLevel() {
        return inProgressLevel;
    }

    /**
     * @return returns the index of the level that has been saved as 'in progress'
     */
    int getInProgressLevelIndex() {
        return inProgressLevelIndex;
    }

    /**
     * @return whether the map has been loaded
     */
    boolean isMapLoaded() {
        return mapLoaded;
    }
    //</editor-fold>

    //<editor-fold desc="Level" defaultstate="collapsed">

    /**
     * An object representing a single level in a Map
     */
    public class Level implements Iterable {
        private final String name;

        private final List<String> content;

        /**
         * Creates a new level given the name. Will instantiate an array of {@link String}s
         *
         * @param name of the level
         */
        public Level(String name) {
            this.name = name;
            content = new ArrayList<>();
        }

        /**
         * @return the name of the level
         */
        public String getName() {
            return name;
        }

        /**
         * @return a string representing the a row of the level
         */
        public List<String> getContent() {
            return content;
        }

        /**
         * @return the width of the level
         */
        public int getX() {
            // TODO Maybe check that they are of the same length
            return content.get(0).length();
        }

        /**
         * @return the height of the level
         */
        public int getY() {
            return content.size();
        }

        @Override
        public MapIterator iterator() {
            return new MapIterator();
        }

        class MapIterator implements Iterator<Character> {
            private final int X;
            private final int Y;

            private int x = -1;
            private int y = 0;

            /**
             * Instantiate the sizes variable
             */
            private MapIterator() {
                X = getX();
                Y = getY();
            }

            @Override
            public boolean hasNext() {
                return y < Y - 1 || x < X - 1;
            }

            /**
             * @return the position at the current iteration
             */
            public Point getPosition() {
                return new Point(x, y);
            }

            @Override
            public Character next() {
                if (x + 1 < X) {
                    x++;
                } else {
                    x = 0;
                    y++;
                }
                return content.get(y).charAt(x);
            }
        }
    }
    //</editor-fold>

    /**
     * An {@link Exception} that is thrown when a loaded map is malformed
     */
    public class MapLoaderException extends Exception {
        /**
         * @param message the message telling what's gone wrong
         */
        MapLoaderException(String message) {
            super(message);
        }
    }
}
