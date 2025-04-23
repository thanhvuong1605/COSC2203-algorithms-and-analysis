interface List<T> {
    boolean insertAt(int index, T value);
    boolean insertBefore(T searchValue, T value);
    boolean insertAfter(T searchValue, T value);
    boolean removeAt(int index);
    boolean remove(T value);
    boolean contains(T value);
    int size();
    boolean hasNext();
    T next(); // Returns null if no next element
    void reset();
    T get(int index); // Returns null if index invalid
    void add(T value);
}

class MyArrayList<T> implements List<T> {
    private int size;
    private int pointer;
    private static final int INITIAL_CAPACITY = 4;
    private T[] items;

    public MyArrayList() {
        size = 0;
        pointer = 0;
        @SuppressWarnings("unchecked")
        T[] temp = (T[]) new Object[INITIAL_CAPACITY];
        items = temp;
    }

    private void ensureCapacity() {
        if (size == items.length) {
            int newCapacity = items.length * 2;
            @SuppressWarnings("unchecked")
            T[] newItems = (T[]) new Object[newCapacity];
            // Using System.arraycopy for efficiency. Replace with loop if forbidden.
            System.arraycopy(items, 0, newItems, 0, size);
            /* Manual Loop Alternative:
            for (int i = 0; i < size; i++) {
                newItems[i] = items[i];
            }
            */
            items = newItems;
        }
    }

    private void shiftRight(int index) {
        for (int i = size; i > index; i--) {
            items[i] = items[i - 1];
        }
    }

    private void shiftLeft(int index) {
        for (int i = index; i < size - 1; i++) {
            items[i] = items[i + 1];
        }
        items[size - 1] = null;
    }

    @Override
    public int size() { return size; }

    @Override
    public void reset() { pointer = 0; }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return items[index];
    }

    @Override
    public boolean hasNext() { return pointer < size; }

    @Override
    public T next() {
        if (!hasNext()) {
            return null; // Return null instead of throwing exception
        }
        return items[pointer++];
    }

    @Override
    public boolean contains(T value) {
        for (int i = 0; i < size; i++) {
            if (value == null) {
                if (items[i] == null) return true;
            } else {
                // Relies on the equals method of type T
                if (value.equals(items[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean insertAt(int index, T value) {
        if (index < 0 || index > size) { return false; }
        ensureCapacity();
        shiftRight(index);
        items[index] = value;
        size++;
        if(index <= pointer) { pointer++; }
        return true;
    }

    @Override
    public boolean insertBefore(T searchValue, T value) {
        for (int i = 0; i < size; i++) {
            if (searchValue == null ? items[i] == null : searchValue.equals(items[i])) {
                return insertAt(i, value);
            }
        }
        return false;
    }

    @Override
    public boolean insertAfter(T searchValue, T value) {
        for (int i = 0; i < size; i++) {
            if (searchValue == null ? items[i] == null : searchValue.equals(items[i])) {
                return insertAt(i + 1, value);
            }
        }
        return false;
    }

    @Override
    public void add(T value) { insertAt(0, value); } // Prepends

    @Override
    public boolean removeAt(int index) {
        if (index < 0 || index >= size) { return false; }
        shiftLeft(index);
        size--;
        if (index < pointer) { pointer--; }
        if (pointer > size) { pointer = size; }
        return true;
    }

    @Override
    public boolean remove(T value) {
        for (int i = 0; i < size; i++) {
             if (value == null ? items[i] == null : value.equals(items[i])) {
                return removeAt(i);
            }
        }
        return false;
    }

    // toString without Arrays.toString
    @Override
    public String toString() {
        if (size == 0) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for(int i = 0; i < size; i++) {
            sb.append(items[i] == null ? "null" : items[i].toString()); // Handle null elements
            if (i < size - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}

// Corrected FIFO Queue (No JCF Dependencies)
class MyQueue<T> {
    private static class QueueNode<T> {
        T data;
        QueueNode<T> next;
        QueueNode(T data) { this.data = data; this.next = null; }
    }

    private int size;
    private QueueNode<T> head;
    private QueueNode<T> tail;

    public MyQueue() { size = 0; head = null; tail = null; }
    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    public boolean enqueue(T item) {
        QueueNode<T> newNode = new QueueNode<>(item);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
        return true;
    }

    public T dequeue() {
        if (isEmpty()) {
            return null; // Return null instead of throwing exception
        }
        T data = head.data;
        head = head.next;
        size--;
        if (isEmpty()) { tail = null; }
        return data;
    }

    public T peek() {
        if (isEmpty()) {
            return null; // Return null instead of throwing exception
        }
        return head.data;
    }
}

// Linked List for HashSet Chaining (No JCF Dependencies)
class MySimpleLinkedList<T> {
    public static class ListNode<T> {
        T data;
        ListNode<T> next;
        ListNode(T data, ListNode<T> next) { this.data = data; this.next = next; }
    }

    public ListNode<T> head;
    private int size;

    public MySimpleLinkedList() { this.head = null; this.size = 0; }
    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    public void addFirst(T element) {
        head = new ListNode<>(element, head);
        size++;
    }

    public boolean contains(T element) {
        ListNode<T> current = head;
        while (current != null) {
            // Assumes element is not null, relies on T.equals()
            if (element.equals(current.data)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public boolean remove(T element) {
        if (head == null) { return false; }
        if (element.equals(head.data)) {
            head = head.next;
            size--;
            return true;
        }
        ListNode<T> current = head;
        while (current.next != null) {
            if (element.equals(current.next.data)) {
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }
}

// Node class (No JCF Dependencies)
class Node {
    private static final int SIZE = RMIT_15_Puzzle_Solver.SIZE;

    public final int[][] board;
    public final Node parent;
    public final char move;
    public final int zeroR;
    public final int zeroC;
    private final int calculatedHashCode;

    public Node(int[][] inputBoard, Node parent, char move) {
        this.board = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            // Using System.arraycopy for efficiency. Replace with loop if forbidden.
            System.arraycopy(inputBoard[i], 0, this.board[i], 0, SIZE);
            /* Manual Loop Alternative:
            for (int j = 0; j < SIZE; j++) {
                this.board[i][j] = inputBoard[i][j];
            }
            */
        }
        this.parent = parent;
        this.move = move;

        int zr = -1, zc = -1;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (this.board[i][j] == 0) {
                    zr = i; zc = j; break;
                }
            }
            if (zr != -1) break;
        }
        if (zr == -1) { throw new IllegalArgumentException("Input board must contain a 0 tile."); }
        this.zeroR = zr; this.zeroC = zc;
        this.calculatedHashCode = calculateHashCode();
    }

    private int calculateHashCode() {
        int result = 17;
        int multiplier = 31;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                result = multiplier * result + this.board[i][j];
            }
        }
        return result;
    }

    @Override
    public int hashCode() { return this.calculatedHashCode; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node otherNode = (Node) o;
        if (this.calculatedHashCode != otherNode.calculatedHashCode) return false;
        // Manual deep equals check instead of Arrays.deepEquals
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (this.board[i][j] != otherNode.board[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    // Helper for manual board comparison
    public boolean isEqual(int[][] otherBoard) {
         if (otherBoard == null || otherBoard.length != SIZE || otherBoard[0].length != SIZE) {
             return false;
        }
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (this.board[i][j] != otherBoard[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isGoal() {
        // Use manual isEqual instead of Arrays.deepEquals
        return this.isEqual(RMIT_15_Puzzle_Solver.GOAL_NODE);
    }

    // toString without Arrays.toString
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Node{move=").append(move).append(", board=\n");
         for (int i = 0; i < SIZE; i++) {
             sb.append(" ["); // Start row
             for (int j = 0; j < SIZE; j++) {
                 sb.append(this.board[i][j]);
                 if (j < SIZE - 1) sb.append(", ");
             }
             sb.append("]\n"); // End row
         }
         sb.append("}");
         return sb.toString();
    }
}

// HashSet using custom list (No JCF Dependencies)
class MyHashSet {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private MySimpleLinkedList<Node>[] buckets;
    private int capacity;
    private int size;
    private float loadFactor;

    public MyHashSet() { this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR); }
    public MyHashSet(int initialCapacity) { this(initialCapacity, DEFAULT_LOAD_FACTOR); }

    @SuppressWarnings("unchecked")
    public MyHashSet(int initialCapacity, float loadFactor) {
        if (initialCapacity <= 0) initialCapacity = DEFAULT_INITIAL_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) loadFactor = DEFAULT_LOAD_FACTOR;
        int cap = 1;
        while (cap < initialCapacity) cap <<= 1;
        this.capacity = cap;
        this.loadFactor = loadFactor;
        this.buckets = (MySimpleLinkedList<Node>[]) new MySimpleLinkedList[this.capacity];
        this.size = 0;
    }

    private int getBucketIndex(Node node) {
        int h = node.hashCode();
        return (h & 0x7fffffff) % capacity; // Modulo for safety
    }

    public boolean add(Node node) {
        if (node == null) throw new NullPointerException("Cannot add null node to HashSet");
        if ((float) (size + 1) / capacity > loadFactor) { resize(capacity * 2); }
        int index = getBucketIndex(node);
        MySimpleLinkedList<Node> bucket = buckets[index];
        if (bucket == null) {
            bucket = new MySimpleLinkedList<>();
            buckets[index] = bucket;
        }
        if (!bucket.contains(node)) { // Relies on Node.equals()
            bucket.addFirst(node);
            size++;
            return true;
        }
        return false;
    }

    public boolean contains(Node node) {
        if (node == null) return false;
        int index = getBucketIndex(node);
        MySimpleLinkedList<Node> bucket = buckets[index];
        return bucket != null && bucket.contains(node); // Relies on Node.equals()
    }

    public boolean remove(Node node) {
        if (node == null) return false;
        int index = getBucketIndex(node);
        MySimpleLinkedList<Node> bucket = buckets[index];
        if (bucket == null) { return false; }
        boolean removed = bucket.remove(node); // Relies on Node.equals()
        if (removed) { size--; }
        return removed;
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        MySimpleLinkedList<Node>[] oldBuckets = buckets;
        this.capacity = newCapacity;
        this.buckets = (MySimpleLinkedList<Node>[]) new MySimpleLinkedList[this.capacity];
        this.size = 0;
        for (MySimpleLinkedList<Node> oldBucket : oldBuckets) {
            if (oldBucket != null) {
                MySimpleLinkedList.ListNode<Node> current = oldBucket.head;
                while (current != null) {
                    add(current.data); // Rehash into new table
                    current = current.next;
                }
            }
        }
    }
}

// --- Custom Stack Implementation (No JCF Dependencies) ---
class MyStack<T> {
    private static class StackNode<T> {
        T data;
        StackNode<T> next; // Link to the node below it in the stack

        StackNode(T data, StackNode<T> next) {
            this.data = data;
            this.next = next;
        }
    }

    private StackNode<T> top; // The head of the linked list is the top of the stack
    private int size;

    public MyStack() {
        top = null;
        size = 0;
    }

    /** Returns the number of items currently in the stack. */
    public int size() {
        return size;
    }

    /** Returns true if the stack is empty, false otherwise. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Adds an item to the top of the stack. */
    public void push(T item) {
        // Create a new node pointing to the current top
        StackNode<T> newNode = new StackNode<>(item, top);
        // The new node becomes the new top
        top = newNode;
        size++;
    }

    /** Removes and returns the item from the top of the stack. Returns null if the stack is empty. */
    public T pop() {
        if (isEmpty()) {
            return null; // Return null instead of throwing exception
        }
        T data = top.data; // Get data from the top node
        top = top.next;    // Update top to the next node down
        size--;
        return data;
    }

    /** Returns the item currently at the top of the stack without removing it. Returns null if the stack is empty. */
    public T peek() {
        if (isEmpty()) {
            return null; // Return null instead of throwing exception
        }
        return top.data;
    }
}

// --- Main Solver Class (No JCF Dependencies) ---
public class RMIT_15_Puzzle_Solver {
    public static final int SIZE = 4;
    public static final int[][] GOAL_NODE = {
            { 1,  2,  3,  4 }, 
            { 5,  6,  7,  8 }, 
            { 9, 10, 11, 12 }, 
            {13, 14, 15,  0 }
    };
    public static final int MAX_PATH_LENGTH = 1000000;

    public String solve(int[][] initialBoard) {
        if (!isSolvable(initialBoard)) {
             return "UNSOLVABLE";
        }

        MyQueue<Node> queue = new MyQueue<>();
        MyHashSet visited = new MyHashSet(100000);
        Node startNode = new Node(initialBoard, null, 'S');

        queue.enqueue(startNode);
        visited.add(startNode);
        int nodesExplored = 0;

        while (!queue.isEmpty()) {
            Node currentNode = queue.dequeue(); // Returns null if empty (already checked by isEmpty)
            // Safety check although isEmpty should prevent this
            if (currentNode == null) continue;

            nodesExplored++;

            if (currentNode.isGoal()) { // isGoal now uses manual comparison
                System.out.println("Solution Found! Nodes explored: " + nodesExplored);
                return reconstructPath(currentNode);
            }

            List<Node> children = generateSuccessors(currentNode); // Uses MyArrayList
            children.reset();
            while (children.hasNext()) {
                 Node child = children.next(); // Returns null if no more
                 if (child != null) { // Check if next() returned a valid node
                    if (visited.add(child)) { // add relies on Node.equals/hashCode
                        queue.enqueue(child);
                    }
                 }
            }
        }
        throw new RuntimeException("BFS failed to find a solution for a supposedly solvable puzzle.");
    }

    // --- Inside RMIT_15_Puzzle_Solver Class ---

    /**
     * Solves the 15-puzzle using Depth-First Search (DFS).
     * Note: DFS does not guarantee the shortest path.
     * Uses a stack for the frontier and a set to track visited states to prevent cycles.
     *
     * @param initialBoard The starting configuration of the puzzle.
     * @return A string representing the sequence of moves (tile-based), or "UNSOLVABLE", or "NO_SOLUTION_FOUND" if search fails.
     */
    public String solveDFS(int[][] initialBoard) {
        if (!isSolvable(initialBoard)) {
            return "UNSOLVABLE";
        }

        MyStack<Node> stack = new MyStack<>();          // Use MyStack for DFS frontier
        MyHashSet visited = new MyHashSet(100000);      // Track visited states to avoid cycles
        Node startNode = new Node(initialBoard, null, 'S'); // Start node

        stack.push(startNode);
        // We add to visited when pushing to prevent exploring cycles immediately.
        // Alternative: Add to visited when popping, might explore more but uses more memory/time if cycles are revisited before being marked.
        visited.add(startNode);

        int nodesExplored = 0; // Counter for stats

        while (!stack.isEmpty()) {
            Node currentNode = stack.pop(); // Get node from the TOP of the stack (LIFO)

            // Safety check for null return from pop (shouldn't happen if isEmpty is checked)
            if (currentNode == null) continue;

            nodesExplored++;
            // Optional: Add a check against MAX_PATH_LENGTH here based on node depth,
            // but calculating depth requires extra effort. The path reconstruction
            // check is a safeguard against overly long *solutions*.

            // Goal check
            if (currentNode.isGoal()) {
                System.out.println("DFS Solution Found! Nodes explored: " + nodesExplored);
                // Path reconstruction is the same as BFS
                return reconstructPath(currentNode);
            }

            // Generate successors (neighbors)
            List<Node> children = generateSuccessors(currentNode); // Uses MyArrayList

            // Add valid, unvisited children to the stack
            children.reset(); // Reset iterator for MyArrayList
            while (children.hasNext()) {
                Node child = children.next();
                if (child != null) {
                    // If the child state hasn't been visited, add it to visited and push onto stack
                    if (visited.add(child)) { // visited.add() uses Node's equals/hashCode
                        stack.push(child);
                    }
                    // If already visited, do nothing (don't push onto stack)
                }
            }
        }

        // If the stack becomes empty and no solution was found (shouldn't happen for solvable puzzles)
        System.out.println("DFS failed to find a solution! Nodes explored: " + nodesExplored);
        return "NO_SOLUTION_FOUND"; // Or throw exception
    }

    // isSolvable uses only primitive arrays and loops (No JCF)
    private boolean isSolvable(int[][] puzzle) {
         if (puzzle == null || puzzle.length != SIZE || puzzle[0].length != SIZE) { return false; }
        int[] linearPuzzle = new int[SIZE * SIZE];
        int k = 0;
        int blankRow = -1;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                 if (k < linearPuzzle.length) { linearPuzzle[k++] = puzzle[i][j]; }
                 else { System.err.println("Error: Exceeded linear puzzle array bounds."); return false; }
                 if (puzzle[i][j] == 0) { blankRow = i; }
            }
        }
        if (blankRow == -1) { System.err.println("Error: No blank tile (0) found."); return false; }
        int inversions = 0;
        for (int i = 0; i < linearPuzzle.length; i++) {
            if (linearPuzzle[i] == 0) continue;
            for (int j = i + 1; j < linearPuzzle.length; j++) {
                if (linearPuzzle[j] == 0) continue;
                if (linearPuzzle[i] > linearPuzzle[j]) { inversions++; }
            }
        }
        int blankRowFromBottom_1Based = SIZE - blankRow;
        boolean isBlankRowEvenFromBottom = (blankRowFromBottom_1Based % 2 == 0);
        boolean isInversionsOdd = (inversions % 2 != 0);
        if (isBlankRowEvenFromBottom) { return isInversionsOdd; }
        else { return !isInversionsOdd; }
    }

    // reconstructPath uses StringBuilder (standard Java, not Collections Framework)
    private String reconstructPath(Node goalNode) {
        StringBuilder path = new StringBuilder();
        Node current = goalNode;
        while (current != null && current.parent != null) {
             if (current.move != 'S') { path.append(current.move); }
             current = current.parent;
             if (path.length() > MAX_PATH_LENGTH) {
                 throw new RuntimeException("Solution path exceeds " + MAX_PATH_LENGTH + " moves.");
             }
        }
        return path.reverse().toString();
    }

    // generateSuccessors uses MyArrayList (No JCF)
    // Uses the "LL" logic for examplePuzzle as requested
    private List<Node> generateSuccessors(Node node) {
        List<Node> successors = new MyArrayList<>();
        int[] dr = { -1,  1,  0,  0 }; int[] dc = {  0,  0, -1,  1 };
        for (int i = 0; i < 4; i++) {
            int newZeroR = node.zeroR + dr[i]; int newZeroC = node.zeroC + dc[i];
            if (newZeroR >= 0 && newZeroR < SIZE && newZeroC >= 0 && newZeroC < SIZE) {
                int[][] childBoard = new int[SIZE][SIZE];
                for (int r = 0; r < SIZE; r++) {
                     // Using System.arraycopy for efficiency. Replace with loop if forbidden.
                     System.arraycopy(node.board[r], 0, childBoard[r], 0, SIZE);
                     /* Manual Loop Alternative:
                     for (int c = 0; c < SIZE; c++) {
                         childBoard[r][c] = node.board[r][c];
                     }
                     */
                }
                childBoard[node.zeroR][node.zeroC] = childBoard[newZeroR][newZeroC];
                childBoard[newZeroR][newZeroC] = 0;
                char actualMove; // Assign based on numbered tile movement
                if (dr[i] == -1) actualMove = 'D'; else if (dr[i] == 1) actualMove = 'U';
                else if (dc[i] == -1) actualMove = 'R'; else actualMove = 'L';
                successors.add(new Node(childBoard, node, actualMove));
            }
        }
        return successors;
    }

    // --- Main Method (No JCF Dependencies other than System.out) ---
    public static void main(String[] args) {
        RMIT_15_Puzzle_Solver solver = new RMIT_15_Puzzle_Solver();
        int[][] examplePuzzle = { /* ... */ }; // Define puzzles as before
        int[][] samplePuzzle = { /* ... */ };
        int[][] hardPuzzle = { /* ... */ };
        int[][] unsolvablePuzzle = { /* ... */ };
         examplePuzzle = new int[][] {
            {1, 6, 2, 0},
            {9, 5, 4, 3},
            {13, 11, 7, 8},
            {14, 10, 15, 12}
        };
         samplePuzzle = new int[][] {
                { 1,  2,  3,  4 }, { 5,  6,  0,  8 }, { 9, 10,  7, 12 }, {13, 14, 11, 15 }
        };
         hardPuzzle = new int[][] {
                { 5,  1,  2,  4 }, { 9,  6,  3,  8 }, {13, 10, 12,  0 }, {14, 11,  7, 15 }
        };
         unsolvablePuzzle = new int[][] {
                 { 1,  2,  3,  4 }, { 5,  6,  7,  8 }, { 9, 10, 11, 12 }, {13, 15, 14,  0 }
        };

        System.out.println("--- Example Puzzle ---");
        runSolver("Example", solver, examplePuzzle);
        System.out.println("\n--- Sample Puzzle ---");
        runSolver("Sample", solver, samplePuzzle);
        System.out.println("\n--- Hard Puzzle ---");
        runSolver("Hard", solver, hardPuzzle);
        System.out.println("\n--- Unsolvable Puzzle ---");
        runSolver("Unsolvable", solver, unsolvablePuzzle);

        System.out.println("========== DFS ==========");
        // Pass the specific DFS method using a method reference (solver::solveDFS)
        runSolverDFS("DFS Example", solver, examplePuzzle);
        runSolverDFS("DFS Sample", solver, samplePuzzle);
        //runSolver("DFS Already Solved", solver::solveDFS, alreadySolved);
        runSolverDFS("DFS Hard", solver, hardPuzzle); // Note: DFS may be slow or find a long path
        runSolverDFS("DFS Unsolvable", solver, unsolvablePuzzle);
        System.out.println("=======================");
    }

    private static void runSolver(String name, RMIT_15_Puzzle_Solver solver, int[][] puzzle) {
         try {
            System.out.println("Solving " + name + " puzzle with custom BFS (No JCF):");
            long start = System.currentTimeMillis();
            String solution = solver.solve(puzzle);
            long duration = System.currentTimeMillis() - start; 
            if (solution.equals("UNSOLVABLE")) {
                 System.out.println("Result: Puzzle is UNSOLVABLE.");
            } else {
                 System.out.println("Solution: " + solution);
                 System.out.println("Moves: " + solution.length());
            }
            // Use manual formatting if String.format is disallowed (though unlikely)
            long seconds = duration / 1000;
            long millis = duration % 1000;
            System.out.println("Time taken: " + seconds + "." + (millis < 100 ? (millis < 10 ? "00" : "0") : "") + millis + "s");
            //System.out.printf("Time taken: %.3fs%n", (float) duration / 1000);

        } catch (Exception e) {
            System.err.println("An error occurred while solving " + name + ":");
            // e.printStackTrace(); // printStackTrace is standard Java I/O
            System.err.println(e.toString()); // Simpler error output
        }
    }

    private static void runSolverDFS(String name, RMIT_15_Puzzle_Solver solver, int[][] puzzle) {
        try {
           System.out.println("Solving " + name + " puzzle with custom BFS (No JCF):");
           long start = System.currentTimeMillis();
           String solution = solver.solveDFS(puzzle);
           long duration = System.currentTimeMillis() - start; 
           if (solution.equals("UNSOLVABLE")) {
                System.out.println("Result: Puzzle is UNSOLVABLE.");
           } else {
                System.out.println("Solution: " + solution);
                System.out.println("Moves: " + solution.length());
           }
           // Use manual formatting if String.format is disallowed (though unlikely)
           long seconds = duration / 1000;
           long millis = duration % 1000;
           System.out.println("Time taken: " + seconds + "." + (millis < 100 ? (millis < 10 ? "00" : "0") : "") + millis + "s");
           //System.out.printf("Time taken: %.3fs%n", (float) duration / 1000);

       } catch (Exception e) {
           System.err.println("An error occurred while solving " + name + ":");
           // e.printStackTrace(); // printStackTrace is standard Java I/O
           System.err.println(e.toString()); // Simpler error output
       }
   }

// --- isSolvable needed by runSolver ---
// Make sure the actual isSolvable method exists in the class
// This is just a placeholder if runSolver was defined outside
// static boolean isSolvable(int[][] p) { return true; }
}