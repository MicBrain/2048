package game2048.gui;

import java.util.Random;
import java.util.Scanner;
import java.util.InputMismatchException;

/** The input/output and GUI controller for play of a game of 2048.
 *  @author P. N. Hilfinger. */
public class Game {

    /** Probability of choosing 2 as random tile (as opposed to 4). */
    static final double LOW_TILE_PROBABILITY = 0.9;

    /** A new ROWS x ROWS Game whose window title is TITLE.  SEED is a seed for
     *  the PRNG, or 0 to indicate random seeding.  LOG is true iff all keys
     *  pressed and random tiles returned are to be written to standard output.
     *  GRAPHIC is true iff the window is to be displayed.  TESTING is true
     *  iff key pressings and random tiles come from the standard input rather
     *  than user input. */
    public Game(String title, int rows, long seed,
                boolean log, boolean graphic, boolean testing) {
        if (rows < 4) {
            throw new IllegalArgumentException("rows must be >= 4");
        }
        _rows = rows;
        if (seed == 0) {
            _random = new Random();
        } else {
            _random = new Random(seed);
        }
        _log = log;
        _graphic = graphic;
        _testing = testing;

        if (graphic) {
            _display = new GameDisplay(title, rows);
        }
        if (testing) {
            _testInput = new Scanner(System.in);
        }

        clear();
    }

    /** Clear and reset the current state to an empty board. */
    public void clear() {
        _tiles = new Tile[_rows][_rows];
        _tiles2 = new Tile[_rows][_rows];
        _nextTiles = new Tile[_rows][_rows];
        _moves = 0;

        if (_testing) {
            System.out.printf("C%n");
        }
        if (_graphic) {
            _display.clear();
        }
    }

    /** Create a new Tile showing VALUE at ROW and COL. */
    public void addTile(int value, int row, int col) {
        if (_moves != 0) {
            throw badArg("must do pending moves before addTile");
        }
        if (_tiles[row][col] != null) {
            throw badArg("square at (%d, %d) is already occupied", row, col);
        }

        _tiles[row][col] = new Tile(value);
        _tiles[row][col].setPosition(row, col);

        if (_testing) {
            System.out.printf("A %d %d %d%n", value, row, col);
        }
        if (_graphic) {
            _display.displayMoves(_tiles, _tiles2, _tiles);
        }
        _tiles2 = new Tile[_rows][_rows];
        _nextTiles = new Tile[_rows][_rows];
    }

    /** Move a tile whose value is VALUE from (ROW, COL) to (NEWROW, NEWCOL).
     *  An appropriate tile must be present at (ROW, COL). */
    public void moveTile(int value, int row, int col, int newRow, int newCol) {
        Tile tile = _tiles[row][col];
        if (tile == null) {
            throw badArg("no tile at (%d, %d)", row, col);
        }
        if (row == newRow && col == newCol) {
            return;
        }
        if (_tiles2[row][col] != null) {
            throw badArg("tile at (%d, %d) is already merged", row, col);
        } else if (tile.getValue() != value) {
            throw badArg("wrong value (%d) for tile at (%d, %d)",
                         value, row, col);
        } else if (_tiles[newRow][newCol] != null) {
            throw badArg("square at (%d, %d) is occupied", newRow, newCol);
        }

        _moves += 1;
        _tiles[row][col] = null;
        _nextTiles[newRow][newCol] = _tiles[newRow][newCol] = tile;
    }

    /** Move a tile whose value is VALUE from (ROW, COL) to (NEWROW, NEWCOL),
     *  merging it with the tile of the same value that is present there to
     *  create a new one with value NEWVALUE. Appropriate tiles must be
     *  present at (ROW, COL) and (NEWROW, NEWCOL). */
    public void mergeTile(int value, int newValue, int row, int col,
                          int newRow, int newCol) {
        Tile tile = _tiles[row][col];
        if (tile == null) {
            throw badArg("no tile at (%d, %d)", row, col);
        } else if (tile.getValue() != value) {
            throw badArg("wrong value (%d) for tile at (%d, %d)",
                         value, row, col);
        } else if (_tiles[newRow][newCol] == null) {
            throw badArg("no tile to merge with at (%d, %d)", row, col);
        } else if (_tiles2[newRow][newCol] != null) {
            throw badArg("tile at (%d, %d) is already merged", newRow, newCol);
        } else if (_tiles[newRow][newCol].getValue() != tile.getValue()) {
            throw badArg("merging mismatched tiles at (%d, %d)",
                         newRow, newCol);
        }

        _moves += 1;
        _tiles[row][col] = null;
        _tiles2[newRow][newCol] = tile;
        _nextTiles[newRow][newCol] = new Tile(newValue);
    }

    /** Animate and complete all pending moves. Has no effect (and logs no
     *  output) if there are no moves. */
    public void displayMoves() {
        if (_moves == 0) {
            return;
        }
        for (int r = 0; r < _rows; r += 1) {
            for (int c = 0; c < _rows; c += 1) {
                if (_nextTiles[r][c] == null) {
                    _nextTiles[r][c] = _tiles[r][c];
                }
            }
        }
        if (_testing) {
            System.out.printf("D %d %d", _score, _maxScore);
            for (int r = 0; r < _rows; r += 1) {
                for (int c = 0; c < _rows; c += 1) {
                    Tile tile = _nextTiles[r][c];
                    System.out.printf(" %d",
                                      tile == null ? 0 : tile.getValue());
                }
            }
            System.out.println();
        }
        if (_graphic) {
            _display.displayMoves(_tiles, _tiles2, _nextTiles);
        }
        _moves = 0;
        _tiles = _nextTiles;
        _tiles2 = new Tile[_rows][_rows];
        _nextTiles = new Tile[_rows][_rows];
    }

    /** Display SCORE as the current score and MAXSCORE as the current
     *  maximum score so far achieved. */
    public void setScore(int score, int maxScore) {
        _score = score;
        _maxScore = maxScore;
        if (_graphic) {
            _display.setScore(score, maxScore);
        }
    }

    /** Indicate (and possibly log) end of game. */
    public void endGame() {
        if (_testing) {
            System.out.printf("E %d %d%n", _score, _maxScore);
        }
        if (_graphic) {
            _display.markEnd();
        }
    }


    /** Generate the specs for a random tile, ignoring current board contents.
     *  Return a triple { V, R, C }, giving the tile value (either 2 or 4),
     *  row, and column. */
    public int[] getRandomTile() {
        int[] result;
        if (_testing) {
            skipComments();
            _testInput.next("T");
            result = new int[] { _testInput.nextInt(), _testInput.nextInt(),
                                 _testInput.nextInt() };
        } else {
            int value = 2 * (1 + (int) (_random.nextDouble()
                                        / LOW_TILE_PROBABILITY));
            result = new int[] { value, _random.nextInt(_rows),
                                 _random.nextInt(_rows) };
        }
        if (_log) {
            System.out.printf("T %d %d %d%n", result[0], result[1], result[2]);
        }
        return result;
    }

    /** Strings representing the four arrow keys. */
    private static final String[] ARROW_KEYS = {
        "Up", "Down", "Left", "Right"
    };

    /** Return (and log if _log) a random arrow key. */
    public String readRandomKey() {
        String key = ARROW_KEYS[_random.nextInt(4)];
        if (_log) {
            System.out.printf("K%s%n", key);
        }
        return key;
    }

    /** Return (and log if _log) a key input from the user. If _testing,
     *  takes input instead from standard input. */
    public String readKey() {
        String key;
        if (_testing) {
            skipComments();
            if (!_testInput.hasNext()) {
                return "Quit";
            } else if (!_testInput.hasNext("K.*")) {
                throw new InputMismatchException("next input is not key");
            }
            _testInput.findWithinHorizon("\\s*K(.*)", 0);
            key = _testInput.match().group(1);
        } else if (_graphic) {
            key = _display.readKey();
        } else {
            throw new IllegalStateException("Game has no input source");
        }
        if (_log) {
            System.out.printf("K%s%n", key);
        }
        return key;
    }

    /** Skip over any comment lines in input file. */
    private void skipComments() {
        while (_testInput.hasNext("#.*")) {
            _testInput.skip("\\s*#.*");
        }
    }

    /** Return an IllegalArgumentException with the message given by
     *  MSG and ARGS as for String.format. */
    static IllegalArgumentException badArg(String msg, Object... args) {
        return new IllegalArgumentException(String.format(msg, args));
    }

    /** The GUI interface. */
    private GameDisplay _display;
    /** Number of rows and of columns. */
    private int _rows;

    /** The tiles currently on the board. */
    private Tile[][] _tiles;
    /** Tiles that are to be merged with tiles already at the
     *  indicated squares. */
    private Tile[][] _tiles2;
    /** Tiles that will be displayed after next displayMoves call. */
    private Tile[][] _nextTiles;
    /** Number of pending moves to be made by displayMoves. */
    private int _moves;

    /** Score of current game. */
    private int _score;
    /** Running maximum score over all games this session. */
    private int _maxScore;

    /** True if logging input. */
    private boolean _log = false;
    /** True iff displaying GUI. */
    private boolean _graphic = true;
    /** True iff using standard input rather than user input from GUI. */
    private boolean _testing = false;
    /** PRNG for generating random tiles or keys. */
    private final Random _random;
    /** Input source from standard input. */
    private Scanner _testInput;

}
