public class RMIT_15_Puzzle_Solver_BFS_DFS {

    public static final int SIZE = 4;
    public static final int[][] GOAL_BOARD = {
        {1, 2, 3, 4},
        {5, 6, 7, 8},
        {9, 10, 11, 12},
        {13, 14, 15, 0}
    };

    static class Node {
        int[][] board;
        Node parent;
        int emptyRow, emptyCol;
        char move;
        int depth;

        Node(int[][] board, Node parent, int emptyRow, int emptyCol, char move) {
            this.board = board;
            this.parent = parent;
            this.emptyRow = emptyRow;
            this.emptyCol = emptyCol;
            this.move = move;
            this.depth = (parent == null) ? 0 : parent.depth + 1;
        }

        boolean isGoal() {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (board[i][j] != GOAL_BOARD[i][j]) return false;
                }
            }
            return true;
        }

        String getKey() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    sb.append(board[i][j]).append(',');
                }
            }
            return sb.toString();
        }
    }

    static class IntList {
        private int[] data;
        private int size;

        public IntList(int capacity) {
            data = new int[capacity];
            size = 0;
        }

        public void add(int value) {
            if (size >= data.length) {
                grow();
            }
            data[size++] = value;
        }

        public int get(int index) {
            return data[index];
        }

        public int size() {
            return size;
        }

        private void grow() {
            int[] newData = new int[data.length * 2];
            for (int i = 0; i < data.length; i++) {
                newData[i] = data[i];
            }
            data = newData;
        }
    }

    static class SimpleQueue {
        Node[] data;
        int head, tail;

        SimpleQueue(int capacity) {
            data = new Node[capacity];
            head = 0;
            tail = 0;
        }

        void enqueue(Node node) {
            data[tail++] = node;
        }

        Node dequeue() {
            return data[head++];
        }

        boolean isEmpty() {
            return head == tail;
        }
    }

    static class SimpleStack {
        Node[] data;
        int top;

        SimpleStack(int capacity) {
            data = new Node[capacity];
            top = 0;
        }

        void push(Node node) {
            data[top++] = node;
        }

        Node pop() {
            return data[--top];
        }

        boolean isEmpty() {
            return top == 0;
        }
    }

    static class SimpleHashSet {
        String[] table;

        SimpleHashSet(int capacity) {
            table = new String[capacity];
        }

        int hash(String key) {
            return Math.abs(key.hashCode()) % table.length;
        }

        boolean contains(String key) {
            int idx = hash(key);
            while (table[idx] != null) {
                if (table[idx].equals(key)) return true;
                idx = (idx + 1) % table.length;
            }
            return false;
        }

        void add(String key) {
            int idx = hash(key);
            while (table[idx] != null) {
                idx = (idx + 1) % table.length;
            }
            table[idx] = key;
        }
    }

    public String solveBFS(int[][] puzzle) {
        if (!isSolvable(puzzle)) return "UNSOLVABLE";

        int emptyR = -1, emptyC = -1;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (puzzle[i][j] == 0) {
                    emptyR = i;
                    emptyC = j;
                }
            }
        }

        SimpleQueue queue = new SimpleQueue(1000000);
        SimpleHashSet visited = new SimpleHashSet(1000000);

        queue.enqueue(new Node(puzzle, null, emptyR, emptyC, 'S'));

        int[][] dir = {{1,0}, {-1,0}, {0,1}, {0,-1}};
        char[] moves = {'U', 'D', 'L', 'R'};

        while (!queue.isEmpty()) {
            Node curr = queue.dequeue();

            if (curr.depth > 1_000_000) {
                throw new RuntimeException("Move sequence exceeds 1,000,000 moves");
            }

            if (visited.contains(curr.getKey())) continue;
            visited.add(curr.getKey());

            if (curr.isGoal()) return buildPath(curr);

            for (int d = 0; d < 4; d++) {
                int nr = curr.emptyRow + dir[d][0];
                int nc = curr.emptyCol + dir[d][1];
                if (nr < 0 || nr >= SIZE || nc < 0 || nc >= SIZE) continue;

                int[][] newBoard = copyBoard(curr.board);
                newBoard[curr.emptyRow][curr.emptyCol] = newBoard[nr][nc];
                newBoard[nr][nc] = 0;

                queue.enqueue(new Node(newBoard, curr, nr, nc, moves[d]));
            }
        }

        return "NO_SOLUTION";
    }

    public String solveDFS(int[][] puzzle) {
        if (!isSolvable(puzzle)) return "UNSOLVABLE";

        int emptyR = -1, emptyC = -1;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (puzzle[i][j] == 0) {
                    emptyR = i;
                    emptyC = j;
                }
            }
        }

        SimpleStack stack = new SimpleStack(1000000000);
        SimpleHashSet visited = new SimpleHashSet(10000000);

        stack.push(new Node(puzzle, null, emptyR, emptyC, 'S'));

        int[][] dir = {{1,0}, {-1,0}, {0,1}, {0,-1}};
        char[] moves = {'U', 'D', 'L', 'R'};

        while (!stack.isEmpty()) {
            Node curr = stack.pop();

            if (curr.depth > 1_000_000) {
                throw new RuntimeException("Move sequence exceeds 1,000,000 moves");
            }

            if (visited.contains(curr.getKey())) continue;
            visited.add(curr.getKey());

            if (curr.isGoal()) return buildPath(curr);

            for (int d = 0; d < 4; d++) {
                int nr = curr.emptyRow + dir[d][0];
                int nc = curr.emptyCol + dir[d][1];
                if (nr < 0 || nr >= SIZE || nc < 0 || nc >= SIZE) continue;

                int[][] newBoard = copyBoard(curr.board);
                newBoard[curr.emptyRow][curr.emptyCol] = newBoard[nr][nc];
                newBoard[nr][nc] = 0;

                stack.push(new Node(newBoard, curr, nr, nc, moves[d]));
            }
        }

        return "NO_SOLUTION";
    }

    private String buildPath(Node goal) {
        StringBuilder sb = new StringBuilder();
        Node n = goal;
        while (n != null && n.parent != null) {
            sb.append(n.move);
            n = n.parent;

            // if (sb.length() > 1_000_000) {
            //     throw new RuntimeException("Move sequence exceeds 1,000,000 moves");
            // }
        }
        return sb.reverse().toString();
    }

    private int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                newBoard[i][j] = board[i][j];
            }
        }
        return newBoard;
    }

    public static boolean isSolvable(int[][] board) {
        IntList flat = new IntList(16);
        
        // Flatten the board and remove the empty tile (0)
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (board[i][j] != 0) {
                    flat.add(board[i][j]);
                }
            }
        }

        // Count inversions
        int inversions = 0;
        for (int i = 0; i < flat.size(); i++) {
            for (int j = i + 1; j < flat.size(); j++) {
                if (flat.get(i) > flat.get(j)) {
                    inversions++;
                }
            }
        }

        // Find empty tile (0) position
        int emptyRow = -1, emptyCol = -1;
        for (int i = 0; i < 4 && emptyRow == -1; i++) {
            for (int j = 0; j < 4; j++) {
                if (board[i][j] == 0) {
                    emptyRow = i;
                    emptyCol = j;
                    break;
                }
            }
        }

        // Taxicab distance from goal (3, 3)
        int taxicab = Math.abs(emptyRow - 3) + Math.abs(emptyCol - 3);
        int parity = (inversions + taxicab) % 2;

        System.out.printf("Inversions: %d, Empty Row: %d, Empty Col: %d, Taxicab: %d, Parity: %d%n",
                          inversions, emptyRow, emptyCol, taxicab, parity);

        return parity == 0; // Solvable if even
    }
    public static void main(String[] args) {
        RMIT_15_Puzzle_Solver_BFS_DFS solver = new RMIT_15_Puzzle_Solver_BFS_DFS();

        int[][] puzzle = {
            {1, 6, 2, 0},
            {9, 5, 4, 3},
            {13, 11, 7, 8},
            {14, 10, 15, 12}
        };

        int[][] puzzle1 = {
            {1, 2, 3, 4},
            {5, 6, 7, 8},
            {9, 10, 11, 12},
            {13, 0, 14, 15}
        };

        System.out.println("BFS Solution:");
        System.out.println(solver.solveBFS(puzzle));

        System.out.println("DFS Solution:");
        System.out.println(solver.solveDFS(puzzle));
    }
}
