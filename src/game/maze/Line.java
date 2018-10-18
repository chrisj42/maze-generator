package game.maze;

import java.awt.Color;
import java.awt.Graphics;

import game.maze.MazeGrid.Cell;

class Line {
	public static final Color ACTIVE_COLOR = Color.RED;
	public static final Color FADE_COLOR = Color.LIGHT_GRAY;
	
	private final Cell cell1, cell2;
	private Color color;
	public Line(Cell cell1, Cell cell2) { this(cell1, cell2, ACTIVE_COLOR); }
	public Line(Cell cell1, Cell cell2, Color color) {
		this.cell1 = cell1;
		this.cell2 = cell2;
		this.color = color;
	}
	
	public void draw(Graphics g, int xo, int yo, int size) {
		g.setColor(color);
		cell1.drawConnection(g, xo, yo, size, cell2);
	}
	
	public Color getColor() { return color; }
	public void setColor(Color color) { this.color = color; }
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Line)) return false;
		Line l = (Line) o;
		return cell1.equals(l.cell1) && cell2.equals(l.cell2);//p1.equals(l.p1) && p2.equals(l.p2);
	}
	@Override
	public int hashCode() {
		return cell1.hashCode() + cell2.hashCode() * 17;//p1.hashCode() * 113 + p2.hashCode() * 73;
	}
}
