package game.maze;

import game.maze.MazeGrid.Cell;

@FunctionalInterface
interface MazeCondition {
	boolean cellMatches(Cell cell);
}
