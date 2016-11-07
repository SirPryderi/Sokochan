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
public class MapLoader {
    private final List<Level> levels;
    private String name;
    private Level inProgressLevel;
    private int inProgressLevelIndex;

    public MapLoader() {
        levels = new ArrayList<>();
    }

    void loadMap() throws MapLoaderException, NullPointerException {
        InputStream systemResourceAsStream = ClassLoader.getSystemResourceAsStream("maps/SampleGame.skb");

        Reader targetReader = new InputStreamReader(systemResourceAsStream);

        BufferedReader reader = new BufferedReader(targetReader);

        loadMap(reader.lines());
    }

    void loadMap(File file) throws IOException, MapLoaderException {
        Path file1 = file.toPath();

        Stream<String> lines = Files.lines(file1);

        loadMap(lines);
    }

    private void loadMap(Stream<String> lines) throws MapLoaderException {
        Iterator<String> i = lines.iterator();

        boolean isInProgressLevel = false;

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
    }

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
        // The normal level has been saved now

        // Now append the progress of the current level
        builder.append("CurrentLevel: ");
        builder.append(engine.getLevelIndex());
        builder.append("\n");

        engine.getSokochanGrid().forEach(tileGridObject -> {
            MovableGridObject content = tileGridObject.getPlacedObject();

            if (tileGridObject instanceof Wall)
                builder.append('w');
            else if (tileGridObject instanceof Diamond) {
                if (content == null)
                    builder.append('d');
                else if (content instanceof Crate)
                    builder.append('p');
                else if (content instanceof WarehouseKeeper)
                    builder.append('r');
            } else {
                if (content == null)
                    builder.append(' ');
                else if (content instanceof Crate)
                    builder.append('c');
                else if (content instanceof WarehouseKeeper)
                    builder.append('s');
            }

            // If it's the last in the row, send a carriage return
            if (tileGridObject.getPosition().getX() == engine.getSokochanGrid().X_SIZE - 1)
                builder.append('\n');
        });

        Files.write(file.toPath(), builder.toString().getBytes());
    }

    //<editor-fold desc="Getters and Setters" defaultstate="collapsed">
    List<Level> getLevels() {
        return levels;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    Level getInProgressLevel() {
        return inProgressLevel;
    }

    int getInProgressLevelIndex() {
        return inProgressLevelIndex;
    }

    public int getNumberOfLevels() {
        return levels.size();
    }
    //</editor-fold>

    public class Level implements Iterable {
        private final String name;

        private final List<String> content;

        public Level(String name) {
            this.name = name;
            content = new ArrayList<>();
        }

        public String getName() {
            return name;
        }

        public List<String> getContent() {
            return content;
        }

        public int getX() {
            // Maybe check that they are of the same length
            return content.get(0).length();
        }

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

            private MapIterator() {
                X = getX();
                Y = getY();
            }

            @Override
            public boolean hasNext() {
                return y < Y - 1 || x < X - 1;
            }

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

    public class MapLoaderException extends Exception {
        public MapLoaderException(String message) {
            super(message);
        }
    }
}
