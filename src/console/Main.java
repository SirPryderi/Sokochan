package console;

import sokochan.Direction;
import sokochan.GridObjects.*;
import sokochan.SokochanEngine;
import sokochan.SokochanGrid;

import java.io.IOException;
import java.util.Scanner;

/**
 * A console implementation for the {@link SokochanEngine} used for early testing of the project.
 * <p>
 * Created by Vittorio on 12-Oct-16.
 */
class Main {

    public static void main(String args[]) {
        SokochanEngine engine = null;
        try {
            engine = new SokochanEngine();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.err.println("Provided game file does not exist. Exiting now...");
            System.exit(0);
        }

        printGrid(engine.getSokochanGrid());

        boolean exit = false;

        while (!exit) {
            Scanner scanner = new Scanner(System.in);
            String choice = scanner.nextLine();

            switch (choice) {
                case "w":
                    engine.movePlayer(Direction.NORTH);
                    break;
                case "a":
                    engine.movePlayer(Direction.WEST);
                    break;
                case "s":
                    engine.movePlayer(Direction.SOUTH);
                    break;
                case "d":
                    engine.movePlayer(Direction.EAST);
                    break;
                case "q":
                    exit = true;
            }

            if (engine.isComplete()) {
                System.out.println("Game completed in " + engine.getMovesCount() + " moves.");
                exit = true;
            }

            printGrid(engine.getSokochanGrid());
        }
    }

    private static void printGrid(SokochanGrid grid) {
        clearScreen();

        for (int y = 0; y < grid.Y_SIZE; y++) {
            for (int x = 0; x < grid.X_SIZE; x++) {
                MovableGridObject object = grid.getGridObject(x, y);
                TileGridObject tile = grid.getTile(x, y);

                char c = ' ';

                if (object instanceof WarehouseKeeper)
                    c = 9824;
                else if (object instanceof Crate)
                    c = 9632;
                else if (tile instanceof Wall)
                    c = '\u20ac';
                else if (tile instanceof Diamond)
                    c = 9830;


                System.out.print("[" + c + "]");
                //System.out.print(' ');
            }
            System.out.print("\n");
        }
    }

    private static void clearScreen() {
        try {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                Runtime.getRuntime().exec("cls");

            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (final Exception e) {
            //System.out.println("\n\n\n\n\n");
        }
    }
}


