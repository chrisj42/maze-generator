package game.maze;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import game.maze.MazeGrid.MazeTraveler;
import jdk.nashorn.internal.scripts.JO;

class MazePanel extends JPanel {
	
	private static final int PAD = 20;
	private static final Object paintLock = new Object();
	public static final int WAIT = 10;
	
	private MazeGrid maze;
	private final int width, height, size;
	
	// track lines
	//private final HashSet<Line> allPaths = new HashSet<>();
	//private HashMap<Point, Integer> pathIndexes = new HashMap<>(); // allows fetching of the line in curPath that lies on the given point.
	//private DLList<Line> curPath = new DLList<>();
	//private Point prevPoint;
	private Line[] mainLines, subLines;
	
	void append(Color color, Line... moreLines) { append(true, color, moreLines); }
	void append(boolean main, Color color, Line... moreLines) {
		for(Line line: moreLines)
			line.setColor(color);
		append(moreLines);
	}
	void append(Line... moreLines) { append(true, moreLines); }
	void append(boolean main, Line... moreLines) {
		if(main) {
			if(mainLines == null) mainLines = moreLines;
			else mainLines = join(mainLines, moreLines);
		}
		else {
			if(subLines == null) subLines = moreLines;
			else subLines = join(subLines, moreLines);
		}
	}
	void setMainLines(Line... mainLines) {
		this.mainLines = mainLines;
	}
	void setSubLines(Line... subLines) {
		this.subLines = subLines;
	}
	void isolate(Color subColor, Line... lines) {
		isolate(lines);
		for(Line line: subLines)
			line.setColor(subColor);
	}
	void isolate(Line... lines) {
		if(subLines == null) subLines = new Line[0];
		if(mainLines != null) append(false, mainLines);
		mainLines = lines;
	}
	
	private Line[] join(Line[] l1, Line[] l2) {
		Line[] lines = new Line[l1.length+l2.length];
		System.arraycopy(l1, 0, lines, 0, l1.length);
		System.arraycopy(l2, 0, lines, l1.length, l2.length);
		return lines;
	}
	
	public MazePanel(int width, int height, int size) {
		this.width = width;
		this.height = height;
		this.size = size;
		// prevPoint = new Point(-1, 1);
		maze = new MazeGrid(width, height);
	}
	
	@Override public Dimension getPreferredSize() { return new Dimension(width*size + PAD*2, height*size + PAD*2); }
	
	private boolean painted = false;
	@Override
	protected void paintComponent(Graphics g) {
		synchronized (paintLock) {
			super.paintComponent(g);
			if(maze != null)
				maze.drawMaze(g, PAD, PAD, size);
			if(subLines != null)
				for(Line line: subLines)
					line.draw(g, PAD, PAD, size);
			if(mainLines != null)
				for(Line line: mainLines)
					line.draw(g, PAD, PAD, size);
			painted = true;
		}
	}
	
	void pause() { pause(WAIT); }
	void pause(int time) { pause(true, time); }
	void pause(boolean paint, int time) {
		/*if(paint) {
			synchronized (paintLock) {
				painted = false;
				repaint();
			}
		}
		
		try {
			Thread.sleep(time);
		} catch(InterruptedException ignored) {
		}
		
		if(paint) {
			while(!painted) {
				try {
					Thread.sleep(2);
				} catch(InterruptedException ignored) {
				}
			}
		}*/
	}
	
	
	public static void main(String[] args) {
		int width = 60;
		int height = 30;
		int size = 18;
		
		int choice;
		do {
			// JFrame frame = new JFrame("Maze");
			// frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			System.out.println("creating maze panel...");
			MazePanel mazePanel = new MazePanel(width, height, size);
			System.out.println("maze panel created.");
			// frame.add(mazePanel);
			// frame.pack();
			//
			// frame.setVisible(true);
			
			mazePanel.pause(false, 1000);
			
			mazePanel.maze.generate(mazePanel);
			
			mazePanel.pause(3000);
			
			MazeTraveler traveler = mazePanel.maze.new MazeTraveler(mazePanel);
			traveler.findLongestPath();
			
			mazePanel.pause(false, 10_000);
			mazePanel.setMainLines((Line[]) null);
			mazePanel.setSubLines((Line[]) null);
			//mazePanel.repaint();
			choice = JOptionPane.showConfirmDialog(null, mazePanel, "Maze", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
		} while(choice == JOptionPane.OK_OPTION);
	}
}
