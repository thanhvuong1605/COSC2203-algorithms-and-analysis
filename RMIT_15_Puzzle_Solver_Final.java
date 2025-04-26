public class RMIT_15_Puzzle_Solver_Final {

    public static final int SIZE = 4;
    public static final int MAX_MOVES = 1_000_000;
    public static final int[][] GOAL_STATE = {
        {1, 2, 3, 4},
        {5, 6, 7, 8},
        {9, 10, 11, 12},
        {13, 14, 15, 0}
    };

    static class State {
        int[][] state;
        State parent;
        int emptyRow, emptyCol;
        char move;
        int g, h;
        int depth;

        State(int[][] state, State parent, int emptyRow, int emptyCol, char move) {
            this.state = state;
            this.parent = parent;
            this.emptyRow = emptyRow;
            this.emptyCol = emptyCol;
            this.move = move;
            this.g = (parent == null ? 0 : parent.g + 1);
            this.h = calculateManhattan(state);
            this.depth = (parent == null) ? 0 : parent.depth + 1;
        }

        boolean isGoal() {
            for (int i = 0; i < SIZE; i++)
                for (int j = 0; j < SIZE; j++)
                    if (state[i][j] != GOAL_STATE[i][j]) return false;
            return true;
        }

        String getKey() {
            StringBuilder sb = new StringBuilder();
            for (int[] row : state)
                for (int val : row)
                    sb.append(val).append(',');
            return sb.toString();
        }

        int f() { return g + h; }

        static int calculateManhattan(int[][] state) {
            int dist = 0;
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    int val = state[i][j];
                    if (val != 0) {
                        int targetRow = (val - 1) / SIZE;
                        int targetCol = (val - 1) % SIZE;
                        dist += Math.abs(i - targetRow) + Math.abs(j - targetCol);
                    }
                }
            }
            return dist;
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
            if (size >= data.length) grow();
            data[size++] = value;
        }

        public int get(int index) { return data[index]; }

        public int size() { return size; }

        private void grow() {
            int[] newData = new int[data.length * 2];
            for (int i = 0; i < data.length; i++) newData[i] = data[i];
            data = newData;
        }
    }

    static class Queue {
        State[] data;
        int head, tail;

        Queue(int capacity) {
            data = new State[capacity];
            head = 0;
            tail = 0;
        }

        void enqueue(State node) { data[tail++] = node; }
        State dequeue() { return data[head++]; }
        boolean isEmpty() { return head == tail; }
    }

    static class Stack {
        State[] data;
        int top;

        Stack(int capacity) {
            data = new State[capacity];
            top = 0;
        }

        void push(State node) { data[top++] = node; }
        State pop() { return data[--top]; }
        boolean isEmpty() { return top == 0; }
    }

    static class PriorityQueue {
        State[] heap;
        int size;

        PriorityQueue(int capacity) {
            heap = new State[capacity];
            size = 0;
        }

        void add(State node) {
            heap[size] = node;
            siftUp(size);
            size++;
        }

        State poll() {
            State res = heap[0];
            heap[0] = heap[--size];
            siftDown(0);
            return res;
        }

        boolean isEmpty() { return size == 0; }

        private void siftUp(int i) {
            while (i > 0) {
                int p = (i - 1) / 2;
                if (heap[i].f() >= heap[p].f()) break;
                swap(i, p);
                i = p;
            }
        }

        private void siftDown(int i) {
            while (2 * i + 1 < size) {
                int left = 2 * i + 1;
                int right = left + 1;
                int smallest = left;
                if (right < size && heap[right].f() < heap[left].f()) smallest = right;
                if (heap[i].f() <= heap[smallest].f()) break;
                swap(i, smallest);
                i = smallest;
            }
        }

        private void swap(int i, int j) {
            State tmp = heap[i];
            heap[i] = heap[j];
            heap[j] = tmp;
        }
    }

    static class HashSet {
        String[] table;

        HashSet(int capacity) { table = new String[capacity]; }

        int hash(String key) { return Math.abs(key.hashCode()) % table.length; }

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
            while (table[idx] != null) idx = (idx + 1) % table.length;
            table[idx] = key;
        }
    }

    private static int[][] copyState(int[][] state) {
        int[][] newState = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                newState[i][j] = state[i][j];
        return newState;
    }

    private static String buildPath(State goal) {
        StringBuilder sb = new StringBuilder();
        State n = goal;
        while (n != null && n.parent != null) {
            sb.append(n.move);
            n = n.parent;
            if (sb.length() > MAX_MOVES) throw new RuntimeException("Move sequence too long");
        }
        return sb.reverse().toString();
    }

    public String solveBFS(int[][] puzzle) {
        if (!isSolvable(puzzle)) return "UNSOLVABLE";

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
            if (curr.depth > MAX_MOVES) throw new RuntimeException("Move sequence exceeds 1,000,000 moves");
            if (visited.contains(curr.getKey())) continue;
            visited.add(curr.getKey());
            if (curr.isGoal()) return buildPath(curr);

            for (int d = 0; d < 4; d++) {
                int nr = curr.emptyRow + dir[d][0], nc = curr.emptyCol + dir[d][1];
                if (nr < 0 || nr >= SIZE || nc < 0 || nc >= SIZE) continue;
                int[][] newState = copyState(curr.state);
                newState[curr.emptyRow][curr.emptyCol] = newState[nr][nc];
                newState[nr][nc] = 0;
                queue.enqueue(new State(newState, curr, nr, nc, moves[d]));
            }
        }
        return "NO_SOLUTION";
    }

    public String solveDFS(int[][] puzzle) {
        if (!isSolvable(puzzle)) return "UNSOLVABLE";

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
            if (curr.depth > MAX_MOVES) throw new RuntimeException("Move sequence exceeds 1,000,000 moves");
            if (visited.contains(curr.getKey())) continue;
            visited.add(curr.getKey());
            if (curr.isGoal()) return buildPath(curr);

            for (int d = 0; d < 4; d++) {
                int nr = curr.emptyRow + dir[d][0], nc = curr.emptyCol + dir[d][1];
                if (nr < 0 || nr >= SIZE || nc < 0 || nc >= SIZE) continue;
                int[][] newState = copyState(curr.state);
                newState[curr.emptyRow][curr.emptyCol] = newState[nr][nc];
                newState[nr][nc] = 0;
                stack.push(new State(newState, curr, nr, nc, moves[d]));
            }
        }
        return "NO_SOLUTION";
    }

    public String solveAStar(int[][] puzzle) {
        if (!isSolvable(puzzle)) return "UNSOLVABLE";

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
            if (curr.g > MAX_MOVES) throw new RuntimeException("Move sequence exceeds 1,000,000 moves");
            if (visited.contains(curr.getKey())) continue;
            visited.add(curr.getKey());
            if (curr.isGoal()) return buildPath(curr);

            for (int d = 0; d < 4; d++) {
                int nr = curr.emptyRow + dir[d][0], nc = curr.emptyCol + dir[d][1];
                if (nr < 0 || nr >= SIZE || nc < 0 || nc >= SIZE) continue;
                int[][] newState = copyState(curr.state);
                newState[curr.emptyRow][curr.emptyCol] = newState[nr][nc];
                newState[nr][nc] = 0;
                heap.add(new State(newState, curr, nr, nc, moves[d]));
            }
        }
        return "NO_SOLUTION";
    }

    public static boolean isSolvable(int[][] state) {
        IntList flat = new IntList(16);
        for (int[] row : state)
            for (int val : row)
                if (val != 0) flat.add(val);

        int inversions = 0;
        for (int i = 0; i < flat.size(); i++)
            for (int j = i + 1; j < flat.size(); j++)
                if (flat.get(i) > flat.get(j)) inversions++;

        int emptyRow = -1;
        for (int i = 0; i < SIZE && emptyRow == -1; i++)
            for (int j = 0; j < SIZE; j++)
                if (state[i][j] == 0) emptyRow = i;

        int taxicab = Math.abs(emptyRow - 3);
        return (inversions + taxicab) % 2 == 0;
    }

    public static void main(String[] args) {
        RMIT_15_Puzzle_Solver_Final solver = new RMIT_15_Puzzle_Solver_Final();

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
        // System.out.println("A* Solution: " + solver.solveAStar(puzzle));
        // System.out.println("BFS Solution: " + solver.solveBFS(puzzle));
        // System.out.println("DFS Solution: " + solver.solveDFS(puzzle));
        
        System.out.println("A* Solution: " + solver.solveAStar(puzzle_1));
        System.out.println("BFS Solution: " + solver.solveBFS(puzzle_1));
        System.out.println("DFS Solution: " + solver.solveDFS(puzzle_1));
        
    }
}