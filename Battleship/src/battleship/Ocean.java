package battleship;

/**
 * A description of an essence of the ocean.
 */
public class Ocean {
    /**
     * Length of the ocean, i.e. length of the first dimension.
     */
    private final int length;

    /**
     * Width of the ocean, i.e. length of the second dimension.
     */
    private final int width;

    /**
     * An ocean with ships.
     */
    private final char[][] ocean;

    /**
     * Extra array of a Game field to show misses and hits.
     */
    private final char[][] gameField;

    /**
     * Extra array for friendly interface of a game field.
     */
    private final char[][] gameInterface;

    /**
     * The number of ships in the ocean.
     */
    private static int numberOfShips;

    /**
     * Memorized extra field of a coordinate of the ocean that used in recovery mode.
     */
    private int firstMemorizedIndex;

    /**
     * Memorized extra field of a coordinate of the ocean that used in recovery mode.
     */
    private int secondMemorizedIndex;

    /**
     * Extra field that shows if the cell was hit that used in recovery mode.
     */
    private boolean hit = false;

    public Ocean(int length, int width) {
        this.length = length;
        this.width = width;
        ocean = new char[length][width];
        gameField = new char[length][width];
        gameInterface = new char[length + 2][width + 2];

        // When initializing the ocean populate matrices with empty cells.
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                ocean[i][j] = '.';
                gameField[i][j] = '.';
            }
        }
    }

    /**
     * Check if a cell of the ocean equals to a value.
     * @param firstIndex index of the first dimension.
     * @param secondIndex index if the second dimension.
     * @param value comparing value.
     * @return true if equals.
     */
    public boolean equals(int firstIndex, int secondIndex, char value) {
        return ocean[firstIndex][secondIndex] == value;
    }

    /**
     * Get length of the first dimension the ocean.
     * @return length.
     */
    public int getLength() {
        return length;
    }

    /**
     * Get length of the second dimension the ocean.
     * @return length.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the ocean.
     * @return the ocean.
     */
    public char[][] getArray() {
        return ocean;
    }

    /**
     * Set a value to the ocean.
     * @param value setting value.
     * @param firstIndex index of the first dimension.
     * @param secondIndex index of the second dimension.
     */
    public void setOcean(char value, int firstIndex, int secondIndex) {
        ocean[firstIndex][secondIndex] = value;
    }

    /**
     * Get number of ships.
     * @return the number of ships.
     */
    public static int getNumberOfShips() {
        return numberOfShips;
    }

    /**
     * Increment the number of ships.
     */
    public static void incrementNumberOfShips() {
        numberOfShips++;
    }

    /**
     * Set a value to the number of ships.
     * @param value setting value.
     */
    public static void setNumberOfShips(int value) {
        numberOfShips = value;
    }

    /**
     * Display the game interface.
     */
    public void display() {
        adjustGameInterface();
        for (int i = 0; i < length + 2; i++) {
            for (int j = 0; j < width + 2; j++) {
                System.out.print(gameInterface[i][j] + " ");
            }
            System.out.println();
        }
    }

    /**
     * Tries to hit a cell with torpedo and shows the message of the result.
     * @param firstIndex index of the first dimension.
     * @param secondIndex index of the second dimension.
     */
    public void hitWithTorpedo(int firstIndex, int secondIndex) {
        if (ocean[firstIndex][secondIndex] == ',' || ocean[firstIndex][secondIndex] == '.') {
            gameField[firstIndex][secondIndex] = '*';
            System.out.println("Miss!");
        } else if (ocean[firstIndex][secondIndex] == 'o') {
            System.out.println("The cell was already hit!");
        } else if (ocean[firstIndex][secondIndex] == 'X') {
            if (checkIfSubmarineSunk(firstIndex, secondIndex)) {
                ocean[firstIndex][secondIndex] = 'o';
                gameField[firstIndex][secondIndex] = '^';
                typeSunk(1);
            } else {
                sinkShipRightAway(firstIndex, secondIndex);
            }
        }
    }

    /**
     * Tries to hit a cell with torpedo in the recovery mode and shows a message of the result.
     * @param firstIndex index of the first dimension.
     * @param secondIndex index of the second dimension.
     */
    public void hitWithTorpedoInRecoveryMode(int firstIndex, int secondIndex) {
        if (ocean[firstIndex][secondIndex] == ',' || ocean[firstIndex][secondIndex] == '.') {
            // Recover ship if the previous cell was hit and in the previous turn the ship was not sunk.
            if (hit && (firstMemorizedIndex != -1 && secondMemorizedIndex != -1)) {
                recoverShip(firstMemorizedIndex, secondMemorizedIndex);
            }
            hit = false;
            gameField[firstIndex][secondIndex] = '*';
            System.out.println("Miss!");
        } else if (ocean[firstIndex][secondIndex] == 'o') {
            System.out.println("The cell was already hit!");
            // Recover ship if the previous cell was hit and in the previous turn the ship was not sunk.
            if (hit && (firstMemorizedIndex != -1 && secondMemorizedIndex != -1)) {
                recoverShip(firstMemorizedIndex, secondMemorizedIndex);
            }
            hit = false;
        } else if (ocean[firstIndex][secondIndex] == 'X') {
            if (checkIfSubmarineSunk(firstIndex, secondIndex)) {
                caseWhenSubmarineWasHitByTorpedo(firstIndex, secondIndex);
            } else {
                if (checkAdjacencyForTorpedo(firstIndex, secondIndex)) {
                    if (firstMemorizedIndex != -1 && secondMemorizedIndex != -1) {
                        recoverShip(firstMemorizedIndex, secondMemorizedIndex);
                    }
                }
                sinkShipRightAway(firstIndex, secondIndex);

                // When a ship was sunk set the indexes values.
                firstMemorizedIndex = -1;
                secondMemorizedIndex = -1;
                hit = false;
            }
        }
    }

    /**
     * Check if the adjacent to the previous hit cell was hit by torpedo.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     * @return true if the hit cell is adjacent.
     */
    private boolean checkAdjacencyForTorpedo(int firstIndex, int secondIndex) {
        return (firstIndex != firstMemorizedIndex || secondIndex + 1 != secondMemorizedIndex)
                && (firstIndex != firstMemorizedIndex || secondIndex - 1 != secondMemorizedIndex)
                && (firstIndex + 1 != firstMemorizedIndex || secondIndex != secondMemorizedIndex)
                && (firstIndex - 1 != firstMemorizedIndex || secondIndex != secondMemorizedIndex);
    }

    /**
     * Actions when the submaarine was hit by torpedo.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     */
    private void caseWhenSubmarineWasHitByTorpedo(int firstIndex, int secondIndex) {
        // If the previous cell was not hit make the submarine sunk.
        // Or else get the submarine sunk but recover ship that was already hit.
        if (!hit) {
            ocean[firstIndex][secondIndex] = 'o';
            gameField[firstIndex][secondIndex] = '^';
            typeSunk(1);
            firstMemorizedIndex = -1;
            secondMemorizedIndex = -1;
        } else {
            ocean[firstIndex][secondIndex] = 'o';
            gameField[firstIndex][secondIndex] = '^';
            typeSunk(1);
            hit = false;
            if (firstMemorizedIndex != -1 && secondMemorizedIndex != -1) {
                recoverShip(firstMemorizedIndex, secondMemorizedIndex);
            }
            firstMemorizedIndex = -1;
            secondMemorizedIndex = -1;
        }
    }

    /**
     * Ship recovery.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     */
    private void recoverShip(int firstIndex, int secondIndex) {
        if (firstIndex != 0 && firstIndex != ocean.length - 1) {
            // If adjacent vertical cell equals to X or o then we recover ship vertically by going up and down.
            if (ocean[firstIndex + 1][secondIndex] == 'X' || ocean[firstIndex + 1][secondIndex] == 'o'
            || ocean[firstIndex - 1][secondIndex] == 'X' || ocean[firstIndex - 1][secondIndex] == 'o') {
                recoverUpAndDown(firstIndex, secondIndex);
            }
        } else if (firstIndex == 0) {
            // If adjacent vertical cell equals to X or o then we recover ship vertically by going down.
            if (ocean[firstIndex + 1][secondIndex] == 'X' || ocean[firstIndex + 1][secondIndex] == 'o') {
                recoverDown(firstIndex, secondIndex);
            }
        } else if (firstIndex == ocean.length - 1) {
            // If adjacent vertical cell equals to X or o then we recover ship vertically by going up.
            if (ocean[firstIndex - 1][secondIndex] == 'X' || ocean[firstIndex - 1][secondIndex] == 'o') {
                recoverUp(firstIndex, secondIndex);
            }
        }

        if (secondIndex != 0 && secondIndex != ocean[0].length - 1) {
            // If adjacent horizontal cell equals to X or o then we recover ship vertically by going up and down.
            if (ocean[firstIndex][secondIndex + 1] == 'X' || ocean[firstIndex][secondIndex + 1] == 'o'
                    || ocean[firstIndex][secondIndex - 1] == 'X' || ocean[firstIndex][secondIndex - 1] == 'o') {
                recoverLeftAndRight(firstIndex, secondIndex);
            }
        } else if (secondIndex == 0) {
            // If adjacent horizontal cell equals to X or o then we recover ship vertically by going right.
            if (ocean[firstIndex][secondIndex + 1] == 'X' || ocean[firstIndex][secondIndex + 1] == 'o') {
                recoverRight(firstIndex, secondIndex);
            }
        } else if (secondIndex == ocean[0].length - 1) {
            // If adjacent horizontal cell equals to X or o then we recover ship vertically by going left.
            if (ocean[firstIndex][secondIndex - 1] == 'X' || ocean[firstIndex][secondIndex - 1] == 'o') {
                recoverLeft(firstIndex, secondIndex);
            }
        }
    }

    /**
     * Recovering ship by going left horizontally in while loop.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second second of a dimension.
     */
    private void recoverLeft(int firstIndex, int secondIndex) {
        int i = secondIndex;
        while (ocean[firstIndex][i] != ',') {
            if (ocean[firstIndex][i] == 'o') {
                ocean[firstIndex][i] = 'X';
                if (gameField[firstIndex][i] != '^') {
                    gameField[firstIndex][i] = '.';
                }
            }
            if (i == 0) {
                break;
            }
            i--;
        }
    }

    /**
     * Recovering ship by going right horizontally in while loop.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second of a dimension.
     */
    private void recoverRight(int firstIndex, int secondIndex) {
        int i = secondIndex;
        while (ocean[firstIndex][i] != ',') {
            if (ocean[firstIndex][i] == 'o') {
                ocean[firstIndex][i] = 'X';
                if (gameField[firstIndex][i] != '^') {
                    gameField[firstIndex][i] = '.';
                }
            }
            if (i == ocean[0].length - 1) {
                break;
            }
            i++;
        }
    }

    /**
     * Trying to recover ship by going left and right horizontally.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     */
    private void recoverLeftAndRight(int firstIndex, int secondIndex) {
        recoverRight(firstIndex, secondIndex);
        int i;
        i = secondIndex;
        while (ocean[firstIndex][i] != ',') {
            if (ocean[firstIndex][i] == 'o') {
                ocean[firstIndex][i] = 'X';
                if (gameField[firstIndex][i] != '^') {
                    gameField[firstIndex][i] = '.';
                }
            }
            if (i == 0) {
                break;
            }
            i--;
        }
    }

    /**
     * Recover ship by going up vertically.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     */
    private void recoverUp(int firstIndex, int secondIndex) {
        int i = firstIndex;
        while (ocean[i][secondIndex] != ',') {
            if (ocean[i][secondIndex] == 'o') {
                ocean[i][secondIndex] = 'X';
                if (gameField[i][secondIndex] != '^') {
                    gameField[i][secondIndex] = '.';
                }
            }
            if (i == 0) {
                break;
            }
            i--;
        }
    }

    /**
     * Recover ship by going down vertically
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     */
    private void recoverDown(int firstIndex, int secondIndex) {
        int i = firstIndex;
        while (ocean[i][secondIndex] != ',') {
            if (ocean[i][secondIndex] == 'o') {
                ocean[i][secondIndex] = 'X';
                if (gameField[i][secondIndex] != '^') {
                    gameField[i][secondIndex] = '.';
                }
            }
            if (i == ocean.length - 1) {
                break;
            }
            i++;
        }
    }

    /**
     * Recovering ship by going up and down vertically.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     */
    private void recoverUpAndDown(int firstIndex, int secondIndex) {
        recoverDown(firstIndex, secondIndex);
        int i;
        i = firstIndex;
        while (ocean[i][secondIndex] != ',') {
            if (ocean[i][secondIndex] == 'o') {
                ocean[i][secondIndex] = 'X';
                if (gameField[i][secondIndex] != '^') {
                    gameField[i][secondIndex] = '.';
                }
            }
            if (i == 0) {
                break;
            }
            i--;
        }
    }

    /**
     * Hits a ship in recovery mode.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     */
    public void hitRecovery(int firstIndex, int secondIndex) {
        // Case when missed.
        if (ocean[firstIndex][secondIndex] == ',' || ocean[firstIndex][secondIndex] == '.') {
            missRecovery(firstIndex, secondIndex);
            // Case when the hit cell gets hit again.
        } else if (ocean[firstIndex][secondIndex] == 'o') {
            System.out.println("The cell was already hit!");
            if (hit && (firstMemorizedIndex != -1 && secondMemorizedIndex != -1)) {
                recoverShip(firstMemorizedIndex, secondMemorizedIndex);
            }
            hit = false;
            // Case when a ship gets hit
        } else if (ocean[firstIndex][secondIndex] == 'X') {
            ocean[firstIndex][secondIndex] = 'o';
            gameField[firstIndex][secondIndex] = 'X';

            if (!hit) {
                caseWhenCellWasNotHit(firstIndex, secondIndex);
            } else {
                if (checkAdjacentHitCells(firstIndex, secondIndex)) {
                    caseWhenAdjacentCellWasHit(firstIndex, secondIndex);
                } else {
                    caseWhenNotAdjacentCellWasHit(firstIndex, secondIndex);
                }
            }
        }
    }

    /**
     * Case when next adjacent cell was hit in recovery mode.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     */
    private void caseWhenAdjacentCellWasHit(int firstIndex, int secondIndex) {
        // Check if a ship is sunk.
        if (checkIfSunk(firstIndex, secondIndex)) {
            typeSunk(getSunkShipType(firstIndex, secondIndex));
            hit = false;
            firstMemorizedIndex = -1;
            secondMemorizedIndex = -1;
            // Else hit the cell.
        } else {
            firstMemorizedIndex = firstIndex;
            secondMemorizedIndex = secondIndex;
            System.out.println("Hit!");
            hit = true;
            caseWhenShipSunk(firstIndex, secondIndex);
        }
    }

    /**
     * Check a case when previous cell was not hit.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     */
    private void caseWhenCellWasNotHit(int firstIndex, int secondIndex) {
        System.out.println("Hit!");
        firstMemorizedIndex = firstIndex;
        secondMemorizedIndex = secondIndex;
        hit = true;

        caseWhenSubmarineSunk(firstIndex, secondIndex);

        caseWhenShipSunk(firstIndex, secondIndex);
    }

    /**
     * Checking when not adjacent cell was hit.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     */
    private void caseWhenNotAdjacentCellWasHit(int firstIndex, int secondIndex) {
        // Hitting a cell and recover previous not adjacent cells.
        System.out.println("Hit!");
        if (firstMemorizedIndex != -1 && secondMemorizedIndex != -1 && firstIndex != firstMemorizedIndex && secondIndex != secondMemorizedIndex) {
            recoverShip(firstMemorizedIndex, secondMemorizedIndex);
        }
        firstMemorizedIndex = firstIndex;
        secondMemorizedIndex = secondIndex;

        hit = true;

        // Check if a ship got sunk.
        caseWhenShipSunk(firstIndex, secondIndex);
    }

    /**
     * Checking if the next adjacent cell was hit.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     * @return true if the adjacent cell was hit.
     */
    private boolean checkAdjacentHitCells(int firstIndex, int secondIndex) {
        return (firstIndex == firstMemorizedIndex && secondIndex + 1 == secondMemorizedIndex)
            || (firstIndex == firstMemorizedIndex && secondIndex - 1 == secondMemorizedIndex)
            || (firstIndex + 1 == firstMemorizedIndex && secondIndex == secondMemorizedIndex)
            || (firstIndex - 1 == firstMemorizedIndex && secondIndex == secondMemorizedIndex);
    }

    /**
     * Actions when the ship is sunk.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     */
    private void caseWhenShipSunk(int firstIndex, int secondIndex) {
        if (checkIfSunk(firstIndex, secondIndex)) {
            typeSunk(getSunkShipType(firstIndex, secondIndex));
            hit = false;
            firstMemorizedIndex = -1;
            secondMemorizedIndex = -1;
        }
    }

    /**
     * Actions when the submarine is sunk.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     */
    private void caseWhenSubmarineSunk(int firstIndex, int secondIndex) {
        if (checkIfSubmarineSunk(firstIndex, secondIndex)) {
            typeSunk(1);
            gameField[firstIndex][secondIndex] = '^';
            hit = false;
            firstMemorizedIndex = -1;
            secondMemorizedIndex = -1;
        }
    }

    /**
     * Actions when there was a miss in the recovery mode.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     */
    private void missRecovery(int firstIndex, int secondIndex) {
        gameField[firstIndex][secondIndex] = '*';
        System.out.println("Miss!");
        if (hit && (firstMemorizedIndex != -1 && secondMemorizedIndex != -1)) {
            recoverShip(firstMemorizedIndex, secondMemorizedIndex);
        }
        hit = false;
    }

    /**
     * Tries to hit a ship and shows a message of a result.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     */
    public void hit(int firstIndex, int secondIndex) {
        if (ocean[firstIndex][secondIndex] == ',' || ocean[firstIndex][secondIndex] == '.') {
            gameField[firstIndex][secondIndex] = '*';
            System.out.println("Miss!");
        } else if (ocean[firstIndex][secondIndex] == 'o') {
            System.out.println("The cell was already hit!");
        } else if (ocean[firstIndex][secondIndex] == 'X') {
            ocean[firstIndex][secondIndex] = 'o';
            gameField[firstIndex][secondIndex] = 'X';
            if (!checkIfSunk(firstIndex, secondIndex)) {
                System.out.println("Hit!");
            } else if (checkIfSubmarineSunk(firstIndex, secondIndex)) {
                typeSunk(1);
                gameField[firstIndex][secondIndex] = '^';
            } else if (checkIfSunk(firstIndex, secondIndex)) {
                typeSunk(getSunkShipType(firstIndex, secondIndex));
            }
        }
    }

    /**
     * Checks if there ships in the ocean.
     * @return true if ocean contains ships.
     */
    public boolean checkForShips() {
        for (char[] chars : ocean) {
            for (int j = 0; j < ocean[0].length; j++) {
                if (chars[j] == 'X') {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Extra method that adjusts game interface, makes it more pleasant.
     */
    private void adjustGameInterface() {
        gameInterface[0][0] = ' ';
        gameInterface[0][1] = ' ';
        gameInterface[1][0] = ' ';
        gameInterface[1][1] = ' ';
        for (int i = 0; i < 1; i++) {
            for (int j = 2; j < width + 2; j++) {
                gameInterface[i][j] = Integer.toString(j - 2).charAt(0);
            }
        }
        for (int i = 2; i < length + 2; i++) {
            for (int j = 0; j < 1; j++) {
                gameInterface[i][j] = Integer.toString(i - 2).charAt(0);
            }
        }
        for (int i = 1; i < 2; i++) {
            for (int j = 2; j < width + 2; j++) {
                gameInterface[i][j] = '_';
            }
        }
        for (int i = 2; i < length + 2; i++) {
            for (int j = 1; j < 2; j++) {
                gameInterface[i][j] = '|';
            }
        }
        for (int i = 2; i < length + 2; i++) {
            if (width + 2 - 2 >= 0) {
                System.arraycopy(gameField[i - 2], 0, gameInterface[i], 2, width + 2 - 2);
            }
        }
    }

    /**
     * Sinks a ship right away when using a torpedo.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     */
    private void sinkShipRightAway(int firstIndex, int secondIndex) {
        if (firstIndex != 0 && firstIndex != ocean.length - 1) {
            if (ocean[firstIndex + 1][secondIndex] == 'X' || ocean[firstIndex + 1][secondIndex] == 'o'
                    || ocean[firstIndex - 1][secondIndex] == 'X' || ocean[firstIndex - 1][secondIndex] == 'o') {
                sinkBackAndForth(firstIndex, secondIndex);
            }
        } else if (firstIndex == 0) {
            if (ocean[firstIndex + 1][secondIndex] == 'X' || ocean[firstIndex + 1][secondIndex] == 'o') {
                sinkDown(firstIndex, secondIndex);
            }
        } else if (firstIndex == ocean.length - 1) {
            if (ocean[firstIndex - 1][secondIndex] == 'X' || ocean[firstIndex - 1][secondIndex] == 'o') {
                sinkUp(firstIndex, secondIndex);
            }
        }

        if (secondIndex != 0 && secondIndex != ocean[0].length - 1) {
            if (ocean[firstIndex][secondIndex - 1] == 'X' || ocean[firstIndex][secondIndex - 1] == 'o'
                    || ocean[firstIndex][secondIndex + 1] == 'o' || ocean[firstIndex][secondIndex + 1] == 'X') {
                sinkLeftAndRight(firstIndex, secondIndex);
            }
        } else if (secondIndex == 0) {
            if (ocean[firstIndex][secondIndex + 1] == 'o' || ocean[firstIndex][secondIndex + 1] == 'X') {
                sinkRight(firstIndex, secondIndex);
            }
        } else if (secondIndex == ocean[0].length - 1) {
            if (ocean[firstIndex][secondIndex - 1] == 'X' || ocean[firstIndex][secondIndex - 1] == 'o') {
                sinkLeft(firstIndex, secondIndex);
            }
        }

        typeSunk(getSunkShipType(firstIndex, secondIndex));
    }

    /**
     * Gets a type sunk ship.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     * @return
     */
    private int getSunkShipType(int firstIndex, int secondIndex) {
        int shipType = 0;

        if (firstIndex != 0 && firstIndex != ocean.length - 1) {
            if (ocean[firstIndex + 1][secondIndex] == 'o' || ocean[firstIndex - 1][secondIndex] == 'o') {
                shipType = goBackAndForthVertically(firstIndex, secondIndex, shipType);
            }
        } else if (firstIndex == 0) {
            if (ocean[firstIndex + 1][secondIndex] == 'o') {
                shipType = goDownVertically(firstIndex, secondIndex, shipType);
            }
        } else if (firstIndex == ocean.length - 1) {
            if (ocean[firstIndex - 1][secondIndex] == 'o') {
                shipType = goUpVertically(firstIndex, secondIndex, shipType);
            }
        }


        if (secondIndex != 0 && secondIndex != ocean[0].length - 1) {
            if (ocean[firstIndex][secondIndex + 1] == 'o' || ocean[firstIndex][secondIndex - 1] == 'o') {
                shipType = goBackAndForthHorizontally(firstIndex, secondIndex, shipType);
            }
        } else if (secondIndex == 0) {
            if (ocean[firstIndex][secondIndex + 1] == 'o') {
                shipType = goRight(firstIndex, secondIndex, shipType);
            }
        } else if (secondIndex == ocean[0].length - 1) {
            if (ocean[firstIndex][secondIndex - 1] == 'o') {
                shipType = goLeft(firstIndex, secondIndex, shipType);
            }
        }

        return shipType;
    }

    /**
     * Gets the number of sunk type by going left horizontally.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     * @param shipType the number of cells in a ship.
     * @return the ship type according to the number of cells.
     */
    private int goLeft(int firstIndex, int secondIndex, int shipType) {
        int i = secondIndex;
        while (ocean[firstIndex][i] != ',') {
            if (ocean[firstIndex][i] == 'o') {
                shipType++;
                gameField[firstIndex][i] = '^';
            }
            if (i == 0) {
                break;
            }
            i--;
        }
        return shipType;
    }

    /**
     * Gets the number of sunk type by going right horizontally.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     * @param shipType the number of cells in a ship.
     * @return the ship type according to the number of cells.
     */
    private int goRight(int firstIndex, int secondIndex, int shipType) {
        int i = secondIndex;
        while (ocean[firstIndex][i] != ',') {
            if (ocean[firstIndex][i] == 'o') {
                shipType++;
                gameField[firstIndex][i] = '^';
            }
            if (i == ocean[0].length - 1) {
                break;
            }
            i++;
        }
        return shipType;
    }

    /**
     * Gets the number of sunk type by going left and right horizontally.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     * @param shipType the number of cells in a ship.
     * @return the ship type according to the number of cells.
     */
    private int goBackAndForthHorizontally(int firstIndex, int secondIndex, int shipType) {
        shipType = goRight(firstIndex, secondIndex, shipType);
        int i;
        i = secondIndex;
        while (ocean[firstIndex][i] != ',') {
            if (ocean[firstIndex][i] == 'o' && i < secondIndex) {
                shipType++;
                gameField[firstIndex][i] = '^';
            }
            if (i == 0) {
                break;
            }
            i--;
        }
        return shipType;
    }

    /**
     * Gets the number of sunk type by going up vertically.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     * @param shipType the number of cells in a ship.
     * @return the ship type according to the number of cells.
     */
    private int goUpVertically(int firstIndex, int secondIndex, int shipType) {
        int i = firstIndex;
        while (ocean[i][secondIndex] != ',') {
            if (ocean[i][secondIndex] == 'o') {
                shipType++;
                gameField[i][secondIndex] = '^';
            }
            if (i == 0) {
                break;
            }
            i--;
        }
        return shipType;
    }

    /**
     * Gets the number of sunk type by going down vertically.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     * @param shipType the number of cells in a ship.
     * @return the ship type according to the number of cells.
     */
    private int goDownVertically(int firstIndex, int secondIndex, int shipType) {
        int i = firstIndex;
        while (ocean[i][secondIndex] != ',') {
            if (ocean[i][secondIndex] == 'o') {
                shipType++;
                gameField[i][secondIndex] = '^';
            }
            if (i == ocean.length - 1) {
                break;
            }
            i++;
        }
        return shipType;
    }

    /**
     * Gets the number of sunk type by going up and down vertically.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     * @param shipType the number of cells in a ship.
     * @return the ship type according to the number of cells.
     */
    private int goBackAndForthVertically(int firstIndex, int secondIndex, int shipType) {
        shipType = goDownVertically(firstIndex, secondIndex, shipType);
        int i;
        i = firstIndex;
        while (ocean[i][secondIndex] != ',') {
            if (ocean[i][secondIndex] == 'o' && i < firstIndex) {
                shipType++;
                gameField[i][secondIndex] = '^';
            }
            if (i == 0) {
                break;
            }
            i--;
        }
        return shipType;
    }

    /**
     * Checks if a ship was sunk.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     * @return true if was sunk.
     */
    private boolean checkIfSunk(int firstIndex, int secondIndex) {
        int numberOfXs = 0;

        if (secondIndex != 0 && secondIndex != ocean[0].length - 1) {
            if (ocean[firstIndex][secondIndex + 1] == 'X' || ocean[firstIndex][secondIndex - 1] == 'X'
                    || ocean[firstIndex][secondIndex + 1] == 'o' || ocean[firstIndex][secondIndex - 1] == 'o') {
                numberOfXs = checkBackAndForthHorizontally(firstIndex, secondIndex, numberOfXs);
            }
        } else if (secondIndex == 0) {
            if (ocean[firstIndex][secondIndex + 1] == 'X' || ocean[firstIndex][secondIndex + 1] == 'o') {
                numberOfXs = checkRight(firstIndex, secondIndex, numberOfXs);
            }
        } else if (secondIndex == ocean[0].length - 1) {
            if (ocean[firstIndex][secondIndex - 1] == 'X' || ocean[firstIndex][secondIndex - 1] == 'o') {
                numberOfXs = checkLeft(firstIndex, secondIndex, numberOfXs);
            }
        }


        if (firstIndex != 0 && firstIndex != ocean.length - 1) {
            if (ocean[firstIndex + 1][secondIndex] == 'X'
                    || ocean[firstIndex - 1][secondIndex] == 'X'
                    || ocean[firstIndex - 1][secondIndex] == 'o'
                    || ocean[firstIndex + 1][secondIndex] == 'o') {
                numberOfXs = checkBackAndForthVertically(firstIndex, secondIndex, numberOfXs);
            }
        } else if (firstIndex == 0) {
            if (ocean[firstIndex + 1][secondIndex] == 'X' || ocean[firstIndex + 1][secondIndex] == 'o') {
                numberOfXs = checkDown(firstIndex, secondIndex, numberOfXs);
            }
        } else if (firstIndex == ocean.length - 1) {
            if (ocean[firstIndex - 1][secondIndex] == 'X' || ocean[firstIndex - 1][secondIndex] == 'o') {
                numberOfXs = checkUp(firstIndex, secondIndex, numberOfXs);
            }
        }

        return numberOfXs == 0;
    }

    /**
     * Checks if a ship is sunk by going up vertically.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     * @param numberOfXs the number of not hit cells.
     * @return the number of not hit cells.
     */
    private int checkUp(int firstIndex, int secondIndex, int numberOfXs) {
        int i = firstIndex;
        while (ocean[i][secondIndex] != ',' || i != 0) {
            if (ocean[i][secondIndex] == 'X') {
                numberOfXs++;
            }
            if (i == 0) {
                break;
            }
            i--;
        }
        return numberOfXs;
    }

    /**
     * Checks if a ship is sunk by going down vertically.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     * @param numberOfXs the number of not hit cells.
     * @return the number of not hit cells.
     */
    private int checkDown(int firstIndex, int secondIndex, int numberOfXs) {
        int i = firstIndex;
        while (ocean[i][secondIndex] != ',' || i != ocean.length - 1) {
            if (ocean[i][secondIndex] == 'X') {
                numberOfXs++;
            }
            if (i == ocean.length - 1) {
                break;
            }
            i++;
        }
        return numberOfXs;
    }

    /**
     * Checks if a ship is sunk by going up and down vertically.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     * @param numberOfXs the number of not hit cells.
     * @return the number of not hit cells.
     */
    private int checkBackAndForthVertically(int firstIndex, int secondIndex, int numberOfXs) {
        numberOfXs = checkDown(firstIndex, secondIndex, numberOfXs);
        int i;
        i = firstIndex;
        while (ocean[i][secondIndex] != ',' || i != 0) {
            if (ocean[i][secondIndex] == 'X') {
                numberOfXs++;
            }
            if (i == 0) {
                break;
            }
            i--;
        }
        return numberOfXs;
    }

    /**
     * Checks if a ship is sunk by going horizontally left.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     * @param numberOfXs the number of not hit cells.
     * @return the number of not hit cells.
     */
    private int checkLeft(int firstIndex, int secondIndex, int numberOfXs) {
        int i = secondIndex;
        while (ocean[firstIndex][i] != ',' || i != 0) {
            if (ocean[firstIndex][i] == 'X') {
                numberOfXs++;
            }
            if (i == 0) {
                break;
            }
            i--;
        }
        return numberOfXs;
    }

    /**
     * Checks if a ship is sunk by going horizontally right.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     * @param numberOfXs the number of not hit cells.
     * @return the number of not hit cells.
     */
    private int checkRight(int firstIndex, int secondIndex, int numberOfXs) {
        int i = secondIndex;
        while (ocean[firstIndex][i] != ',' || i != ocean[0].length - 1) {
            if (ocean[firstIndex][i] == 'X') {
                numberOfXs++;
            }
            if (i == ocean[0].length - 1) {
                break;
            }
            i++;
        }
        return numberOfXs;
    }

    /**
     * Checks if a ship is sunk by going horizontally left and right.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     * @param numberOfXs the number of not hit cells.
     * @return the number of not hit cells.
     */
    private int checkBackAndForthHorizontally(int firstIndex, int secondIndex, int numberOfXs) {
        numberOfXs = checkRight(firstIndex, secondIndex, numberOfXs);
        int i;
        i = secondIndex;
        while (ocean[firstIndex][i] != ',' || i != 0) {
            if (ocean[firstIndex][i] == 'X') {
                numberOfXs++;
            }
            if (i == 0) {
                break;
            }
            i--;
        }
        return numberOfXs;
    }

    /**
     * Checks if a submarine is sunk if the adjacent cells are empty.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     * @return true if submarine is sunk.
     */
    public boolean checkIfSubmarineSunk(int firstIndex, int secondIndex) {
        if (secondIndex != ocean[0].length - 1) {
            if (ocean[firstIndex][secondIndex + 1] != ',') {
                return false;
            }
        }
        if (secondIndex != 0) {
            if (ocean[firstIndex][secondIndex - 1] != ',') {
                return false;
            }
        }
        if (firstIndex != ocean.length - 1) {
            if (ocean[firstIndex + 1][secondIndex] != ',') {
                return false;
            }
        }
        if (firstIndex != 0) {
            return ocean[firstIndex - 1][secondIndex] == ',';
        }
        return true;
    }

    /**
     * Sinks a ship by going left horizontally.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     */
    private void sinkLeft(int firstIndex, int secondIndex) {
        int i = secondIndex;
        while (ocean[firstIndex][i] != ',') {
            if (ocean[firstIndex][i] != 'o') {
                ocean[firstIndex][i] = 'o';
            }
            gameField[firstIndex][i] = '^';
            if (i == 0) {
                break;
            }
            i--;
        }
    }

    /**
     * Sinks a ship by going right horizontally.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     */
    private void sinkRight(int firstIndex, int secondIndex) {
        int i = secondIndex;
        while (ocean[firstIndex][i] != ',') {
            if (ocean[firstIndex][i] != 'o') {
                ocean[firstIndex][i] = 'o';
            }
            gameField[firstIndex][i] = '^';
            if (i == ocean[0].length - 1) {
                break;
            }
            i++;
        }
    }

    /**
     * Sinks a ship by going left and right horizontally.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     */
    private void sinkLeftAndRight(int firstIndex, int secondIndex) {
        sinkRight(firstIndex, secondIndex);
        int i;
        i = secondIndex;
        while (ocean[firstIndex][i] != ',') {
            if (ocean[firstIndex][i] != 'o') {
                ocean[firstIndex][i] = 'o';
            }
            gameField[firstIndex][i] = '^';
            if (i == 0) {
                break;
            }
            i--;
        }
    }

    /**
     * Sinks a ship by going up vertically.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     */
    private void sinkUp(int firstIndex, int secondIndex) {
        int i = firstIndex;
        while (ocean[i][secondIndex] != ',') {
            if (ocean[i][secondIndex] != 'o') {
                ocean[i][secondIndex] = 'o';
            }
            gameField[i][secondIndex] = '^';
            if (i == 0) {
                break;
            }
            i--;
        }
    }

    /**
     * Sinks a ship by going down vertically.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     */
    private void sinkDown(int firstIndex, int secondIndex) {
        int i = firstIndex;
        while (ocean[i][secondIndex] != ',') {
            if (ocean[i][secondIndex] != 'o') {
                ocean[i][secondIndex] = 'o';
            }
            gameField[i][secondIndex] = '^';
            if (i == ocean.length - 1) {
                break;
            }
            i++;
        }
    }

    /**
     * Sinks a ship by going up and down vertically.
     * @param firstIndex the first index of a dimension.
     * @param secondIndex the second index of a dimension.
     */
    private void sinkBackAndForth(int firstIndex, int secondIndex) {
        sinkDown(firstIndex, secondIndex);
        int i;
        i = firstIndex;
        while (ocean[i][secondIndex] != ',') {
            if (ocean[i][secondIndex] != 'o') {
                ocean[i][secondIndex] = 'o';
            }
            gameField[i][secondIndex] = '^';
            if (i == 0) {
                break;
            }
            i--;
        }
    }

    /**
     * Shows the message which type of a ship was sunk.
     * @param type the number of a type.
     */
    private void typeSunk(int type) {
        switch (type) {
            case 1 -> System.out.println("You have sunk the submarine!");
            case 2 -> System.out.println("You have sunk the destroyer!");
            case 3 -> System.out.println("You have sunk the cruiser!");
            case 4 -> System.out.println("You have sunk the battleship!");
            case 5 -> System.out.println("You have sunk the carrier!");
        }
    }
}
