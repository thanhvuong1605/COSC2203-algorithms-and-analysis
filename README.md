# 15-Puzzle Solver

This is a Java implementation of a 15-Puzzle Solver that uses three different algorithms:
- A* Search
- Breadth-First Search (BFS)
- Depth-First Search (DFS)

## Project Structure

The project consists of the following files:
- `RMIT_15_Puzzle_Solver_Final.java` - Main solver class
- `State.java` - State representation class
- `Queue.java` - Queue implementation for BFS
- `Stack.java` - Stack implementation for DFS
- `PriorityQueue.java` - Priority Queue implementation for A*
- `HashSet.java` - HashSet implementation for visited states
- `IntList.java` - Integer list implementation

## Requirements

- Java Development Kit (JDK) 8 or higher

## How to Run

1. Make sure all the files are in the same directory
2. Compile all Java files:
   ```bash
   javac *.java
   ```
3. Run the solver:
   ```bash
   java RMIT_15_Puzzle_Solver_Final
   ```

## Output Format

The program will test three puzzles (Easy, Medium, and Hard) and for each puzzle, it will show:
- A* solution with execution time and states explored
- BFS solution with execution time and states explored
- DFS solution with execution time and states explored

Example output:
```
Testing Easy Puzzle:
A* Solution: ULLDR (0.02 ms, 15 states explored, moves: 5)
BFS Solution: ULLDR (0.03 ms, 20 states explored, moves: 5)
DFS Solution: ULLDR (0.01 ms, 10 states explored, moves: 5)
```

## Custom Puzzles

You can modify the puzzles in the `main` method of `RMIT_15_Puzzle_Solver_Final.java`. Each puzzle is represented as a 4x4 grid where:
- Numbers 1-15 represent the tiles
- 0 represents the empty space

Example puzzle format:
```java
int[][] puzzle = {
    {1, 2, 3, 4},
    {5, 6, 7, 8},
    {9, 10, 11, 12},
    {13, 0, 14, 15}
};
```