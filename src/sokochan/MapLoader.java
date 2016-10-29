package sokochan;

import java.awt.*;
import java.io.IOException;
import java.nio.file.FileSystems;
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

    public MapLoader(String file) throws IOException {
        levels = new ArrayList<>();

        Path file1 = FileSystems.getDefault().getPath(file);

        Stream<String> lines = Files.lines(file1);

        int line = 0;

        Iterator i = lines.iterator();
        while (i.hasNext()) {
            String s = (String) i.next();

            if (s.contains("MapSetName: ")) {
                setName(s.replace("MapSetName: ", ""));
            } else if (s.contains("LevelName: ")) {

                String name = s.replace("LevelName: ", "");
                levels.add(new Level(name));
            } else if (!s.isEmpty()) {
                int lastIndex = levels.size() - 1;
                if (lastIndex > -1)
                    levels.get(lastIndex).content.add(s);
            }
            line++;
        }
    }

    public static void main(String args[]) throws IOException {
        new MapLoader("C:\\Users\\Vittorio\\Projects\\JavaProjects\\Sokochan\\src\\sokochan\\GridObjects/SampleGame.skb");
    }

    List<Level> getLevels() {
        return levels;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public int getNumberOfLevels() {
        return levels.size();
    }

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

}
