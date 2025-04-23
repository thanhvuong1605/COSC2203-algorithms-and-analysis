public class RMIT_15_Puzzle_Solver_Astar {

    // Custom IntList class to replace ArrayList
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

    class SimpleSet {
        private String[] keys;
        private int size;
    
        public SimpleSet(int capacity) {
            keys = new String[capacity];
            size = 0;
        }
    
        public boolean contains(String key) {
            for (int i = 0; i < size; i++) {
                if (keys[i].equals(key)) return true;
            }
            return false;
        }
    
        public void add(String key) {
            if (!contains(key)) {
                keys[size++] = key;
            }
        }
    }

    class MyHashSet {
        // Each bucket is a simple linked list of entries
        static class Node {
            String key;
            Node next;
    
            Node(String key) {
                this.key = key;
            }
        }
    
        private final int CAPACITY = 1024;  // You can increase if needed
        private Node[] buckets;
    
        public MyHashSet() {
            buckets = new Node[CAPACITY];
        }
    
        private int hash(String key) {
            // Ensure non-negative and within bucket range
            return Math.abs(key.hashCode()) % CAPACITY;
        }
    
        public boolean contains(String key) {
            int index = hash(key);
            Node curr = buckets[index];
            while (curr != null) {
                if (curr.key.equals(key)) return true;
                curr = curr.next;
            }
            return false;
        }
    
        public void add(String key) {
            if (contains(key)) return;
    
            int index = hash(key);
            Node newNode = new Node(key);
            newNode.next = buckets[index];
            buckets[index] = newNode;
        }
    }

    class BinaryHeapPriorityQueue {
        private State[] heap;
        private int size;
    
        public BinaryHeapPriorityQueue(int capacity) {
            heap = new State[capacity];
            size = 0;
        }
    
        public void add(State state) {
            if (size >= heap.length) {
                grow();
            }
            heap[size] = state;
            siftUp(size);
            size++;
        }
    
        public State poll() {
            if (size == 0) return null;
            State top = heap[0];
            heap[0] = heap[--size];
            siftDown(0);
            return top;
        }
    
        public boolean isEmpty() {
            return size == 0;
        }
    
        private void siftUp(int index) {
            while (index > 0) {
                int parent = (index - 1) / 2;
                if (heap[index].compareTo(heap[parent]) >= 0) break;
                swap(index, parent);
                index = parent;
            }
        }
    
        private void siftDown(int index) {
            while (2 * index + 1 < size) {
                int left = 2 * index + 1;
                int right = left + 1;
                int smallest = left;
    
                if (right < size && heap[right].compareTo(heap[left]) < 0) {
                    smallest = right;
                }
    
                if (heap[index].compareTo(heap[smallest]) <= 0) break;
    
                swap(index, smallest);
                index = smallest;
            }
        }
    
        private void swap(int i, int j) {
            State temp = heap[i];
            heap[i] = heap[j];
            heap[j] = temp;
        }
    
        private void grow() {
            State[] newHeap = new State[heap.length * 2];
            for (int i = 0; i < heap.length; i++) {
                newHeap[i] = heap[i];
            }
            heap = newHeap;
        }
    }

    static class State implements Comparable<State> {
        int emptyRow, emptyCol;
        int g;
        String moves;
        int h;
        private int[][] initialBoard;

        State(int emptyRow, int emptyCol, int g, String moves, int[][] initialBoard) {
            this.emptyRow = emptyRow;
            this.emptyCol = emptyCol;
            this.g = g;
            this.moves = moves;
            this.initialBoard = initialBoard;
            this.h = calculateManhattan();
        }

        private int[][] reconstructBoard() {
            int[][] board = new int[4][4];
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++)
                    board[i][j] = initialBoard[i][j];

            int row = -1, col = -1;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (board[i][j] == 0) {
                        row = i;
                        col = j;
                        break;
                    }
                }
            }

            for (char move : moves.toCharArray()) {
                int newRow = row, newCol = col;
                if (move == 'U') newRow++;
                else if (move == 'D') newRow--;
                else if (move == 'L') newCol++;
                else if (move == 'R') newCol--;
                board[row][col] = board[newRow][newCol];
                board[newRow][newCol] = 0;
                row = newRow;
                col = newCol;
            }
            return board;
        }

        int calculateManhattan() {
            int[][] board = reconstructBoard();
            int dist = 0;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    int val = board[i][j];
                    if (val == 0) continue;
                    int targetRow = (val - 1) / 4;
                    int targetCol = (val - 1) % 4;
                    dist += Math.abs(i - targetRow) + Math.abs(j - targetCol);
                }
            }
            return dist;
        }

        String getStateKey() {
            int[][] board = reconstructBoard();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++)
                    sb.append(board[i][j]).append(",");
            return sb.toString();
        }

        @Override
        public int compareTo(State other) {
            int f1 = this.g + this.h;
            int f2 = other.g + other.h;
            if (f1 != f2) return f1 - f2;
            return Integer.compare(this.moves.length(), other.moves.length());
        }
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

    public String solve(int[][] puzzle) {
        if (!isSolvable(puzzle)) {
            throw new IllegalArgumentException("Unsolvable puzzle");
        }

        int emptyRow = -1, emptyCol = -1;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (puzzle[i][j] == 0) {
                    emptyRow = i;
                    emptyCol = j;
                    break;
                }
            }
            if (emptyRow != -1) break;
        }

        BinaryHeapPriorityQueue queue = new BinaryHeapPriorityQueue(10000);
        MyHashSet visited = new MyHashSet();

        State initial = new State(emptyRow, emptyCol, 0, "", puzzle);
        queue.add(initial);

        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        String[] moveNames = {"U", "D", "L", "R"};

        while (!queue.isEmpty()) {
            State current = queue.poll();
            String stateKey = current.getStateKey();

            if (visited.contains(stateKey)) continue;
            visited.add(stateKey);

            if (current.h == 0) {
                return current.moves;
            }

            for (int d = 0; d < 4; d++) {
                int newRow = current.emptyRow + directions[d][0];
                int newCol = current.emptyCol + directions[d][1];
                if (newRow < 0 || newRow >= 4 || newCol < 0 || newCol >= 4) continue;

                String newMoves = current.moves + moveNames[d];
                if (newMoves.length() > 1_000_000) {
                    throw new RuntimeException("Move sequence too long");
                }

                State nextState = new State(newRow, newCol, current.g + 1, newMoves, puzzle);
                if (!visited.contains(nextState.getStateKey())) {
                    queue.add(nextState);
                }
            }
        }

        throw new RuntimeException("No solution found");
    }

    public static void main(String[] args) {
        RMIT_15_Puzzle_Solver_Astar solver = new RMIT_15_Puzzle_Solver_Astar();

        int[][] puzzle = {
            {1, 6, 2, 0},
            {9, 5, 4, 3},
            {13, 11, 7, 8},
            {14, 10, 15, 12}
        };

        int[][] puzzle_1 = {
            {1, 2, 3, 4},
            {5, 6, 7, 8},
            {9, 10, 11, 12},
            {13, 0, 14, 15}
        };

        try {
            String solution = solver.solve(puzzle_1);
            System.out.println("Solution: " + solution);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}