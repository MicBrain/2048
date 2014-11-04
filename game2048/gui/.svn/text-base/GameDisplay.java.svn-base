package game2048.gui;

import java.awt.event.KeyEvent;
import java.util.concurrent.ArrayBlockingQueue;

import ucb.gui.TopLevel;
import ucb.gui.LayoutSpec;

/** The GUI controller for a 2048 board and buttons.
 *  @author P. N. Hilfinger
 */
class GameDisplay extends TopLevel {

    /** A new window with given TITLE and a board with ROWS tiles on a side. */
    GameDisplay(String title, int rows) {
        super(title, true);
        _board = new BoardDisplay(rows);
        addLabel("", "Score", new LayoutSpec("y", 1));
        addButton("New Game", "newGame", new LayoutSpec("y", 1));
        addButton("Quit", "quit", new LayoutSpec("y", 1));
        add(_board, new LayoutSpec("y", 0, "width", 3));
        _board.requestFocusInWindow();
        _board.setKeyHandler("keypress", this, "keyPressed");
        setPreferredFocus(_board);
        display(true);
        setScore(0, 0);
    }

    /** Response to "Quit" button click. */
    public void quit(String dummy) {
        _pendingKeys.offer("Quit");
        _board.requestFocusInWindow();
    }

    /** Response to "New Game" button click. */
    public void newGame(String dummy) {
        _pendingKeys.offer("New Game");
        _board.requestFocusInWindow();
    }

    /** Clear all tiles from the board. */
    public void clear() {
        _board.clear();
    }

    /** Given that TILES[r][c] and TILES2[r][c] contain the final positions of
     *  existing tiles (a tile is in TILES2[r][c] if it is to be merged with
     *  the corresponding tile inn TILES[r][c], and that NEXTTILES[r][c]
     *  contain the tiles that will result (with new tiles created for merges),
     *  animate the display of the necessary moves and update the board state.
     *  The tiles themselves contain their actual coordinates on the board. */
    public void displayMoves(Tile[][] tiles,
                             Tile[][] tiles2,
                             Tile[][] nextTiles) {
        _board.displayMoves(tiles, tiles2, nextTiles);
    }

    /** Respond to the user pressing key E. */
    public void keyPressed(KeyEvent e) {
        _pendingKeys.offer(e.getKeyText(e.getKeyCode()));
    }

    /** Return the next key press, waiting for it as necessary. */
    String readKey() {
        try {
            return _pendingKeys.take();
        } catch (InterruptedException excp) {
            throw new Error("unexpected interrupt");
        }
    }

    /** Set the current score being displayed to SCORE and the current
     *  maximum score to MAXSCORE. */
    public void setScore(int score, int maxScore) {
        setLabel("Score", String.format("Score: %6d / Max score: %6d",
                                        score, maxScore));
    }

    /** Apply the "GAME OVER" label. */
    public void markEnd() {
        _board.markEnd();
    }

    /** The board widget. */
    private BoardDisplay _board;
    /** Queue of pending key presses. */
    private ArrayBlockingQueue<String> _pendingKeys =
        new ArrayBlockingQueue<>(5);

}
