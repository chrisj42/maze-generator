package game.maze;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;

import game.util.DLList;
import game.util.EndHeap;
import game.util.Point;

public class MazeGrid {
	
	/*
		vertical walls, x/y match cell to the right (array is one greater than cell array)
		horizontal walls, x/y match cell to the right (array is one greater than cell array)
	 */
	
	private final Cell[][] cells;
	private final boolean[][] horiWalls;
	private final boolean[][] vertWalls;
	private final int width, height;
	
	MazeGrid(int width, int height) {
		this.width = width;
		this.height = height;
		this.horiWalls = new boolean[width][height+1];
		this.vertWalls = new boolean[width+1][height];
		
		cells = new Cell[width][height];
		for(int x = 0; x < cells.length; x++) {
			for(int y = 0; y < cells[x].length; y++) {
				cells[x][y] = new Cell(x, y);
			}
		}
		
		for(boolean[] horiWallRow : horiWalls) Arrays.fill(horiWallRow, true);
		for(boolean[] vertWallRow : vertWalls) Arrays.fill(vertWallRow, true);
		
	}
	
	Cell getCell(Point p) { return cells[p.x][p.y]; }
	Cell getCell(int x, int y) { return cells[x][y]; }
	Cell[][] getCells() { return cells; }
	
	private static final int PAD = 5;
	public void drawMaze(Graphics g, int xo, int yo, int cellSize) {
		g.setColor(Color.WHITE);
		g.fillRect(xo-PAD, yo-PAD, width*cellSize + PAD*2, height*cellSize + PAD*2);
		g.setColor(Color.BLACK);
		// vertical lines
		for(int x = 0; x < vertWalls.length; x++)
			for(int y = 0; y < vertWalls[x].length; y++)
				if(vertWalls[x][y])
					g.drawLine(xo + x*cellSize, yo + y*cellSize, xo + x*cellSize, yo + (y+1)*cellSize);
		// horizontal lines
		for(int x = 0; x < horiWalls.length; x++)
			for(int y = 0; y < horiWalls[x].length; y++)
				if(horiWalls[x][y])
					g.drawLine(xo + x*cellSize, yo + y*cellSize, xo + (x+1)*cellSize, yo + y*cellSize);
	}
	
	public Cell getMatchingAdjacentCell(Cell cell, MazeCondition condition) {
		Cell[] matches = getMatchingAdjacentCells(cell, condition);
		if(matches.length > 0)
			return matches[(int)(Math.random()*matches.length)];
		else
			return null;
	}
	public Cell[] getMatchingAdjacentCells(Cell cell, MazeCondition condition) {
		Cell[] around = {
			cell.getRelative(-1, 0),
			cell.getRelative(1, 0),
			cell.getRelative(0, -1),
			cell.getRelative(0, 1)
		};
		DLList<Cell> matches = new DLList<>();
		for(Cell c: around)
			if(c != null && condition.cellMatches(c))
				matches.add(c);
		
		//if(matches.size() == 0) return null;
		return matches.toArray(Cell.class);
	}
	
	public void generate(MazePanel panel) {
		// Generate the maze
		System.out.println("generating maze grid...");
		
		DLList<PathNode<Cell>> backlog = new DLList<>();
		boolean[][] visited = new boolean[width][height]; // track which cells have already been visited
		
		Cell c = getCell(0, 0);
		PathNode<Cell> path = new PathNode<>(new PathNode<>(null, new Cell(-1, 0)), c); // start in upper left cell
		//c.setWall(false, -1, 0); // remove left wall as starting point
		c.set(true, visited); // starting here, already "visited"
		int unvisited = width*height-1;
		while(unvisited > 0) {
			Cell[] nextCells = getMatchingAdjacentCells(path.getCur(), other -> !other.get(visited));
			if(nextCells.length > 0) {
				// if there are unvisited cells adjacent to current one...
				// one has been selected
				
				if(nextCells.length > 1)
					backlog.add(path); // cache current cell
				
				Cell next = nextCells[(int)(Math.random()*nextCells.length)];
				path = new PathNode<>(path, next); // set current cell to new cell
				panel.append(path.getLine());
				path.getCur().set(true, visited); // mark new cell as visited
				unvisited--; // decrement count of unvisited cells
				panel.pause();
			}
			else if(backlog.size() > 0) {
				// else if stack isn't empty...
				// pop a cell and make it the current one
				path.setPathColor(Color.BLUE);
				panel.pause(true, MazePanel.WAIT*2);
				path = backlog.remove(0);
				panel.isolate(Line.FADE_COLOR, path.getLines());
				path.setPathColor(Line.ACTIVE_COLOR);
				panel.pause();
			}
		}
		
		panel.setMainLines((Line[])null);
		panel.setSubLines((Line[])null);
		System.out.println("maze grid generated.");
	}
	
	public class MazeTraveler {
		
		private MazePanel panel;
		
		public MazeTraveler(MazePanel panel) {
			this.panel = panel;
		}
		
		@SuppressWarnings("unchecked")
		public void findLongestPath() {
			System.out.println("finding longest path...");
			panel.setMainLines((Line[])null);
			panel.setSubLines((Line[])null);
			// while the maze was generated using a depth-first technique, the path-finder algorithm will use a breadth-first algorithm.
			
			Cell start = getCell(0, 0);
			boolean[][] visited = new boolean[width][height];
			start.set(true, visited);
			
			PathNode<Cell> startNode = new PathNode<>(new PathNode<>(null, new Cell(-1, 0)), start);
			
			PathNode<Cell>[] paths = (PathNode<Cell>[]) new PathNode[] {startNode};
			EndHeap<PathNode<Cell>> edges = new EndHeap<>();
			do {
				paths = getBranches(paths, visited, edges);
			} while(paths.length > 0);
			
			System.out.println("finished traversing maze");
			// prevPaths now holds an array of the last, longest paths that were still going before all the grid squares filled up.
			// pick one and render it.
			
			PathNode<Cell> path = edges.poll();
			Cell outside = new Cell(path.getCur().getRandomEdge());
			path = new PathNode<>(path, outside);
			panel.isolate(Line.FADE_COLOR, path.getLines());
			path.setPathColor(Color.GREEN);
			// the last cell should be an edge, so get a random out-of-bounds edge, remove the wall, and make a line there.
			System.out.println("lines created.");
			panel.pause();
		}
		
		// this is passed an array of paths. It goes through them, and for every original path, it creates new paths for all the branches, and returns them.
		@SuppressWarnings("unchecked")
		private PathNode<Cell>[] getBranches(PathNode<Cell>[] paths, boolean[][] visited, EndHeap<PathNode<Cell>> edges) {
			DLList<PathNode<Cell>> branches = new DLList<>();
			boolean[] fresh = new boolean[paths.length];
			boolean recolor = false;
			for(int i = 0; i < paths.length; i++) {
				PathNode<Cell> pathNode = paths[i];
				
				Cell[] nextCells = getMatchingAdjacentCells(pathNode.getCur(), adjacentCell -> {
					if(adjacentCell.get(visited)) return false;
					// make sure there is no wall between the previous cell and this one
					return !adjacentCell.hasWall(pathNode.getCur().getPos());
				});
				for(Cell nextCell: nextCells) {
					nextCell.set(true, visited);
					PathNode<Cell> node = new PathNode<>(pathNode, nextCell);
					branches.add(node);
					panel.append(node.getLine());
				}
				
				if(nextCells.length == 0) {
					recolor = true;
					pathNode.setPathColor(Line.FADE_COLOR);
					PathNode<Cell> prevLongest = edges.peek();
					if(pathNode.getCur().isEdge()) {
						edges.add(pathNode);
						if(prevLongest != edges.peek()) {
							if(prevLongest != null)
								prevLongest.setPathColor(Line.FADE_COLOR, Color.BLUE);
						}
					}
				} else
					fresh[i] = true;
			}
			
			PathNode<Cell>[] branchPaths = branches.toArray((Class<PathNode<Cell>>) paths.getClass().getComponentType());
			// this makes sure that when a path is deactivated, the other paths restore their sections' color, since the deactivated part(s) will overlap.
			if(recolor) {
				for(int i = 0; i < paths.length; i++)
					if(fresh[i])
						paths[i].setPathColor(Line.ACTIVE_COLOR);
				if(edges.peek() != null)
					edges.peek().setPathColor(Color.BLUE);
			}
			
			panel.pause(MazePanel.WAIT*5);
			return branchPaths;
		}
	}
	
	public class Cell {
		
		private final int x, y;
		
		Cell(Point p) { this(p.x, p.y); }
		Cell(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public Point getPos() { return new Point(x, y); }
		public Point getPos(int xo, int yo, int size) { return new Point(x*size+xo, y*size+yo); }
		public Point getCenter(int xo, int yo, int size) {
			Point pos = getPos(xo, yo, size);
			return new Point(pos.x + size/2, pos.y + size/2);
		}
		
		public Cell getRelative(int xo, int yo) {
			int nx = x + xo, ny = y + yo;
			if(nx < 0 || ny < 0 || nx >= width || ny >= height)
				return null;
			return cells[nx][ny];
		}
		
		public void drawConnection(Graphics g, int xo, int yo, int size, Cell other) {
			Point p1 = getCenter(xo, yo, size);
			Point p2 = other.getCenter(xo, yo, size);
			g.drawLine(p1.x, p1.y, p2.x, p2.y);
		}
		
		// returns if there is a wall in the direction given.
		public boolean hasWall(Point otherPos) { return hasWall(otherPos.x - x, otherPos.y - y); }
		public boolean hasWall(int xd, int yd) {
			// the horizontal walls VERTICALLY separate cells, and the vertical walls HORIZONTALLY separate cells. So, delta x's should refer to the vertical walls, and delta y's should refer to the horizontal walls.
			if(xd < 0 && vertWalls[x][y]) return true;
			if(xd > 0 && vertWalls[x+1][y]) return true;
			if(yd < 0 && horiWalls[x][y]) return true;
			if(yd > 0 && horiWalls[x][y+1]) return true;
			return false;
		}
		
		void setWall(boolean val, Point otherPos) { setWall(val, otherPos.x - x, otherPos.y - y); }
		void setWall(boolean val, int xd, int yd) {
			// the horizontal walls VERTICALLY separate cells, and the vertical walls HORIZONTALLY separate cells. So, delta x's should alter vertical walls, and delta y's should alter horizontal walls.
			if(xd < 0) vertWalls[x][y] = val;
			if(xd > 0) vertWalls[x+1][y] = val;
			if(yd < 0) horiWalls[x][y] = val;
			if(yd > 0) horiWalls[x][y+1] = val;
		}
		
		public boolean get(boolean[][] data) { return data[x][y]; }
		public void set(boolean val, boolean[][] data) { data[x][y] = val; }
		
		public boolean isEdge() { return x == 0 || y == 0 || x == width-1 || y == height-1; }
		
		public Point getRandomEdge() {
			if(!isEdge()) return null;
			DLList<Point> pts = new DLList<>();
			if(x == 0) pts.add(new Point(-1, y));
			if(y == 0) pts.add(new Point(x, -1));
			if(x == width-1) pts.add(new Point(width, y));
			if(y == height-1) pts.add(new Point(x, height));
			return pts.get((int)(Math.random()*pts.size()));
		}
		
		@Override
		public boolean equals(Object o) {
			if(!(o instanceof Cell)) return false;
			Cell c = (Cell) o;
			return getPos().equals(c.getPos());
		}
		@Override
		public int hashCode() { return getPos().hashCode(); }
	}
}
