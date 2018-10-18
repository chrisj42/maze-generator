package game.maze;

import java.awt.Color;

import game.maze.MazeGrid.Cell;
import game.util.DLList;

// it remembers the previous node.
class PathNode<E extends Cell> implements Comparable<PathNode<E>> {
	private final PathNode<E> prev;
	private final E cur;
	private Line line;
	private Line[] lines;
	private int length = -1;
	
	public PathNode(PathNode<E> prev, E cur) {
		this.prev = prev;
		this.cur = cur;
		line = prev == null ? null : new Line(prev.getCur(), cur);
		
		if(prev != null)
			prev.getCur().setWall(false, getCur().getPos());
		
		PathNode<E> node = this;
		DLList<Cell> path = new DLList<>();
		DLList<Line> lines = new DLList<>();
		do {
			//System.out.println("adding "+pathNode.getCur()+" to final path");
			path.add(node.getCur(), 0);
			if(node.getLine() != null)
				lines.add(node.getLine());
			node = node.getPrev();
		} while(node != null);
		
		this.lines = lines.toArray(Line.class);
	}
	
	public Line getLine() { return line; }
	public Line[] getLines() { return lines; }
	public void setPathColor(Color color) {
		for(Line line: lines)
			line.setColor(color);
	}
	public void setPathColor(Color color, Color replaceThis) {
		for(Line line: lines) {
			if(line.getColor().equals(replaceThis))
				line.setColor(color);
		}
	}
	
	public E getCur() { return cur; }
	public PathNode<E> getPrev() { return prev; }
	
	public E getRoot() {
		PathNode<E> cur = this;
		while(cur.prev != null)
			cur = cur.prev;
		return cur.cur;
	}
	
	public int getLength() {
		if(length >= 0) return length;
		
		// calculate it
		int len = 0;
		PathNode<E> node = this;
		while(node != null) {
			len++;
			node = node.prev;
		}
		length = len;
		return length;
	}
	
	/*public Line[] getLinePath(PathNode<Cell> node) {
		
		// the path is found, now make the lines.
		// Line[] lines = new Line[path.size()];
		// Cell[] cellPath = path.toArray(Cell.class);
		// for(int i = 0; i < lines.length; i++)
		// 	lines[i] = //new Line(cellPath[i], i==0?maze.new Cell(-1, 0):cellPath[i-1], color);
		
		return lines.toArray(Line.class);
	}*/
	
	@Override
	public int compareTo(PathNode<E> o) { return getLength() - o.getLength(); }
}
