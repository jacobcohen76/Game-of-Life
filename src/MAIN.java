import java.awt.Color;
import java.awt.Image;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;

public class MAIN
{
	@SuppressWarnings("unused")
	public static void main(String args[])
	{
		System.setProperty("sun.java2d.opengl", "true");
		
		LoadFromSettings.loadSettings();
		int numRows = LoadFromSettings.numRows;
		int numCols = LoadFromSettings.numCols;
		int cellSize = LoadFromSettings.cellSize;
		int gap = LoadFromSettings.gap;
		long clockSpeed = LoadFromSettings.clockSpeed;
		long repaintRate = LoadFromSettings.repaintRate;
		Color aliveColor = LoadFromSettings.aliveColor;
		Color deadColor = LoadFromSettings.deadColor;
		Color gapColor = LoadFromSettings.gapColor;
		
		Grid grid = new Grid(numRows, numCols);
		List<Image> icons = new LinkedList<Image>();
		icons.add((new ImageIcon("resources\\icon_16x16.png")).getImage());
		icons.add((new ImageIcon("resources\\icon_32x32.png")).getImage());
		icons.add((new ImageIcon("resources\\icon_64x64.png")).getImage());
		icons.add((new ImageIcon("resources\\icon_128x128.png")).getImage());
		GUI gui = new GUI(grid, cellSize, gap, clockSpeed, aliveColor, deadColor, gapColor, repaintRate, icons);
	}
}
