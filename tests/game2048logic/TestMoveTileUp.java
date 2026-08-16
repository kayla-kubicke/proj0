package game2048logic;
import jh61b.grader.GradedTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static com.google.common.truth.Truth.assertWithMessage;

/** Tests the moveTileUpAsFarAsPossible() method of Model.
 *
 *
 * @author Erik Kizior
 */
@Timeout(value = 60, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
public class TestMoveTileUp {

    /** No merging required. */
    @Test
    @Tag("moveTileUpAsFarAsPossible")
    @DisplayName("Single tile in empty column")
    @GradedTest(number = "10.1")
    public void testOneTile() {
        int[][] board = {
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {2, 0, 0, 0}
        };
        Model before = new Model(board, 0);
        before.moveTileUpAsFarAsPossible(0, 0);

        int[][] result = {
                {2, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        };

        Model after = new Model(result, 0);
        assertWithMessage("Boards should match:").that(before.toString()).isEqualTo(after.toString());
    }

    /** No merging required. Tile blocks movement. */
    @Test
    @Tag("moveTileUpAsFarAsPossible")
    @DisplayName("two tiles, different values")
    @GradedTest(number = "10.2")
    public void testTwoTiles() {
        int[][] board = {
                {4, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {2, 0, 0, 0}
        };
        Model before = new Model(board, 0);
        before.moveTileUpAsFarAsPossible(0, 0);

        int[][] result = {
                {4, 0, 0, 0},
                {2, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        };

        Model after = new Model(result, 0);
        assertWithMessage("Boards should match:").that(before.toString()).isEqualTo(after.toString());
    }

    /** Merging required. Tiles of same value in same column. Does not depend on the score. */
    @Test
    @Tag("moveTileUpAsFarAsPossible")
    @DisplayName("two tiles merge no score")
    @GradedTest(number = "10.3")
    public void testTwoTilesMergeNoScore() {
        int[][] board = {
                {2, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {2, 0, 0, 0}
        };
        Model before = new Model(board, 0);
        before.moveTileUpAsFarAsPossible(0, 0);

        int[][] result = {
                {4, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        };

        Model after = new Model(result, before.score());
        assertWithMessage("Boards should match:").that(before.toString()).isEqualTo(after.toString());
    }

    /** Merging required. Tiles of same value in same column. Checks that score updates correctly. */
    @Test
    @Tag("moveTileUpAsFarAsPossible")
    @DisplayName("two tiles merge with score update")
    @GradedTest(number = "10.4")
    public void testTwoTilesMergeScore() {
        int[][] board = {
                {2, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {2, 0, 0, 0}
        };
        Model before = new Model(board, 0);
        before.moveTileUpAsFarAsPossible(0, 0);

        int[][] result = {
                {4, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        };

        Model after = new Model(result, 4);
        assertWithMessage("Boards should match:").that(before.toString()).isEqualTo(after.toString());
    }

    /** Technically, not suppose to add code anywhere but Model...
     * but, I don't think writing extra tests count. ;) */
    @Test
    @Tag("moveTileUpAsFarAsPossible")
    @DisplayName("two tiles in neighboring columns do not update")
    @GradedTest(number = "10.4")
    public void testTwoTilesInNeighboringColumnsDoNotUpdate() {
        int[][] board = {
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 4, 2}
        };
        Model before = new Model(board, 0);
        before.moveTileUpAsFarAsPossible(2, 0);
        before.moveTileUpAsFarAsPossible(3, 0);

        int[][] result = {
                {0, 0, 4, 2},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        };

        Model after = new Model(result, 0);
        assertWithMessage("Boards should match:").that(before.toString()).isEqualTo(after.toString());
    }

    @Test
    @Tag("moveTileUpAsFarAsPossible")
    @DisplayName("three of the same tiles in single column merge as expected")
    @GradedTest(number = "10.4")
    public void testThreeOfTheSameTilesInSingleColumnMergeAsExpected() {
        int[][] board = {
                {0, 0, 0, 0},
                {0, 0, 0, 2},
                {0, 0, 0, 2},
                {0, 0, 0, 2}
        };
        Model before = new Model(board, 0);
        before.moveTileUpAsFarAsPossible(3, 2);
        before.moveTileUpAsFarAsPossible(3, 1);
        before.moveTileUpAsFarAsPossible(3, 0);

        int[][] result = {
                {0, 0, 0, 4},
                {0, 0, 0, 2},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        };

        Model after = new Model(result, 4);
        assertWithMessage("Boards should match:").that(before.toString()).isEqualTo(after.toString());
    }

    /** Terrible name... */
    @Test
    @Tag("moveTileUpAsFarAsPossible")
    @DisplayName("three tiles in single column do not merge again after top two merge and match (the) remaining tile")
    @GradedTest(number = "10.4")
    public void testThreeTilesInSingleColumnDoNotMergeAgainAfterTopTwoMergeAndMatchRemainingTile() {
        int[][] board = {
                {0, 0, 0, 0},
                {0, 0, 0, 2},
                {0, 0, 0, 2},
                {0, 0, 0, 4}
        };
        Model before = new Model(board, 0);
        before.moveTileUpAsFarAsPossible(3, 2);
        before.moveTileUpAsFarAsPossible(3, 1);
        before.moveTileUpAsFarAsPossible(3, 0);

        int[][] result = {
                {0, 0, 0, 4},
                {0, 0, 0, 4},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        };

        Model after = new Model(result, 4);
        assertWithMessage("Boards should match:").that(before.toString()).isEqualTo(after.toString());
    }
}
