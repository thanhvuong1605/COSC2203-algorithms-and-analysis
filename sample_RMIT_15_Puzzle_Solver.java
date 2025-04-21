package assignment.group;

// import java.util.*;

// // Class definition as required [cite: 18]
// public class sample_RMIT_15_Puzzle_Solver {

//     // Target state (solved puzzle)
//     private static final int[][] GOAL_STATE = {
//             {1, 2, 3, 4},
//             {5, 6, 7, 8},
//             {9, 10, 11, 12},
//             {13, 14, 15, 0} // 0 represents the empty space
//     };
//     private static final int SIZE = 4; // Size of the puzzle grid (4x4)

//     // Represents a state (node) in the search space
//     private static class Node implements Comparable<Node> {
//         int[][] board;
//         int zeroRow, zeroCol; // Position of the empty tile (0)
//         int g; // Cost (number of moves) from the start state
//         int h; // Heuristic estimate (Manhattan distance) to the goal state
//         Node parent; // Parent node to reconstruct the path
//         char move; // Move that led to this state ('U', 'D', 'L', 'R', or 'S' for start)

//         Node(int[][] board, int g, Node parent, char move) {
//             this.board = board;
//             this.g = g;
//             this.parent = parent;
//             this.move = move;
//             findZero(); // Locate the empty tile
//             this.h = calculateManhattanDistance();
//         }

//         // Find the coordinates of the empty tile (0)
//         private void findZero() {
//             for (int r = 0; r < SIZE; r++) {
//                 for (int c = 0; c < SIZE; c++) {
//                     if (board[r][c] == 0) {
//                         zeroRow = r;
//                         zeroCol = c;
//                         return;
//                     }
//                 }
//             }
//         }

//         // Calculate Manhattan distance heuristic
//         private int calculateManhattanDistance() {
//             int distance = 0;
//             for (int r = 0; r < SIZE; r++) {
//                 for (int c = 0; c < SIZE; c++) {
//                     int value = board[r][c];
//                     if (value != 0) {
//                         int targetRow = (value - 1) / SIZE;
//                         int targetCol = (value - 1) % SIZE;
//                         distance += Math.abs(r - targetRow) + Math.abs(c - targetCol);
//                     }
//                     // Optionally add distance for the zero tile back to its goal position
//                     // else {
//                     //    int targetRow = SIZE - 1;
//                     //    int targetCol = SIZE - 1;
//                     //    distance += Math.abs(r - targetRow) + Math.abs(c - targetCol);
//                     // }
//                 }
//             }
//             return distance;
//         }

//         // Calculate f = g + h
//         int f() {
//             return g + h;
//         }

//         // Check if this state is the goal state
//         boolean isGoal() {
//             return Arrays.deepEquals(board, GOAL_STATE);
//         }

//         // Generate successor nodes (possible next states)
//         List<Node> generateSuccessors() {
//             List<Node> successors = new ArrayList<>();
//             // Possible moves (Up, Down, Left, Right) relative to the empty tile
//             int[] dr = {-1, 1, 0, 0}; // Change in row
//             int[] dc = {0, 0, -1, 1}; // Change in column
//             char[] moves = {'U', 'D', 'L', 'R'}; // Moves corresponding to dr, dc

//             for (int i = 0; i < 4; i++) {
//                 int newRow = zeroRow + dr[i];
//                 int newCol = zeroCol + dc[i];

//                 // Check if the new position is valid
//                 if (newRow >= 0 && newRow < SIZE && newCol >= 0 && newCol < SIZE) {
//                     int[][] newBoard = new int[SIZE][SIZE];
//                     for(int r=0; r<SIZE; r++) {
//                         newBoard[r] = Arrays.copyOf(board[r], SIZE);
//                     }

//                     // Swap the empty tile with the adjacent tile
//                     newBoard[zeroRow][zeroCol] = newBoard[newRow][newCol];
//                     newBoard[newRow][newCol] = 0;

//                     // Determine the move character based on the tile that moved *into* the empty space
//                     char actualMove;
//                     if (dr[i] == -1) actualMove = 'D'; // Tile moved Down into empty space
//                     else if (dr[i] == 1) actualMove = 'U'; // Tile moved Up into empty space
//                     else if (dc[i] == -1) actualMove = 'R'; // Tile moved Right into empty space
//                     else actualMove = 'L'; // Tile moved Left into empty space

//                     // Avoid immediately reversing the parent's move
//                     if (parent != null && areOppositeMoves(actualMove, parent.move)) {
//                         continue;
//                     }

//                     successors.add(new Node(newBoard, g + 1, this, actualMove));
//                 }
//             }
//             return successors;
//         }
        
//         // Helper to check if two moves are opposites
//         private boolean areOppositeMoves(char move1, char move2) {
//             return (move1 == 'U' && move2 == 'D') || (move1 == 'D' && move2 == 'U') ||
//                     (move1 == 'L' && move2 == 'R') || (move1 == 'R' && move2 == 'L');
//         }


//         // Override equals and hashCode for use in visited set
//         @Override
//         public boolean equals(Object o) {
//             if (this == o) return true;
//             if (o == null || getClass() != o.getClass()) return false;
//             Node node = (Node) o;
//             return Arrays.deepEquals(board, node.board);
//         }

//         @Override
//         public int hashCode() {
//             return Arrays.deepHashCode(board);
//         }

//         // Compare nodes based on f = g + h for the priority queue
//         @Override
//         public int compareTo(Node other) {
//             return Integer.compare(this.f(), other.f());
//         }
//     }

//     /**
//      * Solves the given 15-puzzle instance using A* search.
//      *
//      * @param puzzle A 4x4 2D integer array representing the puzzle state.
//      * 0 represents the empty cell. [cite: 19]
//      * @return A String representing the sequence of moves ('U', 'D', 'L', 'R')
//      * to solve the puzzle. [cite: 20]
//      * @throws IllegalArgumentException If the puzzle is unsolvable or no solution
//      * is found within limits.
//      * @throws RuntimeException If the solution path exceeds 1,000,000 moves. [cite: 21]
//      */
//     public String solve(int[][] puzzle) {
//         // Basic validation
//         if (puzzle == null || puzzle.length != SIZE || puzzle[0].length != SIZE) {
//             throw new IllegalArgumentException("Invalid puzzle dimensions. Must be 4x4.");
//         }

//         // Check solvability (optional but recommended)
//         // A 15-puzzle is solvable if the number of inversions is even when the blank
//         // is on an even row from the bottom (rows 1 or 3, counting from 0),
//         // or if the number of inversions is odd when the blank is on an odd row
//         // from the bottom (rows 0 or 2).
//         if (!isSolvable(puzzle)) {
//             throw new IllegalArgumentException("The provided puzzle is unsolvable.");
//         }


//         // A* Search Implementation
//         PriorityQueue<Node> openSet = new PriorityQueue<>();
//         Set<Node> closedSet = new HashSet<>(); // Using Node's equals/hashCode based on board state

//         Node startNode = new Node(puzzle, 0, null, 'S'); // 'S' for start
//         openSet.add(startNode);

//         while (!openSet.isEmpty()) {
//             Node currentNode = openSet.poll(); // Get node with lowest f = g + h

//             // Goal check
//             if (currentNode.isGoal()) {
//                 return reconstructPath(currentNode); // Found solution
//             }

//             closedSet.add(currentNode); // Mark current node as visited

//             // Explore neighbors (successors)
//             for (Node successor : currentNode.generateSuccessors()) {
//                 // If already visited and the new path isn't better, skip
//                 if (closedSet.contains(successor)) {
//                     continue;
//                 }

//                 // Check if the successor is in the open set already
//                 Node existingNode = findNodeInQueue(openSet, successor);

//                 if (existingNode == null) {
//                     // Not in open set, add it
//                     openSet.add(successor);
//                 } else if (successor.g < existingNode.g) {
//                     // Found a better path to this state, update the existing node
//                     openSet.remove(existingNode); // Remove the old one
//                     openSet.add(successor);     // Add the updated one (with better g and parent)
//                 }
//                  // If the existing node in the open set has a better or equal path, do nothing
//             }
//         }

//         // If the loop finishes without finding a solution
//         throw new IllegalArgumentException("No solution found for the given puzzle.");
//     }

//     // Helper to check solvability based on inversions and blank position
//     private boolean isSolvable(int[][] puzzle) {
//         int[] linearPuzzle = new int[SIZE * SIZE -1]; // Ignore the blank tile for inversions
//         int k = 0;
//         int blankRow = -1;

//         for (int i = 0; i < SIZE; i++) {
//             for (int j = 0; j < SIZE; j++) {
//                 if (puzzle[i][j] == 0) {
//                     blankRow = i; // Record the row of the blank tile
//                 } else {
//                      if(k < linearPuzzle.length) { // Boundary check
//                         linearPuzzle[k++] = puzzle[i][j];
//                     }
//                 }
//             }
//         }
        
//         if (blankRow == -1) { // Should not happen with valid input
//             return false; 
//         }

//         int inversions = 0;
//         for (int i = 0; i < linearPuzzle.length; i++) {
//             for (int j = i + 1; j < linearPuzzle.length; j++) {
//                 if (linearPuzzle[i] > linearPuzzle[j]) {
//                     inversions++;
//                 }
//             }
//         }

//         // Grid width is even (4). Solvability depends on inversions and blank row.
//         // Blank on an even row from the bottom (row 0 or 2) requires odd inversions.
//         // Blank on an odd row from the bottom (row 1 or 3) requires even inversions.
//         int blankRowFromBottom = SIZE - 1 - blankRow;
//         if (blankRowFromBottom % 2 == 0) { // Even row from bottom (1 or 3)
//             return inversions % 2 == 0;
//         } else { // Odd row from bottom (0 or 2)
//             return inversions % 2 != 0;
//         }
//     }


//     // Helper to find a node in the priority queue (needed for path updates)
//     // Note: This is O(N) and can be slow for very large queues.
//     // A more efficient approach uses a Map<State, Node> alongside the queue.
//     private Node findNodeInQueue(PriorityQueue<Node> queue, Node target) {
//         for (Node node : queue) {
//             if (node.equals(target)) { // Compares board state
//                 return node;
//             }
//         }
//         return null;
//     }

//     // Reconstructs the path from the goal node back to the start node
//     private String reconstructPath(Node goalNode) {
//         StringBuilder path = new StringBuilder();
//         Node current = goalNode;
//         while (current.parent != null) { // Stop before the start node ('S' move)
//             if (current.move != 'S') { // Don't append the start 'move'
//                 path.append(current.move);
//             }
//             current = current.parent;
//              // Check path length constraint
//             if (path.length() > 1000000) {
//                 throw new RuntimeException("Solution path exceeds 1,000,000 moves.");
//             }
//         }
//         return path.reverse().toString(); // Reverse to get path from start to goal
//     }

//     // Main method for testing (optional)
//     public static void main(String[] args) throws IllegalArgumentException {
//         sample_RMIT_15_Puzzle_Solver solver = new sample_RMIT_15_Puzzle_Solver();

//         // Example Puzzle from the document [cite: 21] (already almost solved)
//         int[][] examplePuzzle = {
//             {1, 2, 3, 4},
//             {5, 6, 7, 8},
//             {9, 10, 11, 12},
//             {13, 0, 14, 15}
//         };

//          // A slightly more complex solvable puzzle
//         int[][] samplePuzzle = {
//             {1, 2, 3, 4},
//             {5, 6, 0, 8}, // Blank moved
//             {9, 10, 7, 12},
//             {13, 14, 11, 15}
//         };
        
//          // A known difficult puzzle (takes many moves)
//         // int[][] hardPuzzle = {
//         //     {15, 14, 1, 6},
//         //     {9, 11, 4, 12},
//         //     {0, 10, 7, 3},
//         //     {13, 8, 5, 2}
//         // };

//         int[][] hardPuzzle = {
//             {5, 1, 2, 4},
//             {9, 6, 3, 8},
//             {13, 10, 12, 0},
//             {14, 11, 7, 15}
//         };


//         try {
//             // System.out.println("Solving example puzzle:");
//             // long start = System.currentTimeMillis();
//             // String solution = solver.solve(examplePuzzle);
//             // System.out.println("Time taken: " + ((float) (System.currentTimeMillis() - start) / 1000) + "s");
//             // System.out.println("Expected: LL"); // As per document [cite: 22]
//             // System.out.println("Found Solution: " + solution);
//             // System.out.println("Moves: " + solution.length());

//             // System.out.println("\nSolving sample puzzle:");
//             // start = System.currentTimeMillis();
//             // String solution2 = solver.solve(samplePuzzle);
//             // System.out.println("Time taken: " + ((float) (System.currentTimeMillis() - start) / 1000) + "s");
//             // System.out.println("Found Solution: " + solution2);
//             // System.out.println("Moves: " + solution2.length());
            
            // System.out.println("\nSolving hard puzzle (may take time):");
            // long start = System.currentTimeMillis();
            // String solution3 = solver.solve(hardPuzzle);            
            // System.out.println("Time taken: " + ((float) (System.currentTimeMillis() - start) / 1000) + "s");
            // System.out.println("Found Solution: " + solution3);
            // System.out.println("Moves: " + solution3.length());

//         } catch (RuntimeException e) {
//             System.err.println("Error: " + e.getMessage());
//         }
//     }
// }


import java.util.*;

// Class definition (can contain A*, BFS, DFS methods)
public class sample_RMIT_15_Puzzle_Solver {

    private static final int SIZE = 4;
    private static final int[][] GOAL_STATE = {
            {1, 2, 3, 4},
            {5, 6, 7, 8},
            {9, 10, 11, 12},
            {13, 14, 15, 0} // 0 represents the empty space
    };
    private static final int MAX_PATH_LENGTH = 1000000; 

    // --- Node Class for BFS/DFS ---
    // Simplified Node without f, g, h - only tracks state, parent, and move
    private static class SimpleNode {
        final int[][] board;
        final SimpleNode parent;
        final char move; // Move that led to this state ('U', 'D', 'L', 'R', or 'S' for start)
        final int zeroRow, zeroCol; // Position of the empty tile

        // Cached hash code for efficiency in HashSet
        private final int hashCode;

        SimpleNode(int[][] board, SimpleNode parent, char move) {
            this.board = board;
            this.parent = parent;
            this.move = move;

            // Find zero and calculate hashcode once
            int rZ = -1, cZ = -1;
            for (int r = 0; r < SIZE; r++) {
                for (int c = 0; c < SIZE; c++) {
                    if (board[r][c] == 0) {
                        rZ = r;
                        cZ = c;
                        break; // Found zero
                    }
                }
                if(rZ != -1) break;
            }
            this.zeroRow = rZ;
            this.zeroCol = cZ;
            this.hashCode = Arrays.deepHashCode(this.board);
        }

        boolean isGoal() {
            return Arrays.deepEquals(board, GOAL_STATE);
        }

        // Override equals and hashCode for use in visited set (HashSet)
        // Crucial for correctly identifying visited board states
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SimpleNode node = (SimpleNode) o;
            // Compare boards element by element for true equality
            return Arrays.deepEquals(board, node.board);
        }

        @Override
        public int hashCode() {
             // Use deep hash code of the board array
            return this.hashCode;
        }
    }


    // --- BFS Implementation ---

    /**
     * Solves the 15-puzzle using Breadth-First Search (BFS).
     * Guaranteed to find the shortest solution path.
     * Can be very memory-intensive.
     *
     * @param puzzle The initial puzzle state. [cite: 19]
     * @return The sequence of moves. [cite: 20]
     * @throws IllegalArgumentException If unsolvable.
     * @throws RuntimeException If path exceeds limit [cite: 21] or no solution found.
     */
    public String solveBFS(int[][] puzzle) {
        if (!isSolvable(puzzle)) {
            throw new IllegalArgumentException("The provided puzzle is unsolvable.");
        }

        Queue<SimpleNode> queue = new LinkedList<>(); // Use LinkedList as a Queue (FIFO)
        Set<SimpleNode> visited = new HashSet<>();   // Use SimpleNode's equals/hashCode

        SimpleNode startNode = new SimpleNode(puzzle, null, 'S');

        queue.offer(startNode); // offer is equivalent to enqueue
        visited.add(startNode);

        while (!queue.isEmpty()) {
            SimpleNode currentNode = queue.poll(); // poll is equivalent to dequeue

            if (currentNode.isGoal()) {
                return reconstructPath(currentNode); // Found shortest path
            }

            // Generate successors (valid next states)
            for (SimpleNode successor : generateSimpleSuccessors(currentNode)) {
                // Check if this board state has been visited before
                if (visited.add(successor)) { // .add returns true if the element was not already present
                    queue.offer(successor);
                }
            }
        }

        // Should not be reached if puzzle is solvable
        throw new RuntimeException("No solution found (BFS error - should be solvable).");
    }


    // --- DFS Implementation ---

    /**
     * Solves the 15-puzzle using Depth-First Search (DFS).
     * Not guaranteed to find the shortest path.
     * Uses less memory than BFS/A*.
     * Can be very slow if it explores long, non-optimal paths.
     *
     * @param puzzle The initial puzzle state. [cite: 19]
     * @return A sequence of moves (not necessarily shortest). [cite: 20]
     * @throws IllegalArgumentException If unsolvable.
     * @throws RuntimeException If path exceeds limit [cite: 21] or no solution found.
     */
    public String solveDFS(int[][] puzzle) {
         if (!isSolvable(puzzle)) {
            throw new IllegalArgumentException("The provided puzzle is unsolvable.");
        }

        Stack<SimpleNode> stack = new Stack<>();
        Set<SimpleNode> visited = new HashSet<>(); // Tracks states currently in stack path + fully explored

        SimpleNode startNode = new SimpleNode(puzzle, null, 'S');
        stack.push(startNode);

        while(!stack.isEmpty()) {
            SimpleNode currentNode = stack.pop();

             // Check if already fully explored (necessary for graph DFS with cycles)
             // Add to visited only AFTER checking goal and BEFORE processing successors
            if (!visited.add(currentNode)) {
                 continue; // Already explored this state path completely
            }
            
             // Goal check AFTER potentially popping from stack
            if (currentNode.isGoal()) {
                 return reconstructPath(currentNode); // Found a path
            }
            
             // Generate successors (consider reversing order for typical DFS behavior)
             List<SimpleNode> successors = generateSimpleSuccessors(currentNode);
             // Optional: Collections.reverse(successors); // To explore in a specific typical DFS order

            for (SimpleNode successor : successors) {
                 // Push onto stack only if not already explored/visited
                 if (!visited.contains(successor)) {
                    stack.push(successor);
                 }
            }
        }

        // Should not be reached if puzzle is solvable
        throw new RuntimeException("No solution found (DFS error - should be solvable).");
    }


    // --- Helper Methods (adapted/reused) ---

    // Generates successors using SimpleNode
    private List<SimpleNode> generateSimpleSuccessors(SimpleNode node) {
        List<SimpleNode> successors = new ArrayList<>();
        int[] dr = {-1, 1, 0, 0}; // Change in row
        int[] dc = {0, 0, -1, 1}; // Change in column

        for (int i = 0; i < 4; i++) {
            int tileToMoveRow = node.zeroRow + dr[i];
            int tileToMoveCol = node.zeroCol + dc[i];

            if (tileToMoveRow >= 0 && tileToMoveRow < SIZE && tileToMoveCol >= 0 && tileToMoveCol < SIZE) {
                int[][] newBoard = new int[SIZE][SIZE];
                for(int r=0; r<SIZE; r++) {
                    newBoard[r] = Arrays.copyOf(node.board[r], SIZE);
                }

                newBoard[node.zeroRow][node.zeroCol] = newBoard[tileToMoveRow][tileToMoveCol];
                newBoard[tileToMoveRow][tileToMoveCol] = 0;

                char actualMove;
                 if (dr[i] == -1) actualMove = 'D';
                 else if (dr[i] == 1) actualMove = 'U';
                 else if (dc[i] == -1) actualMove = 'R';
                 else actualMove = 'L';

                // Simple cycle avoidance (optional but good for DFS/BFS)
                if (node.parent != null && areOppositeMoves(actualMove, node.move)) {
                    continue;
                }

                successors.add(new SimpleNode(newBoard, node, actualMove));
            }
        }
        return successors;
    }
    
    // Helper to check if two moves are opposites (same as in A*)
    private boolean areOppositeMoves(char move1, char move2) {
        return (move1 == 'U' && move2 == 'D') || (move1 == 'D' && move2 == 'U') ||
               (move1 == 'L' && move2 == 'R') || (move1 == 'R' && move2 == 'L');
    }

    // Reconstructs the path from the goal node back to the start node using SimpleNode
    private String reconstructPath(SimpleNode goalNode) {
        StringBuilder path = new StringBuilder();
        SimpleNode current = goalNode;
        while (current.parent != null) { // Stop before the start node ('S' move)
            if (current.move != 'S') { // Don't append the start 'move'
                 path.append(current.move);
            }
            current = current.parent;
             // Check path length constraint [cite: 21]
            if (path.length() > MAX_PATH_LENGTH) {
                 throw new RuntimeException("Solution path exceeds " + MAX_PATH_LENGTH + " moves.");
            }
        }
        return path.reverse().toString(); // Reverse to get path from start to goal
    }

    // isSolvable method (same as provided for A*)
    private boolean isSolvable(int[][] puzzle) {
        int[] linearPuzzle = new int[SIZE * SIZE -1]; // Ignore the blank tile for inversions
        int k = 0;
        int blankRow = -1;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (puzzle[i][j] == 0) {
                    blankRow = i; // Record the row of the blank tile
                } else {
                     if(k < linearPuzzle.length) { // Boundary check
                        linearPuzzle[k++] = puzzle[i][j];
                    }
                }
            }
        }
        
        if (blankRow == -1) return false; // Should not happen with valid input

        int inversions = 0;
        for (int i = 0; i < linearPuzzle.length; i++) {
            for (int j = i + 1; j < linearPuzzle.length; j++) {
                if (linearPuzzle[i] > linearPuzzle[j]) {
                    inversions++;
                }
            }
        }

        int blankRowFromBottom = SIZE - 1 - blankRow;
        if (blankRowFromBottom % 2 == 0) { // Even row from bottom (1 or 3)
            return inversions % 2 == 0;
        } else { // Odd row from bottom (0 or 2)
            return inversions % 2 != 0;
        }
    }

    // (You would also include the A* solve method and its specific Node class if desired)
    // public String solve(...) { /* A* implementation */ }

     // Main method for testing (optional)
    public static void main(String[] args) {
        sample_RMIT_15_Puzzle_Solver solver = new sample_RMIT_15_Puzzle_Solver();

        // Example Puzzle from the document [cite: 22]
        int[][] examplePuzzle = {
            {1, 2, 3, 4},
            {5, 6, 7, 8},
            {9, 10, 11, 12},
            {13, 0, 14, 15}
        };
        // A slightly more complex solvable puzzle
         int[][] samplePuzzle = {
             {1, 2, 3, 4},
             {5, 6, 0, 8}, // Blank moved
             {9, 10, 7, 12},
             {13, 14, 11, 15}
         };

         int[][] hardPuzzle = {
            {5, 1, 2, 4},
            {9, 6, 3, 8},
            {13, 10, 12, 0},
            {14, 11, 7, 15}
        };

        try {
            // System.out.println("Solving example puzzle with BFS:");
            // long start = System.currentTimeMillis();
            // String solutionBFS = solver.solveBFS(examplePuzzle);
            // System.out.println("Expected: LL"); // [cite: 21, 22]
            // System.out.println("BFS Solution: " + solutionBFS);
            // System.out.println("Moves: " + solutionBFS.length());
            // System.out.println("Time taken: " + ((float) (System.currentTimeMillis() - start) / 1000) + "s");

            // System.out.println("\nSolving example puzzle with DFS:");
            // long start = System.currentTimeMillis();
            // String solutionDFS_ex = solver.solveDFS(examplePuzzle);
            // System.out.println("DFS Solution (example): " + solutionDFS_ex); // Might not be optimal
            // System.out.println("Moves: " + solutionDFS_ex.length());
            // System.out.println("Time taken: " + ((float) (System.currentTimeMillis() - start) / 1000) + "s");


            // System.out.println("\nSolving sample puzzle with BFS:");
            // long start = System.currentTimeMillis();
            // String solutionBFS2 = solver.solveBFS(samplePuzzle);
            // System.out.println("BFS Solution: " + solutionBFS2);
            // System.out.println("Moves: " + solutionBFS2.length());
            // System.out.println("Time taken: " + ((float) (System.currentTimeMillis() - start) / 1000) + "s");

            // System.out.println("\nSolving sample puzzle with DFS:");
            // long start = System.currentTimeMillis();
            // String solutionDFS2 = solver.solveDFS(samplePuzzle);
            // System.out.println("DFS Solution (sample): " + solutionDFS2); // Might not be optimal
            // System.out.println("Moves: " + solutionDFS2.length());
            // System.out.println("Time taken: " + ((float) (System.currentTimeMillis() - start) / 1000) + "s");

            // System.out.println("\nSolving hard puzzle with BFS:");
            // long start = System.currentTimeMillis();
            // String solution3 = solver.solveBFS(hardPuzzle);            
            // System.out.println("Time taken: " + ((float) (System.currentTimeMillis() - start) / 1000) + "s");
            // System.out.println("Found Solution: " + solution3);
            // System.out.println("Moves: " + solution3.length());

            System.out.println("\nSolving hard puzzle with DFS:");
            long start = System.currentTimeMillis();
            String solution4 = solver.solveDFS(hardPuzzle);            
            System.out.println("Time taken: " + ((float) (System.currentTimeMillis() - start) / 1000) + "s");
            System.out.println("Found Solution: " + solution4);
            System.out.println("Moves: " + solution4.length());

        } catch (RuntimeException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}