package fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sokochan.Direction;
import sokochan.GridObjects.*;
import sokochan.SokochanEngine;
import sokochan.SokochanGrid;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.Optional;

public class Main extends Application {
    public static GridPane gameGrid;

    private static Scene scene;
    private static SokochanEngine engine;
    private static Stage primaryStage;

    private static int rectangleSize = 20;

    public static void main(String[] args) {
        launch(args);
    }

    private static int getRectangleSize() {
        return rectangleSize;
    }

    private static void setRectangleSize(int rectangleSize) {
        Main.rectangleSize = rectangleSize;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        scene = new Scene(root);

        Main.primaryStage = primaryStage;
        primaryStage.setTitle("Sokochan Game");
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(event -> {
            closeGame();
            event.consume();
        });

        primaryStage.getIcons().add(new Image("fx/icon.png"));

        primaryStage.show();

        primaryStage.setResizable(false);

        primaryStage.sizeToScene();
    }

    private File getDefaultLocation() {
        File folder = new File(System.getProperty("user.dir") + "\\src\\maps\\");

        if (!Files.exists(folder.toPath())) {
            folder = new File(System.getProperty("user.dir"));
        }

        return folder;
    }

    public void loadGameFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Save File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Sokoban File", "*.skb"));

        try {
            fileChooser.setInitialDirectory(getDefaultLocation());
        } finally {
            File file = fileChooser.showOpenDialog(primaryStage);

            if (file != null)
                initGame(file);
        }
    }

    public void resetLevel() {
        if (engine == null)
            return;

        engine.loadLevel(engine.getLevelIndex());
        draw();
    }

    public void saveGameFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Game File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Sokoban File", "*.skb"));

        try {
            fileChooser.setInitialDirectory(getDefaultLocation());
        } finally {
            File file = fileChooser.showSaveDialog(primaryStage);

            if (file != null) {
                try {
                    engine.saveGame(file);
                } catch (IOException e) {
                    // TODO Alert
                    showExceptionDialog(e);
                } catch (Exception e) {
                    showExceptionDialog(e);
                }
            }
        }
    }

    private void initGame(File file) {
        try {
            engine = new SokochanEngine(file);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Unable to load file");
            // TODO error message
            return;
        } catch (IndexOutOfBoundsException e) {
            // TODO better error
            return;
        } catch (Exception e) {
            showExceptionDialog(e);
            return;
        }
        draw();
        bindEvents();
    }

    public void initGame() {
        try {
            engine = new SokochanEngine();

            draw();
            bindEvents();
        } catch (Exception e) {
            showExceptionDialog(e);
        }
    }

    private void draw() {
        if (engine == null)
            return;

        SokochanGrid grid = engine.getSokochanGrid();

        gameGrid = (GridPane) scene.lookup("#gameGrid");

        gameGrid.getChildren().clear();

        for (int y = 0; y < grid.Y_SIZE; y++) {
            for (int x = 0; x < grid.X_SIZE; x++) {
                MovableGridObject object = grid.getGridObject(x, y);
                TileGridObject tile = grid.getTile(x, y);

                Rectangle r = new Rectangle(rectangleSize, rectangleSize);

                Paint paint = Color.WHITE;

                if (object instanceof WarehouseKeeper) {
                    paint = Color.GREEN;
                } else if (object instanceof Crate) {
                    // Different color if the Crate is on Diamond or not
                    if (((Crate) object).isOnDiamond())
                        paint = Color.INDIANRED;
                    else
                        paint = Color.ORANGE;
                } else if (tile instanceof Wall) {
                    paint = Color.BLACK;
                } else if (tile instanceof Diamond) {
                    paint = Color.RED;
                }

                r.setFill(paint);

                gameGrid.add(r, x, y);
            }
        }

        primaryStage.sizeToScene();
    }

    private void bindEvents() {
        scene.setOnKeyPressed(event -> {
            Direction d;

            switch (event.getCode()) {
                case UP:
                    d = Direction.NORTH;
                    break;
                case DOWN:
                    d = Direction.SOUTH;
                    break;
                case RIGHT:
                    d = Direction.EAST;
                    break;
                case LEFT:
                    d = Direction.WEST;
                    break;
                case W:
                    d = Direction.NORTH;
                    break;
                case S:
                    d = Direction.SOUTH;
                    break;
                case A:
                    d = Direction.WEST;
                    break;
                case D:
                    d = Direction.EAST;
                    break;
                default:
                    return;
            }

            engine.movePlayer(d);

            draw();

            if (engine.isComplete()) {
                if (engine.getLevelIndex() + 1 < engine.getLevelsCount()) {

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Victory!");
                    alert.setHeaderText(null);
                    alert.setContentText("You completed the game in " + engine.getMovesCount() + " moves!\nPress okay to load the next level.");

                    alert.showAndWait();

                    engine.loadLevel(engine.getLevelIndex() + 1);
                    draw();
                } else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Victory!");
                    alert.setHeaderText(null);
                    alert.setContentText("You completed the game in " + engine.getMovesCount() + " moves!\n This was the last level in the map, congrats!");

                    alert.showAndWait();

                    primaryStage.close();
                }
            }
        });
    }

    public void undo() {
        if (engine == null)
            return;

        engine.undo();
        draw();
    }

    public void closeGame() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setTitle("Quit Game");
        alert.setContentText("Are you sure you want to quit?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            primaryStage.close();
        }
    }

    public void notImplemented() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Not implemented");
        alert.setHeaderText(null);
        alert.setContentText("Sorry,this function will be available in the following versions.");

        alert.showAndWait();
    }

    public void showLevelInfo() {
        if (engine == null)
            return;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Level Information");
        alert.setHeaderText(engine.getCurrentLevel().getName());
        alert.setContentText(String.format(
                "Map name: %s" +
                        "\nLevel %d of %d" +
                        "\nSize %dx%d\n" +
                        "\nCrates:\t%d" +
                        "\nOn end:\t%d" +
                        "\nMoves:\t%d" +
                        "\nPushes:\t%d",

                engine.getMapName(),
                engine.getLevelIndex() + 1,
                engine.getLevelsCount(),
                engine.getSokochanGrid().X_SIZE,
                engine.getSokochanGrid().Y_SIZE,

                engine.getCratesCount(),
                engine.getCratesOnDiamondCount(),
                engine.getMovesCount(),
                engine.getPushesCount()
                )
        );

        alert.showAndWait();
    }

    public void setTileSize() {
        boolean error = false;

        do {
            TextInputDialog dialog = new TextInputDialog(String.valueOf(getRectangleSize()));
            dialog.setTitle("Tile size");
            dialog.setHeaderText("Set the tile size");
            if (error)
                dialog.setContentText("Invalid input provided\n");

            Optional<String> result = dialog.showAndWait();

            try {
                result.ifPresent(value -> setRectangleSize(Integer.valueOf(value)));

                error = false;
            } catch (Exception e) {
                error = true;
            }
        } while (error);

        draw();
    }

    @SuppressWarnings("unused")
    private void showExceptionDialog(Exception ex) {
        // credit: http://code.makery.ch/blog/javafx-dialogs-official/

        ex.printStackTrace();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText("An unhandled exception occurred!");
        alert.setContentText("Message: " + ex.getMessage());

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(false);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }
}
