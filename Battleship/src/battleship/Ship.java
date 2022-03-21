package battleship;

import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A description of an essence of a ship.
 */
abstract class Ship {
    /**
     * The number of ships of a particular type.
     */
    protected int numberOfShips;

    /**
     * Random ship generation in the ocean.
     * @param length length of the ocean.
     * @param width width of the ocean.
     * @param ocean a game field, i.e. an ocean.
     */
    public abstract void randomShipGenerator(int length, int width, Ocean ocean);

    /**
     * Enter needed number of ships of a particular type.
     * @param in an instance of a Scanner class to input data.
     */
    public abstract void enterNumberOfShips(Scanner in);

    /**
     * A getter of the number of ships.
     * @return the number of ships.
     */
    public int getShips() {
        return numberOfShips;
    }

    /**
     * Adds ships in the ocean.
     * @param ocean an instance of the game field.
     */
    protected void addShipsInOcean(Ocean ocean) {
        for (int i = 0; i < numberOfShips; i++) {
            randomShipGenerator(ocean.getLength(), ocean.getWidth(), ocean);
        }
    }

    /**
     * Tries to populate the ocean with ships.
     * @param X the coordinate of rows.
     * @param Y the coordinate of columns.
     * @param repeatRow the length of a ship in rows.
     * @param repeatColumn the length of a ship in columns.
     * @param length the length of the ocean.
     * @param width the width of the ocean.
     * @param ocean an instance of the game field.
     */
    protected void populate(int X, int Y, int repeatRow, int repeatColumn, int length, int width, Ocean ocean) {
        int iteration = 0;
        // Give 100 tries to populate the ocean.
        while (iteration < 100) {
            // If the generated ship satisfies the adjacency rules then we populate the ocean.
            // Or else we try to populate again by generating new X and Y coordinate.
            if (checkAdjacency(X, Y, repeatRow, repeatColumn, ocean)) {
                for (int i = X; i < X + repeatRow; i++) {
                    for (int j = Y; j < Y + repeatColumn; j++) {
                        ocean.setOcean('X', i, j);
                    }
                }
                makeAdjacencyRestrictions(ocean);
                Ocean.incrementNumberOfShips();
                break;
            } else {
                X = ThreadLocalRandom.current().nextInt(0, length);
                Y = ThreadLocalRandom.current().nextInt(0, width);
            }
            iteration++;
        }
    }

    /**
     * Fence a ship with commas depending on its position in the ocean.
     * @param ocean an instance of the game field.
     */
    private void makeAdjacencyRestrictions(Ocean ocean) {
        // When some ship is populated in the ocean it gets fenced with commas in matrix.
        // It was made for the sake of easy check of adjacency rules.
        for (int i = 0; i < ocean.getArray().length; i++) {
            for (int j = 0; j < ocean.getArray()[0].length; j++) {
                if (ocean.equals(i, j, 'X')) {
                    if (j != 0) {
                        if (!ocean.equals(i, j - 1, 'X')) {
                            ocean.setOcean(',', i, j - 1);
                        }
                    }
                    if (j != ocean.getArray()[0].length - 1) {
                        if (!ocean.equals(i, j + 1, 'X')) {
                            ocean.setOcean(',', i, j + 1);
                        }
                    }
                }
                if (i != ocean.getArray().length - 1) {
                    if (ocean.equals(i + 1, j, 'X') && !ocean.equals(i, j, 'X')) {
                        fenceShip(ocean, i, j);
                    }
                }
                if (i != 0) {
                    if (ocean.equals(i - 1, j, 'X') && !ocean.equals(i, j, 'X')) {
                        fenceShip(ocean, i, j);
                    }
                }
            }
        }
    }

    /**
     * Extra method to fence ship with commas around it.
     * @param ocean an instance of the game field.
     * @param i iteration index for rows.
     * @param j iteration index for columns.
     */
    private void fenceShip(Ocean ocean, int i, int j) {
        ocean.setOcean(',', i, j);
        if (j != 0) {
            ocean.setOcean(',', i, j - 1);
        }
        if (j != ocean.getArray()[0].length - 1) {
            ocean.setOcean(',', i, j + 1);
        }
    }

    /**
     * Checks if a ship satisfies the adjacency rules.
     * @param X random X coordinate of row in matrix.
     * @param Y random Y coordinate of column in matrix.
     * @param repeatRow the length of a ship in rows.
     * @param repeatColumn the length of a ship in columns.
     * @param ocean an instance of the game field.
     * @return true if satisfies.
     */
    private boolean checkAdjacency(int X, int Y, int repeatRow, int repeatColumn, Ocean ocean) {
        // The whole point of the method is that we check whether we try to set the ship on empty cells or not.
        try {
            for (int i = X; i < X + repeatRow; i++) {
                for (int j = Y; j < Y + repeatColumn; j++) {
                    if (ocean.equals(i, j, 'X') || ocean.equals(i, j, ',')) {
                        return false;
                    }
                }
            }
            // Catch an exception if we got out of the bonds of array.
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
