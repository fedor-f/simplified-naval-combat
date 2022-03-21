package battleship;

import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Destroyer extends Ship {
    public Destroyer(Scanner in) {
        // When initializing an instance the user has to enter the number of submarines in the ocean.
        enterNumberOfShips(in);
    }

    /**
     * Generate a destroyer in the ocean.
     * @param length length of the ocean.
     * @param width width of the ocean.
     * @param ocean a game field, i.e. an ocean.
     */
    public void randomShipGenerator(int length, int width, Ocean ocean) {
        // Randomly generate one cell of the ocean.
        int randomX = ThreadLocalRandom.current().nextInt(0, ocean.getLength());
        int randomY = ThreadLocalRandom.current().nextInt(0, ocean.getWidth());

        // Generate 0 or 1 to decide if the ship is going to be populated vertically or horizontally.
        int verticalOrHorizontal = ThreadLocalRandom.current().nextInt(0, 2);

        // Depending on the generated number the system populates the ship vertically or horizontally.
        switch (verticalOrHorizontal) {
            case 0 -> populate(randomX, randomY, 1, 2, ocean.getLength(), ocean.getWidth(), ocean);
            case 1 -> populate(randomX, randomY, 2, 1, ocean.getLength(), ocean.getWidth(), ocean);
        }
    }

    /**
     * Enter needed number of destroyers.
     * @param in an instance of a Scanner class to input data.
     */
    public void enterNumberOfShips(Scanner in) {
        int destroyers;
        // The number of it cannot be less than zero and user can only enter an integer value.
        do {
            System.out.println("Enter the number of destroyers. The number of this type of ship should be 0 or above: ");
            while (!in.hasNextInt()) {
                System.out.println("Incorrect input. Try again.");
                in.next();
            }
            destroyers = in.nextInt();
        } while (destroyers < 0);
        numberOfShips = destroyers;
    }
}
