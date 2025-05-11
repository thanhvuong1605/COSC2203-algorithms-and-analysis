public class State {
    public static final int SIZE = 4; // Puzzle's size is 4 rows and 4 columns
    public static final int[][] GOAL_STATE = { // Final Goal State to achieve
        {1, 2, 3, 4},
        {5, 6, 7, 8},
        {9, 10, 11, 12},
        {13, 14, 15, 0}
    };

    public int[][] state; // Puzzle board
    public State parent; // Previous state that creates this current state
    public int emptyRow, emptyCol; // coordinate of empty tile
    public char move; // Move ('U', 'D', 'L', 'R') to get this state
    public int g, h; // g is height from the root, h is Manhattan distance
    public int depth; // 

    public State(int[][] state, State parent, int emptyRow, int emptyCol, char move) { // default constructors
        this.state = state;
        this.parent = parent;
        this.emptyRow = emptyRow;
        this.emptyCol = emptyCol;
        this.move = move;
        this.g = (parent == null ? 0 : parent.g + 1);
        this.h = calculateManhattan(state);
        this.depth = (parent == null) ? 0 : parent.depth + 1;
    }

    public boolean isGoal() { // check if this board is the GOAL board
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                if (state[i][j] != GOAL_STATE[i][j]) return false; // when catch incorrect tile, return false immediately
        return true; // already looping through the board and find no difference, so two boards are the same
    }

    public String getKey() {
        // Calculate total length needed
        int totalLength = 0;
        for (int[] row : state) {
            for (int val : row) {
                // Count digits in number
                int temp = val;
                do {
                    totalLength++;
                    temp /= 10;
                } while (temp > 0);
                totalLength++; // For comma
            }
        }
        
        char[] result = new char[totalLength];
        int pos = 0;
        
        for (int[] row : state) {
            for (int val : row) {
                // Convert number to string manually
                if (val == 0) {
                    result[pos++] = '0';
                } else {
                    int temp = val;
                    int digits = 0;
                    while (temp > 0) {
                        temp /= 10;
                        digits++;
                    }
                    temp = val;
                    for (int i = digits - 1; i >= 0; i--) {
                        result[pos + i] = (char)('0' + (temp % 10));
                        temp /= 10;
                    }
                    pos += digits;
                }
                result[pos++] = ',';
            }
        }
        return new String(result, 0, pos);
    }

    public int f() { return g + h; } // the smaller f(int) is, the higher priority of state in the queue

    private static int calculateManhattan(int[][] state) { // calculate Manhattan distance, empty tile is not included
        int dist = 0; // start with distance equal 0
        // loop through each tile in board
        for (int i = 0; i < SIZE; i++) { 
            for (int j = 0; j < SIZE; j++) {
                int val = state[i][j]; 
                if (val != 0) { // if value in that tile is not empty
                    int targetRow = (val - 1) / SIZE; 
                    int targetCol = (val - 1) % SIZE;
                    dist += customAbs(i - targetRow) + customAbs(j - targetCol); // sum of distance
                }
            }
        }
        return dist;
    }

    private static int customAbs(int x) {
        return (x < 0) ? -x : x;
    }
} 