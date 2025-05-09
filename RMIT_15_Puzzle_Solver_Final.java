public class RMIT_15_Puzzle_Solver_Final {

    public static final int SIZE = 4; // Puzzle's size is 4 rows and 4 columns
    public static final int MAX_MOVES = 1_000_000; // Maximum path is 1 millions
    public static final int[][] GOAL_STATE = { // Final Goal State to achieve
        {1, 2, 3, 4},
        {5, 6, 7, 8},
        {9, 10, 11, 12},
        {13, 14, 15, 0}
    };

    static class State { // Node, used in A* method
        int[][] state; // Puzzle board
        State parent; // Previous state that creates this current state
        int emptyRow, emptyCol; // coordinate of empty tile
        char move; // Move ('U', 'D', 'L', 'R') to get this state
        int g, h; // g is height from the root, h is Manhattan distance
        int depth; // 

        State(int[][] state, State parent, int emptyRow, int emptyCol, char move) { // default constructors
            this.state = state;
            this.parent = parent;
            this.emptyRow = emptyRow;
            this.emptyCol = emptyCol;
            this.move = move;
            this.g = (parent == null ? 0 : parent.g + 1);
            this.h = calculateManhattan(state);
            this.depth = (parent == null) ? 0 : parent.depth + 1;
        }

        boolean isGoal() { // check if this board is the GOAL board
            for (int i = 0; i < SIZE; i++)
                for (int j = 0; j < SIZE; j++)
                    if (state[i][j] != GOAL_STATE[i][j]) return false; // when catch incorrect tile, return false immediately
            return true; // already looping through the board and find no difference, so two boards are the same
        }

        // String getKey() { // create key (String) for State by writing the every tile in one line, separated by comma
        //     StringBuilder sb = new StringBuilder();
        //     for (int[] row : state)
        //         for (int val : row)
        //             sb.append(val).append(',');
        //     return sb.toString();
        // }

        String getKey() {
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

        int f() { return g + h; } // the smaller f(int) is, the higher priority of state in the queue

        static int calculateManhattan(int[][] state) { // calculate Manhattan distance, empty tile is not included
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
    }

    static class IntList { // Integer Array to store all tile's value, used in function isSolvable()
        private int[] data;
        private int size;

        public IntList(int capacity) { // default constructor
            data = new int[capacity];
            size = 0;
        }

        public void add(int value) { // add new values
            if (size >= data.length) grow(); // when current array's size excceeds, increase array's size 2 times
            data[size++] = value; // allocate new value at the size-th index, then increase the size variables
        }

        public int get(int index) { return data[index]; } // get value at index location

        public int size() { return size; } // get the current size

        private void grow() { // increase the array's size by 2 times
            int[] newData = new int[data.length * 2]; // assign new array with size is larger 2 times than the current array's size
            for (int i = 0; i < data.length; i++) newData[i] = data[i]; // copy every element from old array to new array
            data = newData; // pointing to new array
        }
    }

    static class Queue { // Standard Queue (FIFO) (Array), used in BFS
        State[] data; // State array
        int head, tail; // pointers to the head node and tail node

        Queue(int capacity) { // default constructor of Queue class  
            data = new State[capacity];
            head = 0;
            tail = 0;
        }

        void enqueue(State node) { data[tail++] = node; } // add new node at the end of the line, then increase the tail value
        State dequeue() { return data[head++]; } // return the head node's value, then increase the head value
        boolean isEmpty() { return head == tail; } // queue is empty if head catches tail
    }

    static class Stack { // Standard Stack (LIFO) (Array), used in DFS approach
        State[] data; // State array
        int top; // pointer to top node

        Stack(int capacity) { // default constructor of Stack class
            data = new State[capacity];
            top = 0;
        }

        void push(State node) { data[top++] = node; } // add new element to stack by putting it in the top of line
        State pop() { return data[--top]; } // remove the top, then decrease the pointer 
        boolean isEmpty() { return top == 0; } // Stack is empty if top is 0
    }

    static class PriorityQueue { // Priority Queue (Min-Heap Array), used in the A* method.
        State[] heap;
        int size;

        PriorityQueue(int capacity) {
            heap = new State[capacity];
            size = 0;
        }

        void add(State node) { // add new node
            heap[size] = node; 
            siftUp(size); // ensure the lower f is always shifted up.
            size++;
        }

        State poll() { // remove and return the node at the front of the queue
            State res = heap[0];
            heap[0] = heap[--size];
            siftDown(0);
            return res;
        }

        boolean isEmpty() { return size == 0; }

        private void siftUp(int i) { // move the lower f to the top 
            while (i > 0) {
                int p = (i - 1) / 2;
                if (heap[i].f() >= heap[p].f()) break;
                swap(i, p);
                i = p;
            }
        }

        private void siftDown(int i) { // move the higher f to the bottom
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

        private void swap(int i, int j) { // swap two States
            State tmp = heap[i];
            heap[i] = heap[j];
            heap[j] = tmp;
        }
    }

    static class HashSet { // Hash Table (Array) store the visited states, so that we do not check it again (used for all 3 methods)
        String[] table;

        HashSet(int capacity) { table = new String[capacity]; } // default constructor for Hashset

        // int hash(String key) { return Math.abs(key.hashCode()) % table.length; } // return index

        // Custom hash function to replace String.hashCode()
        private int customHash(String key) {
            int hash = 0;
            for (int i = 0; i < key.length(); i++) {
                hash = (hash * 31 + key.charAt(i)) % table.length;
            }
            return hash;
        }

        int hash(String key) { return customHash(key); }

        private boolean customEquals(String s1, String s2) {
            if (s1.length() != s2.length()) return false;
            for (int i = 0; i < s1.length(); i++) {
                if (s1.charAt(i) != s2.charAt(i)) return false;
            }
            return true;
        }

        boolean contains(String key) { // check if the hash Table contains the elements already 
            int idx = hash(key);
            while (table[idx] != null) { 
                if (customEquals(table[idx], key)) return true;
                idx = (idx + 1) % table.length;
            }
            return false;
        }

        void add(String key) { // add new element (Open Address Hashing - Linear Probing)
            int idx = hash(key);
            while (table[idx] != null) idx = (idx + 1) % table.length; // if the current index is occupied, move to next index
            table[idx] = key; // allocate element to array
        }
    }

    private static int[][] copyState(int[][] state) { // copy board 
        int[][] newState = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                newState[i][j] = state[i][j];
        return newState;
    }

    // private static String buildPath(State goal) { // Path (String) created from combining all the moves from start to current node
    //     StringBuilder sb = new StringBuilder();
    //     State n = goal; 
    //     while (n != null && n.parent != null) {
    //         sb.append(n.move);  
    //         n = n.parent;
    //         if (sb.length() > MAX_MOVES) throw new RuntimeException("Move sequence too long"); // if longer than 1 million moves, raise error
    //     }
    //     return sb.reverse().toString(); // reverse the string, because it is appended from current node to start
    // }

    private static String buildPath(State goal) {
        char[] path = new char[MAX_MOVES];
        int length = 0;
        State n = goal;
        
        while (n != null) {
            path[length++] = n.move;
            n = n.parent;
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

    public String solveBFS(int[][] puzzle) { // BFS method
        if (!isSolvable(puzzle)) return "UNSOLVABLE"; // check if puzzle is solvable or not

        // find the coordinate of the empty tile
        int emptyR = -1, emptyC = -1;
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                if (puzzle[i][j] == 0) { emptyR = i; emptyC = j; } 

        Queue queue = new Queue(1000000); // store unexplored states (FIFO)
        HashSet visited = new HashSet(1000000); // store explored states
        queue.enqueue(new State(puzzle, null, emptyR, emptyC, 'S')); // start the process by enqueuing original state to queue

        int[][] dir = {{1,0}, {-1,0}, {0,1}, {0,-1}}; // changes in coordinates (x, y) when moving Up, Down, Left, Right, respectively.
        char[] moves = {'U', 'D', 'L', 'R'};

        while (!queue.isEmpty()) { // continue until no elements left in queue
            State curr = queue.dequeue(); // dequeue the first element
            if (curr.depth > MAX_MOVES) throw new RuntimeException("Move sequence exceeds 1,000,000 moves"); // continously checking if move is larger than 1 million
            if (visited.contains(curr.getKey())) continue; // if current state is in visited set, which is already explored, ignore it 
            visited.add(curr.getKey()); // if it is not, add to visited set
            if (curr.isGoal()) return buildPath(curr); // compare with Goal state, if yes, return path and end

            // generate 4 new states in 4 directions: U, D, L, R; then add them to queue
            for (int d = 0; d < 4; d++) {
                int nr = curr.emptyRow + dir[d][0], nc = curr.emptyCol + dir[d][1]; // new coordinate of empty tile
                if (nr < 0 || nr >= SIZE || nc < 0 || nc >= SIZE) continue; // if new coordinate of empty tile is invalid (outside puzzle), ignore this state
                int[][] newState = copyState(curr.state); 
                newState[curr.emptyRow][curr.emptyCol] = newState[nr][nc];
                newState[nr][nc] = 0;
                queue.enqueue(new State(newState, curr, nr, nc, moves[d])); // enqueue new state into queue to explore next time 
            }
        }
        return "NO_SOLUTION";
    }

    public String solveDFS(int[][] puzzle) { // DFS method
        if (!isSolvable(puzzle)) return "UNSOLVABLE"; // check if puzzle is solvable or not

        // find the coordinate of the empty tile
        int emptyR = -1, emptyC = -1;
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                if (puzzle[i][j] == 0) { emptyR = i; emptyC = j; }

        Stack stack = new Stack(1000000); // use Stack to store unexplored states (LIFO)
        HashSet visited = new HashSet(1000000); // use Hash Table to store explored states
        stack.push(new State(puzzle, null, emptyR, emptyC, 'S')); // start the process by pushing original puzzle first

        int[][] dir = {{1,0}, {-1,0}, {0,1}, {0,-1}}; // new changes in U, D, L, R
        char[] moves = {'U', 'D', 'L', 'R'};

        while (!stack.isEmpty()) { // loop until stack is empty
            State curr = stack.pop(); // pop the last element
            if (curr.depth > MAX_MOVES) throw new RuntimeException("Move sequence exceeds 1,000,000 moves"); // if move over 1 million, raise error
            if (visited.contains(curr.getKey())) continue; // if current state is already visited, skip to next state
            visited.add(curr.getKey()); // add current state to visited set 
            if (curr.isGoal()) return buildPath(curr); // if find goal state, return path and end

            // generate 4 new states in 4 directions; then push them into stack. The order is U, D, L, R 
            for (int d = 0; d < 4; d++) {
                int nr = curr.emptyRow + dir[d][0], nc = curr.emptyCol + dir[d][1]; // new coordinate of empty tile
                if (nr < 0 || nr >= SIZE || nc < 0 || nc >= SIZE) continue; // if new coordinate of empty tile is invalid (outside puzzle), ignore this state
                int[][] newState = copyState(curr.state);
                newState[curr.emptyRow][curr.emptyCol] = newState[nr][nc];
                newState[nr][nc] = 0;
                stack.push(new State(newState, curr, nr, nc, moves[d])); // push new state into stack to explore next time
            }
        }
        return "NO_SOLUTION";
    }

    public String solveAStar(int[][] puzzle) { // A* method
        if (!isSolvable(puzzle)) return "UNSOLVABLE"; // check if puzzle is solvable or not

        // find the coordinate of the empty tile
        int emptyR = -1, emptyC = -1;
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                if (puzzle[i][j] == 0) { emptyR = i; emptyC = j; }

        PriorityQueue heap = new PriorityQueue(1000000); // use Priority Queue to store unexplored states
        HashSet visited = new HashSet(1000000); // use Hash Table to store explored states
        heap.add(new State(puzzle, null, emptyR, emptyC, 'S')); // start the process 

        int[][] dir = {{1,0}, {-1,0}, {0,1}, {0,-1}}; // new changes in U, D, L, R
        char[] moves = {'U', 'D', 'L', 'R'};

        while (!heap.isEmpty()) {
            State curr = heap.poll();
            if (curr.g > MAX_MOVES) throw new RuntimeException("Move sequence exceeds 1,000,000 moves"); // if move over 1 million, raise error
            if (visited.contains(curr.getKey())) continue; // if current state is already visited, skip to next state
            visited.add(curr.getKey()); // add current state to visited set
            if (curr.isGoal()) return buildPath(curr); // if find goal state, return path and end

            // generate 4 new states in 4 directions; then push them into stack. The order is U, D, L, R 
            for (int d = 0; d < 4; d++) {
                int nr = curr.emptyRow + dir[d][0], nc = curr.emptyCol + dir[d][1];
                if (nr < 0 || nr >= SIZE || nc < 0 || nc >= SIZE) continue; // if new coordinate of empty tile is invalid (outside puzzle), ignore this state
                int[][] newState = copyState(curr.state);
                newState[curr.emptyRow][curr.emptyCol] = newState[nr][nc];
                newState[nr][nc] = 0;
                heap.add(new State(newState, curr, nr, nc, moves[d])); // add new state into heap to explore next time
            }
        }
        return "NO_SOLUTION";
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

        System.out.println("A* Solution: " + solver.solveAStar(puzzle_1));
        System.out.println("BFS Solution: " + solver.solveBFS(puzzle_1));
        System.out.println("DFS Solution: " + solver.solveDFS(puzzle_1));

        System.out.println("A* Solution: " + solver.solveAStar(puzzle));
        System.out.println("BFS Solution: " + solver.solveBFS(puzzle));
        System.out.println("DFS Solution: " + solver.solveDFS(puzzle));
        
        
        
    }
}