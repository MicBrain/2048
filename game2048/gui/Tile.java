package game2048.gui;

import java.util.HashMap;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.FontMetrics;

import static game2048.gui.BoardDisplay.*;

/** Represents the image of a numbered tile on a 2048 board.
 *  @author P. N. Hilfinger.
 */
class Tile {
    /** Amount to move in TICK milliseconds (in pixels). */
    static final double MOVE_DELTA = 0.016 * ROW_SIZE * TICK;

    /** Fractional increase in size for "bloom effect". */
    static final double BLOOM_FACTOR = 0.1;

    /** Number of ticks over which bloom occurs. */
    static final int BLOOM_STEPS = 15;

    /** Mapping from numbers on tiles to their text and background
     *  colors. */
    static final HashMap<Integer, Color[]> TILE_COLORS = new HashMap<>();

    /** List of tile values and corresponding background and foreground
     *  color values. */
    private static final int[][] TILE_COLOR_MAP = {
        { 2, 0x776e65, 0xeee4da },
        { 4, 0x776e65, 0xede0c8 },
        { 8, 0xf9f6f2, 0xf2b179 },
        { 16, 0xf9f6f2, 0xf59563 },
        { 32, 0xf9f6f2, 0xf67c5f },
        { 64, 0xf9f6f2, 0xf65e3b },
        { 128, 0xf9f6f2, 0xedcf72 },
        { 256, 0xf9f6f2, 0xedcc61 },
        { 512, 0xf9f6f2, 0xedc850 },
        { 1024, 0xf9f6f2, 0xedc53f },
        { 2048, 0xf9f6f2, 0xedc22e },
    };

    static {
        /* { "LABEL", "TEXT COLOR (hex)", "BACKGROUND COLOR (hex)" } */
        for (int[] tileData : TILE_COLOR_MAP) {
            TILE_COLORS.put(tileData[0],
                            new Color[] { new Color(tileData[1]),
                                          new Color(tileData[2]) });
        }
    };

    /** A new tile at (0, 0) displaying VALUE. */
    Tile(int value) {
        _value = value;
        bloom();
    }

    /** Set my position to the square at (ROW, COL). */
    void setPosition(int row, int col) {
        _x = toCoord(col);
        _y = toCoord(row);
    }

    /** Return the value supplied to my constructor. */
    int getValue() {
        return _value;
    }

    /** Return the value after one animation step for a coordinate
     *  transitioning from X0 to X1. */
    double step(double x0, double x1) {
        if (x0 > x1) {
            return Math.max(x1, x0 - MOVE_DELTA);
        } else if (x0 < x1) {
            return Math.min(x1, x0 + MOVE_DELTA);
        } else {
            return x0;
        }
    }

    /** Update my position toward (XDEST, YDEST) and size for one animation
     *  step.  Returns true iff there was a change. */
    boolean tick(double xdest, double ydest) {
        if (xdest != _x || ydest != _y) {
            _x = step(_x, xdest);
            _y = step(_y, ydest);
            return true;
        } else if (_bloom > -1) {
            _bloom -= 1;
            return true;
        }
        return false;
    }

    /** Set me to "bloom". */
    void bloom() {
        _bloom = BLOOM_STEPS;
    }

    /** Render this tile on G. */
    void render(Graphics2D g) {
        int x = (int) Math.rint(_x), y = (int) Math.rint(_y);
        if (_value < 100) {
            g.setFont(TILE_FONT2);
        } else if (_value < 1000) {
            g.setFont(TILE_FONT3);
        } else {
            g.setFont(TILE_FONT4);
        }
        FontMetrics metrics = g.getFontMetrics();
        int bloom =
            (int) Math.rint(TILE_SIDE * BLOOM_FACTOR * (1 + _bloom)
                            / (BLOOM_STEPS + 1));
        g.setColor(TILE_COLORS.get(_value)[1]);
        g.fillRect(x - bloom, y - bloom, 2 * bloom + TILE_SIDE,
                   2 * bloom + TILE_SIDE);
        g.setColor(TILE_COLORS.get(_value)[0]);

        String label = Integer.toString(_value);
        g.drawString(label,
                     x + (TILE_SIDE - metrics.stringWidth(label)) / 2,
                     y + (2 * TILE_SIDE + metrics.getMaxAscent()) / 4);
    }

    /** My tile value. */
    private final int _value;
    /** My current position. */
    private double _x, _y;
    /** Number of animation steps left to bloom. */
    private int _bloom;

}
