package assignment.group;

import java.util.*;

// Class definition (contain BFS, DFS methods)
public class sample_RMIT_15_Puzzle_Solver_BFS_DFS {

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
        sample_RMIT_15_Puzzle_Solver_BFS_DFS solver = new sample_RMIT_15_Puzzle_Solver_BFS_DFS();

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
            System.out.println("Solving example puzzle with BFS:");
            long start = System.currentTimeMillis();
            String solutionBFS = solver.solveBFS(examplePuzzle);
            System.out.println("Expected: LL"); // [cite: 21, 22]
            System.out.println("BFS Solution: " + solutionBFS);
            System.out.println("Moves: " + solutionBFS.length());
            System.out.println("Time taken: " + ((float) (System.currentTimeMillis() - start) / 1000) + "s");

            System.out.println("\nSolving example puzzle with DFS:");
            start = System.currentTimeMillis();
            String solutionDFS_ex = solver.solveDFS(examplePuzzle);
            System.out.println("DFS Solution (example): " + solutionDFS_ex); // Might not be optimal
            System.out.println("Moves: " + solutionDFS_ex.length());
            System.out.println("Time taken: " + ((float) (System.currentTimeMillis() - start) / 1000) + "s");


            System.out.println("\nSolving sample puzzle with BFS:");
            start = System.currentTimeMillis();
            String solutionBFS2 = solver.solveBFS(samplePuzzle);
            System.out.println("BFS Solution: " + solutionBFS2);
            System.out.println("Moves: " + solutionBFS2.length());
            System.out.println("Time taken: " + ((float) (System.currentTimeMillis() - start) / 1000) + "s");

            System.out.println("\nSolving sample puzzle with DFS:");
            start = System.currentTimeMillis();
            String solutionDFS2 = solver.solveDFS(samplePuzzle);
            System.out.println("DFS Solution (sample): " + solutionDFS2); // Might not be optimal
            System.out.println("Moves: " + solutionDFS2.length());
            System.out.println("Time taken: " + ((float) (System.currentTimeMillis() - start) / 1000) + "s");

            System.out.println("\nSolving hard puzzle with BFS:");
            start = System.currentTimeMillis();
            String solution3 = solver.solveBFS(hardPuzzle);            
            System.out.println("Time taken: " + ((float) (System.currentTimeMillis() - start) / 1000) + "s");
            System.out.println("Found Solution: " + solution3);
            System.out.println("Moves: " + solution3.length());

            System.out.println("\nSolving hard puzzle with DFS:");
            start = System.currentTimeMillis();
            String solution4 = solver.solveDFS(hardPuzzle);            
            System.out.println("Time taken: " + ((float) (System.currentTimeMillis() - start) / 1000) + "s");
            System.out.println("Found Solution: " + solution4);
            System.out.println("Moves: " + solution4.length());

        } catch (RuntimeException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}