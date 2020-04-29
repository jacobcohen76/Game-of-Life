import java.awt.Color;

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
		GUI gui = new GUI(grid, cellSize, gap, clockSpeed, aliveColor, deadColor, gapColor, repaintRate);
	}
}
