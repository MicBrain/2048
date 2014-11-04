package game2048.gui;

import ucb.gui.Pad;

import java.util.ArrayList;

import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.FontMetrics;

/** A widget that displays a 2048 board.
 *  @author P. N. Hilfinger
 */
class BoardDisplay extends Pad {

    /** Colors of empty squares and grid lines. */
    static final Color
        EMPTY_SQUARE_COLOR = new Color(205, 192, 176),
        BAR_COLOR = new Color(184, 173, 158);

    /** Bar width separating tiles and length of tile's side
     *  (pixels). */
    static final int
        TILE_SEP = 15,
        TILE_SIDE = 100,
        ROW_SIZE = TILE_SEP + TILE_SIDE;

    /** Font used for numbering on tiles with <= 2 digits. */
    static final Font TILE_FONT2 = new Font("SansSerif", 1, 48);
    /** Font used for numbering on tiles with 3 digits. */
    static final Font TILE_FONT3 = new Font("SansSerif", 1, 40);
    /** Font used for numbering on tiles with 4 digits. */
    static final Font TILE_FONT4 = new Font("SansSerif", 1, 32);

    /** Color for overlay text on board. */
    static final Color OVERLAY_COLOR = new Color(200, 0, 0, 64);

    /** Font for overlay text on board. */
    static final Font OVERLAY_FONT = new Font("SansSerif", 1, 64);

    /** Wait between animation steps (in milliseconds). */
    static final int TICK = 16;

    /** A graphical representation of a 2048 board with SIZE rows and
     *  columns. */
    public BoardDisplay(int size) {
        _size = size;
        _boardSide = size * ROW_SIZE + TILE_SEP;
        setPreferredSize(_boardSide, _boardSide);
        clear();
    }

    @Override
    protected void setKeyHandler(String event,
                                 Object receiver, String funcName) {
        super.setKeyHandler(event, receiver, funcName);
    }

    /** Clear all tiles from the board. */
    synchronized void clear() {
        _tiles.clear();
        _end = false;
        repaint();
    }

    /** Indicate that "GAME OVER" label should be displayed. */
    synchronized void markEnd() {
        _end = true;
        repaint();
    }

    /** Return the pixel distance corresponding to A rows or columns. */
    static int toCoord(int a) {
        return TILE_SEP + a * ROW_SIZE;
    }

    @Override
    public synchronized void paintComponent(Graphics2D g) {
        g.setColor(EMPTY_SQUARE_COLOR);
        g.fillRect(0, 0, _boardSide, _boardSide);
        g.setColor(BAR_COLOR);
        for (int k = 0; k <= _boardSide; k += ROW_SIZE) {
            g.fillRect(0, k, _boardSide, TILE_SEP);
            g.fillRect(k, 0, TILE_SEP, _boardSide);
        }
        for (Tile tile : _tiles) {
            tile.render(g);
        }
        if (_end) {
            g.setFont(OVERLAY_FONT);
            FontMetrics metrics = g.getFontMetrics();
            g.setColor(OVERLAY_COLOR);
            g.drawString("GAME OVER",
                         (_boardSide
                          - metrics.stringWidth("GAME OVER")) / 2,
                         (2 * _boardSide + metrics.getMaxAscent()) / 4);
        }
    }

    /** Given that TILES represents the state of a board (with TILES[r][c]
     *  being the tile at (r, c), or null if there is no tile
     *  there), TILES2 represents the state of tiles that are to be
     *  merged into existing tiles, and NEXTTILES represents the desired
     *  resulting state, animate the depicted moves and update _tiles
     *  accordingly. */
    public synchronized void displayMoves(Tile[][] tiles,
                                          Tile[][] tiles2,
                                          Tile[][] nextTiles) {
        boolean changing;
        do {
            _tiles.clear();
            changing = false;
            for (int r = 0; r < _size; r += 1) {
                for (int c = 0; c < _size; c += 1) {
                    boolean change;
                    change = false;
                    Tile tile = tiles[r][c];
                    if (tile == null) {
                        continue;
                    }
                    Tile tile2 = tiles2[r][c];
                    double xDest = toCoord(c), yDest = toCoord(r);
                    if (tile.tick(xDest, yDest)) {
                        change = true;
                        _tiles.add(tile);
                    }
                    if (tile2 != null) {
                        if (tile2.tick(xDest, yDest)) {
                            change = true;
                        }
                    }
                    if (change) {
                        _tiles.add(tile);
                        if (tile2 != null) {
                            _tiles.add(tile2);
                        }
                    }
                    if (!change) {
                        Tile next = nextTiles[r][c];
                        next.setPosition(r, c);
                        _tiles.add(next);
                        change = next.tick(xDest, yDest);
                    }
                    changing |= change;
                }
            }
            repaint();
            try {
                wait(TICK);
            } catch (InterruptedException excp) {
                assert false : "Internal error: unexpected interrupt";
            }
        } while (changing);
    }

    /** Convenience method that returns an IllegalArgument exception whose
     *  message is constructed from MSG and ARGS, as for String.format. */
    private IllegalArgumentException badArg(String msg, Object... args) {
        return new IllegalArgumentException(String.format(msg, args));
    }

    /** A list of Tiles currently being displayed. */
    private final ArrayList<Tile> _tiles = new ArrayList<>();

    /** Number of rows and of columns. */
    private final int _size;

    /** Length (in pixels) of the side of the board. */
    private int _boardSide;
    /** True iff "GAME OVER" message is being displayed. */
    private boolean _end;
}
