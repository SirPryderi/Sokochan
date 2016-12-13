package fx;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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
import javafx.stage.Screen;
import javafx.stage.Stage;
import sokochan.Direction;
import sokochan.GridObjects.*;
import sokochan.MapLoader;
import sokochan.SokochanEngine;
import sokochan.SokochanGrid;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.Optional;

/**
 * The entry class for the JavaFX application functioning as GUI for the {@link SokochanEngine} game
 */
public class Main extends Application {
    //<editor-fold desc="JavaFX" default-state="collapsed">
    // JavaFX modules
    private static Scene scene;
    private static Stage primaryStage;

    // JavaFX nodes
    @FXML
    private MenuBar menu;
    @FXML
    private GridPane gameGrid;
    //</editor-fold>

    // Game Vars
    private SokochanEngine engine;
    private int rectangleSize = 20;

    /**
     * Launches the JavaFX application
     *
     * @param args no console arguments used
     */
    public static void main(String[] args) {
        launch(args);
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

    /**
     * Starts the game given a file name. In case of exception will show different dialogs.
     *
     * @param file the game file to load
     */
    private void startGame(File file) {
        try {
            if (file == null) // Loads default map
                engine = new SokochanEngine();
            else
                engine = new SokochanEngine(file);

            initGame();
        } catch (IOException e) {
            showErrorDialog("Unable to access the file.", e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            showErrorDialog("Index out of bound. Irregular grid provided.", e.getMessage());
        } catch (MapLoader.MapLoaderException e) {
            showErrorDialog("Error while parsing the save file.", e.getMessage());
        } catch (Exception e) {
            showExceptionDialog(e);
        }
    }

    /**
     * Initialises the interface and makes the game ready to be played
     */
    private void initGame() {
        draw();
        bindEvents();
        menu.getMenus().get(1).setDisable(false);
        setUndoStatus();
    }

    /**
     * @return The default location to save/load a game
     */
    private File getDefaultLocation() {
        File folder = new File(System.getProperty("user.dir") + "\\src\\maps\\");

        if (!Files.exists(folder.toPath())) {
            folder = new File(System.getProperty("user.dir"));
        }

        return folder;
    }

    /**
     * Draws the grid to the main windows. It removes the main menu in the process.
     */
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

        setUndoStatus();

        primaryStage.sizeToScene();
    }

    /**
     * Binds keyboard event to the game engine
     */
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
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Victory!");
                alert.setHeaderText(null);
                addIcon(alert);
                if (engine.getLevelIndex() + 1 < engine.getLevelsCount()) {

                    alert.setContentText("You completed the game in " + engine.getMovesCount() + " moves!\nPress okay to load the next level.");

                    alert.showAndWait();

                    engine.loadLevel(engine.getLevelIndex() + 1);
                    draw();
                } else {
                    alert.setContentText("You completed the game in " + engine.getMovesCount() + " moves!\n This was the last level in the map, congrats!");

                    alert.showAndWait();

                    primaryStage.close();
                }
            }
        });
    }

    /**
     * Disables or enables the "undo" menu entry
     */
    private void setUndoStatus() {
        ObservableList<MenuItem> items = menu.getMenus().get(1).getItems();

        items.get(0).setDisable(engine.getHistoryElementsCount() == 0);
    }

    //<editor-fold desc="UI Event Handlers" defaultstate="collapsed">

    /**
     * Opens the load game window
     */
    public void loadGameFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Save File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Sokoban File", "*.skb"));

        try {
            fileChooser.setInitialDirectory(getDefaultLocation());
        } finally {
            File file = fileChooser.showOpenDialog(primaryStage);

            if (file != null)
                startGame(file);
        }
    }

    /**
     * Resets the level to the initial status
     */
    public void resetLevel() {
        if (engine == null)
            return;

        engine.loadLevel(engine.getLevelIndex());
        draw();
    }

    /**
     * Opens the windows to save the game
     */
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
                    showErrorDialog("Unable to access the file.", e.getMessage());
                } catch (Exception e) {
                    showExceptionDialog(e);
                }
            }
        }
    }

    /**
     * Overloaded method for {@code startGame(File file)}, that loads the default game.
     */
    public void startGame() {
        startGame(null);
    }

    /**
     * Cancel the last move made by the player
     */
    public void undo() {
        if (engine == null)
            return;

        engine.undo();
        draw();
    }

    /**
     * Quits the game, after asking for confirmation.
     */
    public void closeGame() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        addIcon(alert);
        alert.setHeaderText(null);
        alert.setTitle("Quit Game");
        addIcon(alert);
        alert.setContentText("Are you sure you want to quit?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            primaryStage.close();
        }
    }

    /**
     * Dialog to display for non implemented functions
     */
    public void notImplemented() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        addIcon(alert);
        alert.setTitle("Not implemented");
        alert.setHeaderText(null);
        alert.setContentText("Sorry, this function will be available in the following versions.");

        alert.showAndWait();
    }

    /**
     * Opens the dialog that shows info about the level
     */
    public void showLevelInfo() {
        if (engine == null)
            return;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        addIcon(alert);
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

    /**
     * Opens the dialog to change the size of the grid elements
     */
    public void setTileSize() {
        boolean error = false;

        do {
            TextInputDialog dialog = new TextInputDialog(String.valueOf(getRectangleSize()));
            addIcon(dialog);
            dialog.setTitle("Tile size");
            dialog.setHeaderText("Set the tile size");

            if (error)
                dialog.setContentText("Invalid input provided\n");
            else
                dialog.setContentText("");

            Optional<String> result = dialog.showAndWait();

            try {
                result.ifPresent(value -> setRectangleSize(Integer.valueOf(value)));

                error = false;
            } catch (IllegalArgumentException e) {
                showErrorDialog("Tile size too big", "The specified size is too big to fit on screen. Please, select a size smaller than " + (getScreenSmallerBound() + 1) + ".");
                error = true;

            } catch (Exception e) {
                error = true;
            }
        } while (error);

        draw();
    }

    private int getScreenSmallerBound() {
        Screen screen = Screen.getPrimary();

        int width = (int) screen.getBounds().getWidth();
        int height = (int) screen.getBounds().getHeight();

        int margin = 50;

        if (width > height)
            return (height - margin) / engine.getSokochanGrid().Y_SIZE;

        else
            return (width - margin) / engine.getSokochanGrid().X_SIZE;
    }

    /**
     * Opens the about dialog, showing the author and license.
     */
    public void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Sokochan");
        alert.setHeaderText("About Sokochan");
        addIcon(alert);

        String url = "https://github.com/SirPryderi/Sokochan";

        GridPane expContent = new GridPane();

        Hyperlink gitHubPageLink = new Hyperlink(url);

        gitHubPageLink.setOnAction(event -> getHostServices().showDocument(url));

        expContent.add(new Label("Copyright © 2016 Vittorio Iocolano"), 0, 0);
        expContent.add(gitHubPageLink, 0, 1);
        expContent.add(new Label("This software is distributed under GPLv3 Licence"), 0, 2);

        alert.getDialogPane().setContent(expContent);

        alert.showAndWait();
    }
    //</editor-fold>

    //<editor-fold desc="Dialogs">

    /**
     * Displays the exception stack trace and message
     *
     * @param ex the exception to show
     */
    private void showExceptionDialog(Exception ex) {
        // credit: http://code.makery.ch/blog/javafx-dialogs-official/

        ex.printStackTrace();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        addIcon(alert);
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

    /**
     * Simple error dialog
     *
     * @param error   the title of the error
     * @param message full message of the error
     */
    private void showErrorDialog(String error, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        addIcon(alert);
        alert.setTitle("Error");
        alert.setHeaderText(error);
        alert.setContentText(message);

        alert.showAndWait();
    }

    /**
     * Adds the default icon to a {@link Dialog}
     *
     * @param dialog dialog to add the icon to
     */
    private void addIcon(Dialog dialog) {
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(primaryStage.getIcons().get(0));
    }
    //</editor-fold>

    //<editor-fold desc="Getters and Setters">
    private int getRectangleSize() {
        return rectangleSize;
    }

    private void setRectangleSize(int rectangleSize) {
        if (rectangleSize <= getScreenSmallerBound()) {
            this.rectangleSize = rectangleSize;
        } else
            throw new IllegalArgumentException("The rectangle size must be smaller than " + getScreenSmallerBound() + " to fit on screen.");
    }
    //</editor-fold>
}
