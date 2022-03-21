package battleship;

import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Carrier extends Ship {
    public Carrier(Scanner in) {
        // When initializing an instance the user has to enter the number of submarines in the ocean.
        enterNumberOfShips(in);
    }

    /**
     * Enter needed number of carriers.
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
            case 0 -> populate(randomX, randomY, 1, 5, ocean.getLength(), ocean.getWidth(), ocean);
            case 1 -> populate(randomX, randomY, 5, 1, ocean.getLength(), ocean.getWidth(), ocean);
        }
    }

    /**
     * Enter needed number of carriers.
     * @param in an instance of a Scanner class to input data.
     */
    public void enterNumberOfShips(Scanner in) {
        int carriers;
        // The number of it cannot be less than zero and user can only enter an integer value.
        do {
            System.out.println("Enter the number of carriers. The number of this type of ship should be 0 or above: ");
            while (!in.hasNextInt()) {
                System.out.println("Incorrect input. Try again.");
                in.next();
            }
            carriers = in.nextInt();
        } while (carriers < 0);
        numberOfShips = carriers;
    }
}
