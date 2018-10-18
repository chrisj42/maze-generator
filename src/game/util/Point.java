package game.util;

import java.io.Serializable;

public class Point implements Serializable {
	
	public final int x;
	public final int y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof Point)) return false;
		Point o = (Point) other;
		return x == o.x && y == o.y;
	}
	
	@Override
	public int hashCode() { return x * 17 + y * 37; }
	
	@Override
	public String toString() { return "("+x+","+y+")"; }
}
