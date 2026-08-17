// My solution for CS61B proj0.
package game2048logic;

import game2048rendering.Board;
import game2048rendering.Side;
import game2048rendering.Tile;

import java.util.Formatter;

import java.util.HashMap;
import java.util.ArrayList;

/** The state of a game of 2048.
 *  @author P. N. Hilfinger + Josh Hug
 */

public class Model {
    /** Current contents of the board. */
    private final Board board;
    /** Current score. */
    private int score;
    private HashMap<Integer, ArrayList<int[]>> valueToPositionMap; // final warning?

    /* Coordinate System: column x, row y of the board (where x = 0,
     * y = 0 is the lower-left corner of the board) will correspond
     * to board.tile(x, y).  Be careful!
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = 0;
        valueToPositionMap = generateValueToPositionsHashMap();
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (x, y) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score) {
        board = new Board(rawValues);
        this.score = score;
        valueToPositionMap = generateValueToPositionsHashMap();
    }

    /** Returns a hashmap where the key is an existing tile value and the value is a list of the positions where that
     * value resides on the board. */
     public HashMap<Integer, ArrayList<int[]>> generateValueToPositionsHashMap() {
        HashMap<Integer, ArrayList<int[]>> valueToPositionMap = new HashMap<>();

        for(int x = 0; x < size(); x++) {
            for(int y = 0; y < size(); y++) {
                int[] position = {x, y}; // Would prefer to size array (2 elements), but won't worry about it right now.
                if (board.tile(x, y) == null) {
                    // Better way to do this? Did I include extra steps?
                    ArrayList<int[]> dummyList = new ArrayList<>();
                    ArrayList<int[]> positionList = valueToPositionMap.getOrDefault(0, dummyList);
                    positionList.add(position);
                    valueToPositionMap.put(0, positionList);
                } else {
                    ArrayList<int[]> dummyList = new ArrayList<>();
                    ArrayList<int[]> positionList = valueToPositionMap.getOrDefault(board.tile(x, y).value(), dummyList);
                    positionList.add(position);
                    valueToPositionMap.put(board.tile(x, y).value(), positionList);
                }
            }
        }


        // System.out.println(valueToPositionMap);
        return valueToPositionMap;
     }

    /** Return the current Tile at (x, y), where 0 <= x < size(),
     *  0 <= y < size(). Returns null if there is no tile there.
     *  Used for testing. */
    public Tile tile(int x, int y) {
        return board.tile(x, y);
    }

    /** Return the number of squares on one side of the board. */
    public int size() {
        return board.size();
    }

    /** Return the current score. */
    public int score() {
        return score;
    }


    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        board.clear();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        // (Guess) Might be the right spot to update the map...
        // valueToPositionMap = generateValueToPositionsHashMap();
        return maxTileExists() || !atLeastOneMoveExists();
    }

    /** Returns this Model's board. */
    public Board getBoard() {
        return board;
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public boolean emptySpaceExists() {
        if (valueToPositionMap.containsKey(0)) {
            return true;
        }

        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by this.MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public boolean maxTileExists() {
        // Winning condition.
        if (valueToPositionMap.containsKey(MAX_PIECE)) {
            return true;
        }

        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public boolean atLeastOneMoveExists() {
        // condition 1:
         if (emptySpaceExists()) {
             return true;
         }

        // condition 2:
         for (Integer tileValue : valueToPositionMap.keySet()) {
             ArrayList<int[]> positionsList = valueToPositionMap.get(tileValue);
             for (int outer = 0; outer < positionsList.size(); outer++) {
                 for (int inner = outer + 1; inner < positionsList.size(); inner++) {
                     // North/South condition
                     if (((positionsList.get(outer)[1] + 1) == positionsList.get(inner)[1] || (positionsList.get(outer)[1] - 1) == positionsList.get(inner)[1]) && positionsList.get(outer)[0] == positionsList.get(inner)[0]) {
                         return true;
                     }
                     // East/West condition
                     if (((positionsList.get(outer)[0] + 1) == positionsList.get(inner)[0] || (positionsList.get(outer)[0] - 1) == positionsList.get(inner)[0]) && positionsList.get(outer)[1] == positionsList.get(inner)[1]) {
                         return true;
                     }
                 }
             }
         }

        return false;
    }

    /**
     * Moves the tile at position (x, y) as far up as possible.
     *
     * Rules for Tilt:
     * 1. If two Tiles are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     */
    public void moveTileUpAsFarAsPossible(int x, int y) {
        Tile currTile = board.tile(x, y);
        int myValue = currTile.value();
        int targetY = y;

         while (targetY != 3) {
             // condition 1: tile above is empty
             // also prevents null tile falling into future conditions
             if (board.tile(x, (targetY + 1)) == null) {
                 targetY = targetY + 1;
                 continue;
             }

             Tile aboveTile = board.tile(x, (targetY + 1));
             // condition 2: the tile above is not equal to current tile
             // OR above tile is equal to the current tile but the current or above tile has already merged
             if (myValue != aboveTile.value() || myValue == aboveTile.value() && (currTile.wasMerged() || aboveTile.wasMerged())) {
                 break;
             // condition 3: tile has not been merged and tile is equal to above tile
             } else if (myValue == aboveTile.value() && !currTile.wasMerged()) {
                 targetY = targetY + 1;
                 score = score + (myValue * 2);
                 break;
             }
         }

         board.move(x, targetY, currTile);
    }

    /** Handles the movements of the tilt in column x of the board
     * by moving every tile in the column as far up as possible.
     * The viewing perspective has already been set,
     * so we are tilting the tiles in this column up.
     * */
    public void tiltColumn(int x) {
         for (int y = 2; y >= 0; y--) {
             if (board.tile(x, y) != null) {
                moveTileUpAsFarAsPossible(x, y);
            }
         }
    }

    public void tilt(Side side) {
        board.setViewingPerspective(side);
        for (int x = 0; x <= 3; x++) {
            tiltColumn(x);
        }

        board.setViewingPerspective(Side.NORTH);

        // Failing:
            // TestMultipleMoves Suite:
                // Multiple moves
                // Multiple moves and end behavior
                // Multiple moves 2

            // Assuming some of this is from my hardcoded numbers...
            // TestNbyN Suite:
                // Exception: The ants go marching
                // testLarge()
                // Exception: Tilts for N = 1, 2, 3
                // Exception: Non-merged tilts for N = 1, 2, 3
    }

    /** Tilts every column of the board toward SIDE.
     */
    public void tiltWrapper(Side side) {
        board.resetMerged();
        tilt(side);
    }


    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int y = size() - 1; y >= 0; y -= 1) {
            for (int x = 0; x < size(); x += 1) {
                if (tile(x, y) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(x, y).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (game is %s) %n", score(), over);
        return out.toString();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Model m) && this.toString().equals(m.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
