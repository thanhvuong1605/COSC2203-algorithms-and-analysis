public class RMIT_15_Puzzle_Solver_Final {
    public static final int SIZE = 4; // Puzzle's size is 4 rows and 4 columns
    public static final int MAX_MOVES = 1_000_000; // Maximum path is 1 millions
    public static final int[][] GOAL_STATE = { // Final Goal State to achieve
        {1, 2, 3, 4},
        {5, 6, 7, 8},
        {9, 10, 11, 12},
        {13, 14, 15, 0}
    };

    private static int[][] copyState(int[][] state) { // copy board 
        int[][] newState = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                newState[i][j] = state[i][j];
        return newState;
    }

    private static String buildPath(State goal) {
        char[] path = new char[MAX_MOVES];
        int length = 0;
        State n = goal;
        
        // Skip the initial 'S' move
        while (n != null && n.parent != null) {
            path[length++] = n.move;
            n = n.parent;
            if (length > MAX_MOVES) throw new RuntimeException("Move sequence too long");
        }
        
        // Reverse the path
        for (int i = 0; i < length / 2; i++) {
            char temp = path[i];
            path[i] = path[length - 1 - i];
            path[length - 1 - i] = temp;
        }
        
        return new String(path, 0, length);
    }

    private static int customAbs(int x) {
        return (x < 0) ? -x : x;
    }

    public String solveBFS(int[][] puzzle) {
        if (!isSolvable(puzzle)) return "UNSOLVABLE";

        long startTime = System.currentTimeMillis();
        int exploredStates = 0;

        int emptyR = -1, emptyC = -1;
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                if (puzzle[i][j] == 0) { emptyR = i; emptyC = j; }

        Queue queue = new Queue(1000000);
        HashSet visited = new HashSet(1000000);
        queue.enqueue(new State(puzzle, null, emptyR, emptyC, 'S'));

        int[][] dir = {{1,0}, {-1,0}, {0,1}, {0,-1}};
        char[] moves = {'U', 'D', 'L', 'R'};

        while (!queue.isEmpty()) {
            State curr = queue.dequeue();
            exploredStates++;
            
            if (curr.depth > MAX_MOVES) {
                long endTime = System.currentTimeMillis();
                return String.format("TIME_LIMIT_EXCEEDED (%.2f ms, %d states explored, moves: 0)", 
                    (endTime - startTime) / 1000.0, exploredStates);
            }
            
            if (visited.contains(curr.getKey())) continue;
            visited.add(curr.getKey());
            if (curr.isGoal()) {
                String path = buildPath(curr);
                long endTime = System.currentTimeMillis();
                return String.format("%s (%.2f ms, %d states explored, moves: %d)", 
                    path, (endTime - startTime) / 1000.0, exploredStates, path.length());
            }

            for (int d = 0; d < 4; d++) {
                int nr = curr.emptyRow + dir[d][0], nc = curr.emptyCol + dir[d][1];
                if (nr < 0 || nr >= SIZE || nc < 0 || nc >= SIZE) continue;
                int[][] newState = copyState(curr.state);
                newState[curr.emptyRow][curr.emptyCol] = newState[nr][nc];
                newState[nr][nc] = 0;
                queue.enqueue(new State(newState, curr, nr, nc, moves[d]));
            }
        }
        long endTime = System.currentTimeMillis();
        return String.format("NO_SOLUTION (%.2f ms, %d states explored, moves: 0)", 
            (endTime - startTime) / 1000.0, exploredStates);
    }

    public String solveDFS(int[][] puzzle) {
        if (!isSolvable(puzzle)) return "UNSOLVABLE";

        long startTime = System.currentTimeMillis();
        int exploredStates = 0;

        int emptyR = -1, emptyC = -1;
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                if (puzzle[i][j] == 0) { emptyR = i; emptyC = j; }

        Stack stack = new Stack(1000000);
        HashSet visited = new HashSet(1000000);
        stack.push(new State(puzzle, null, emptyR, emptyC, 'S'));

        int[][] dir = {{1,0}, {-1,0}, {0,1}, {0,-1}};
        char[] moves = {'U', 'D', 'L', 'R'};

        while (!stack.isEmpty()) {
            State curr = stack.pop();
            exploredStates++;
            
            if (curr.depth > MAX_MOVES) {
                long endTime = System.currentTimeMillis();
                return String.format("TIME_LIMIT_EXCEEDED (%.2f ms, %d states explored, moves: 0)", 
                    (endTime - startTime) / 1000.0, exploredStates);
            }
            
            if (visited.contains(curr.getKey())) continue;
            visited.add(curr.getKey());
            if (curr.isGoal()) {
                String path = buildPath(curr);
                long endTime = System.currentTimeMillis();
                return String.format("%s (%.2f ms, %d states explored, moves: %d)", 
                    path, (endTime - startTime) / 1000.0, exploredStates, path.length());
            }

            for (int d = 0; d < 4; d++) {
                int nr = curr.emptyRow + dir[d][0], nc = curr.emptyCol + dir[d][1];
                if (nr < 0 || nr >= SIZE || nc < 0 || nc >= SIZE) continue;
                int[][] newState = copyState(curr.state);
                newState[curr.emptyRow][curr.emptyCol] = newState[nr][nc];
                newState[nr][nc] = 0;
                stack.push(new State(newState, curr, nr, nc, moves[d]));
            }
        }
        long endTime = System.currentTimeMillis();
        return String.format("NO_SOLUTION (%.2f ms, %d states explored, moves: 0)", 
            (endTime - startTime) / 1000.0, exploredStates);
    }

    public String solveAStar(int[][] puzzle) {
        if (!isSolvable(puzzle)) return "UNSOLVABLE";

        long startTime = System.currentTimeMillis();
        int exploredStates = 0;

        int emptyR = -1, emptyC = -1;
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                if (puzzle[i][j] == 0) { emptyR = i; emptyC = j; }

        PriorityQueue heap = new PriorityQueue(1000000);
        HashSet visited = new HashSet(1000000);
        heap.add(new State(puzzle, null, emptyR, emptyC, 'S'));

        int[][] dir = {{1,0}, {-1,0}, {0,1}, {0,-1}};
        char[] moves = {'U', 'D', 'L', 'R'};

        while (!heap.isEmpty()) {
            State curr = heap.poll();
            exploredStates++;
            
            if (curr.g > MAX_MOVES) {
                long endTime = System.currentTimeMillis();
                return String.format("TIME_LIMIT_EXCEEDED (%.2f ms, %d states explored, moves: 0)", 
                    (endTime - startTime) / 1000.0, exploredStates);
            }
            
            if (visited.contains(curr.getKey())) continue;
            visited.add(curr.getKey());
            if (curr.isGoal()) {
                String path = buildPath(curr);
                long endTime = System.currentTimeMillis();
                return String.format("%s (%.2f ms, %d states explored, moves: %d)", 
                    path, (endTime - startTime) / 1000.0, exploredStates, path.length());
            }

            for (int d = 0; d < 4; d++) {
                int nr = curr.emptyRow + dir[d][0], nc = curr.emptyCol + dir[d][1];
                if (nr < 0 || nr >= SIZE || nc < 0 || nc >= SIZE) continue;
                int[][] newState = copyState(curr.state);
                newState[curr.emptyRow][curr.emptyCol] = newState[nr][nc];
                newState[nr][nc] = 0;
                heap.add(new State(newState, curr, nr, nc, moves[d]));
            }
        }
        long endTime = System.currentTimeMillis();
        return String.format("NO_SOLUTION (%.2f ms, %d states explored, moves: 0)", 
            (endTime - startTime) / 1000.0, exploredStates);
    }

    public static boolean isSolvable(int[][] state) {
        // write out the 15-puzzle board in one line, reading left-to-right, top-to-bottom
        IntList flat = new IntList(16);
        for (int[] row : state)
            for (int val : row)
                if (val != 0) flat.add(val);

        // calculate the inversion
        int inversions = 0;
        for (int i = 0; i < flat.size(); i++)
            for (int j = i + 1; j < flat.size(); j++)
                if (flat.get(i) > flat.get(j)) inversions++;

        // find the row index containing zero tile
        int emptyRow = -1;
        for (int i = 0; i < SIZE && emptyRow == -1; i++)
            for (int j = 0; j < SIZE; j++)
                if (state[i][j] == 0) emptyRow = i;

        int taxicab = customAbs(emptyRow - 3); // absolute difference between the blank tile's row (emptyRow) and the target row
        return (inversions + taxicab) % 2 == 0; // puzzle is solvable  if the sum of the inversions and the row distance from the goal row is even
    }

    public static void main(String[] args) {
        RMIT_15_Puzzle_Solver_Final solver = new RMIT_15_Puzzle_Solver_Final();

        int[][] puzzle_test_1 = {
            {1, 6, 2, 0},
            {9, 5, 4, 3},
            {13, 11, 7, 8},
            {14, 10, 15, 12}
        };

        int[][] puzzle_test_2 = {
            {1, 2, 3, 4},
            {5, 6, 7, 8},
            {9, 10, 11, 12},
            {13, 0, 14, 15}
        };

        //easy puzzle
        int[][] puzzle_easy = {
            {1, 2, 3, 4},
            {5, 6, 7, 8},
            {9, 0, 11, 12},
            {13, 10, 14, 15}
        };

        //medium puzzle
        int[][] puzzle_medium = {
            {1, 3, 4, 8},
            {5, 2, 0, 7},
            {9, 6, 11, 12},
            {13, 10, 14, 15}    
        };

        //hard puzzle
        int[][] puzzle_hard = {
            {1, 2, 3, 4},
            {5, 6, 8, 10},
            {7, 15, 0, 13},
            {14, 11, 9, 12}
        };

        // Test all puzzles
        int[][][] puzzles = { puzzle_easy, puzzle_medium, puzzle_hard};
        String[] names = { "Easy", "Medium", "Hard"};

        for (int i = 0; i < puzzles.length; i++) {
            System.out.println("\nTesting " + names[i] + " Puzzle:");
            System.out.println("A* Solution: " + solver.solveAStar(puzzles[i]));
            System.out.println("BFS Solution: " + solver.solveBFS(puzzles[i]));
            System.out.println("DFS Solution: " + solver.solveDFS(puzzles[i]));
        }
    }
}