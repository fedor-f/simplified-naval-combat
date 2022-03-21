package battleship;

import java.util.Scanner;

public class Main {
    /**
     * The number of all ships in the ocean.
     */
    static int numberOfShips = 0;

    /**
     * The number of shots made.
     */
    static int shotCounter = 0;

    /**
     * The number of torpedoes.
     */
    static int torpedoes = 0;

    private static void message() {
        System.out.println("""
                Choose an option by only entering 1 or 2:\s
                1.Start the game.
                2.Quit the game.""");
    }

    public static void main(String[] args) {
        System.out.println("Welcome to Battleships!");
        Scanner in = new Scanner(System.in);
        start(in);
    }

    /**
     * Start the game.
     * @param in an instance of a class Scanner.
     */
    private static void start(Scanner in) {
        boolean flag = true;
        int option;
        while (flag) {
            message();

            // Enter the option.
            while (!in.hasNextInt()) {
                System.out.println("Incorrect input. Try again.");
                in.next();
            }
            option = in.nextInt();

            // Do the next step depending on the chosen option.
            switch (option) {
                case 1 -> game(in);
                case 2 -> {
                    flag = false;
                    System.out.println("Bye!");
                }
            }
        }
    }

    /**
     * User enters the number of all types of ships.
     * @param in an instance of class Scanner to input data.
     * @param ocean an instance of the game field.
     */
    private static void clarifyShipTypes(Scanner in, Ocean ocean) {
        // Instantiate the ships and enter their quantity.
        Carrier carrier = new Carrier(in);
        Battleship battleship = new Battleship(in);
        Cruiser cruiser = new Cruiser(in);
        Destroyer destroyer = new Destroyer(in);
        Submarine submarine = new Submarine(in);

        // Adding ships in the ocean.
        carrier.addShipsInOcean(ocean);
        battleship.addShipsInOcean(ocean);
        cruiser.addShipsInOcean(ocean);
        destroyer.addShipsInOcean(ocean);
        submarine.addShipsInOcean(ocean);

        // Counting the number of all ships.
        numberOfShips +=
                carrier.getShips() + battleship.getShips() +
                        cruiser.getShips() + destroyer.getShips() + submarine.getShips();
    }

    /**
     * Play the game.
     * @param in an instance of class Scanner to input data.
     */
    private static void game(Scanner in) {
        int rows;
        int columns;

        rows = correctInputOfOceanParameters(in, "rows");

        columns = correctInputOfOceanParameters(in, "columns");

        Ocean ocean = new Ocean(rows, columns);

        clarifyShipTypes(in, ocean);

        if (Ocean.getNumberOfShips() < numberOfShips) {
            System.out.println("The System was unable to arrange all the ships." +
                    " Please try again or you can quit the game.");
        } else if (numberOfShips == 0) {
            System.out.println("The number of ships is 0. Please try again and enter at least one ship.");
        } else {
            switch (enableRecoveryMode(in)) {
                case 1 -> recoveryModeEnabled(in, ocean);
                case 2 -> recoveryModeDisabled(in, ocean);
            }
        }
        Ocean.setNumberOfShips(0);
        numberOfShips = 0;
        torpedoes = 0;
    }

    /**
     * Suggest game options to user when the recovery mode is disabled.
     * @param in an instance of class Scanner to input data.
     * @param ocean the game field.
     */
    private static void recoveryModeDisabled(Scanner in, Ocean ocean) {
        // Choosing the game mode depending on the fact whether the torpedo mode was enabled.
        switch (enableTorpedoMode(in)) {
            case 1 -> {
                torpedoes = enterNumberOfTorpedoes(in);
                playersTurn(in, ocean);
                System.out.printf("Congrats! You won the game with %s shots%n\n", shotCounter);
                shotCounter = 0;
            }
            case 2 -> {
                playersTurn(in, ocean);
                System.out.printf("Congrats! You won the game with %s shots%n\n", shotCounter);
                shotCounter = 0;
            }
        }
    }

    /**
     * Suggest game options to user when the recovery mode is enabled.
     * @param in an instance of class Scanner to input data.
     * @param ocean the game field.
     */
    private static void recoveryModeEnabled(Scanner in, Ocean ocean) {
        // Choosing the game mode depending on the fact whether the torpedo mode was enabled.
        switch (enableTorpedoMode(in)) {
            case 1 -> {
                torpedoes = enterNumberOfTorpedoes(in);
                playersTurnRecovery(in, ocean);
                System.out.printf("Congrats! You won the game with %s shots%n\n", shotCounter);
                shotCounter = 0;
            }
            case 2 -> {
                playersTurnRecovery(in, ocean);
                System.out.printf("Congrats! You won the game with %s shots%n\n", shotCounter);
                shotCounter = 0;
            }
        }
    }

    /**
     * Choosing whether to turn on the recovery mode.
     * @param in an instance of class Scanner to input data.
     * @return the option in a list of suggestions.
     */
    private static int enableRecoveryMode(Scanner in) {
        int value;
        do {
            System.out.println("""
                    Do you wish to enable recovery mode?
                    1. Yes.
                    2. No.""");
            while (!in.hasNextInt()) {
                System.out.println("Incorrect input. Try again.");
                in.next();
            }
            value = in.nextInt();
        } while (value <= 0 || value > 2);
        return value;
    }

    /**
     * Choosing whether to turn on the torpedo mode.
     * @param in an instance of class Scanner to input data.
     * @return the option in a list of suggestions.
     */
    private static int enableTorpedoMode(Scanner in) {
        int value;
        do {
            System.out.println("""
                    Do you wish to enable torpedo mode?
                    1. Yes.
                    2. No.""");
            while (!in.hasNextInt()) {
                System.out.println("Incorrect input. Try again.");
                in.next();
            }
            value = in.nextInt();
        } while (value <= 0 || value > 2);
        return value;
    }

    /**
     * Turns of a player when the recovery mode is disabled.
     * @param in an instance of class Scanner to input data.
     * @param ocean the game field.
     */
    private static void playersTurn(Scanner in, Ocean ocean) {
        ocean.display();
        do {
            if (torpedoes < 1) {
                playWithNoTorpedoes(in, ocean);
            } else {
                switch (useTorpedoOrNot(in)) {
                    case 1 -> playWithTorpedoes(in, ocean);
                    case 2 -> playWithNoTorpedoes(in, ocean);
                }
            }
        } while (ocean.checkForShips());
    }

    /**
     * Turns of a player when the recovery mode is enabled.
     * @param in an instance of class Scanner to input data.
     * @param ocean the game field.
     */
    private static void playersTurnRecovery(Scanner in, Ocean ocean) {
        ocean.display();
        do {
            if (torpedoes < 1) {
                playRecoveryMode(in, ocean);
            } else {
                switch (useTorpedoOrNot(in)) {
                    case 1 -> playRecoveryModeWithTorpedoes(in, ocean);
                    case 2 -> playRecoveryMode(in, ocean);
                }
            }
        } while (ocean.checkForShips());
    }

    /**
     * Shot the ocean when recovery mode is on.
     * @param in an instance of class Scanner to input data.
     * @param ocean the game field.
     */
    private static void playRecoveryMode(Scanner in, Ocean ocean) {
        int row;
        int column;
        // Input a cell to shoot.
        row = correctInputOfFiringCells(in, "Row index");
        column = correctInputOfFiringCells(in, "Column index");

        // Check if a chosen cell exists.
        try {
            ocean.hitRecovery(row, column);
        } catch (Exception e) {
            System.out.println("The cell does not exist. Try again.");
            return;
        }
        shotCounter++;
        ocean.display();
    }

    /**
     * Shot the ocean when recovery mode is on and the torpedo mode is on.
     * @param in an instance of class Scanner to input data.
     * @param ocean the game field.
     */
    private static void playRecoveryModeWithTorpedoes(Scanner in, Ocean ocean) {
        int column;
        int row;

        torpedoes--;

        // Input a cell to shoot.
        row = correctInputOfFiringCells(in, "Row index");
        column = correctInputOfFiringCells(in, "Column index");

        // Check if a chosen cell exists.
        try {
            ocean.hitWithTorpedoInRecoveryMode(row, column);
        } catch (Exception e) {
            System.out.println("The cell does not exist. Try again.");
            torpedoes++;
            return;
        }
        shotCounter++;
        ocean.display();
    }

    /**
     * Shot the ocean when recovery mode is off and the torpedo mode is on.
     * @param in an instance of class Scanner to input data.
     * @param ocean the game field.
     */
    private static void playWithTorpedoes(Scanner in, Ocean ocean) {
        int column;
        int row;
        torpedoes--;

        // Input a cell to shoot.
        row = correctInputOfFiringCells(in, "Row index");
        column = correctInputOfFiringCells(in, "Column index");

        // Check if a chosen cell exists.
        try {
            ocean.hitWithTorpedo(row, column);
        } catch (Exception e) {
            System.out.println("The cell does not exist. Try again.");
            torpedoes++;
            return;
        }
        shotCounter++;
        ocean.display();
    }

    /**
     * Shot the ocean when recovery mode is off and the torpedo mode is off.
     * @param in an instance of class Scanner to input data.
     * @param ocean the game field.
     */
    private static void playWithNoTorpedoes(Scanner in, Ocean ocean) {
        int row;
        int column;

        // Input a cell to shoot.
        row = correctInputOfFiringCells(in, "Row index");
        column = correctInputOfFiringCells(in, "Column index");

        // Check if a chosen cell exists.
        try {
            ocean.hit(row, column);
        } catch (Exception e) {
            System.out.println("The cell does not exist. Try again.");
            return;
        }
        shotCounter++;
        ocean.display();
    }

    /**
     * Choose an option to use torpedo to shoot or not.
     * @param in an instance of class Scanner to input data.
     * @return an option in a suggested list.
     */
    private static int useTorpedoOrNot(Scanner in) {
        int value;
        do {
            System.out.println("""
                    Do you want to use torpedo? Enter only 1 or 2:
                    1. Use torpedo.
                    2. Do not use torpedo.""");

            while (!in.hasNextInt()) {
                System.out.println("Incorrect input. Try again.");
                in.next();
            }
            value = in.nextInt();
        } while (value <= 0 || value > 2);
        return value;
    }

    /**
     * Enter the number of torpedoes.
     * @param in an instance of class Scanner to input data.
     * @return an option in a suggested list.
     */
    private static int enterNumberOfTorpedoes(Scanner in) {
        int value;
        do {
            System.out.println("Enter the number of torpedoes." +
                    " It cannot be less than 1 and greater than the number of all ships");

            while (!in.hasNextInt()) {
                System.out.println("Incorrect input. Try again.");
                in.next();
            }
            value = in.nextInt();
        } while (value <= 0 || value > numberOfShips);
        return value;
    }

    /**
     * Enter the parameters of the ocean.
     * @param in an instance of class Scanner to input data.
     * @param parameter string that puts into a message.
     * @return the length of one of dimensions.
     */
    private static int correctInputOfOceanParameters(Scanner in, String parameter) {
        int value;
        do {
            System.out.printf("Input the positive number of %s. It has to be less than 11: %n", parameter);
            while (!in.hasNextInt()) {
                System.out.println("Incorrect input. Try again.");
                in.next();
            }
            value = in.nextInt();
        } while (value <= 0 || value > 10);
        return value;
    }

    /**
     * Input the coordinate to shoot.
     * @param in an instance of class Scanner to input data.
     * @param parameter string that puts into a message.
     * @return the coordinate of one of dimensions.
     */
    private static int correctInputOfFiringCells(Scanner in, String parameter) {
        int value;
        do {
            System.out.printf("Input the positive number of %s: %n", parameter);
            while (!in.hasNextInt()) {
                System.out.println("Incorrect input. Try again.");
                in.next();
            }
            value = in.nextInt();
        } while (value < 0);
        return value;
    }
}
