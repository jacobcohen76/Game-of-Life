import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class PaintPanel extends JPanel
{
	private static final long serialVersionUID = 8915539899387579004L;
	
	private int cellSize;
	private int gap;
	private Grid grid;
	
	private Color aliveColor;
	private Color deadColor;
	private Color gapColor;

	public PaintPanel(Grid toPaint, int cellSize, int gap, Color aliveColor, Color deadColor, Color gapColor)
	{
		this.cellSize = cellSize;
		this.gap = gap;
		
		this.aliveColor = aliveColor;
		this.deadColor = deadColor;
		this.gapColor = gapColor;
		
		grid = toPaint;		
		Dimension preferredSize = new Dimension(0, 0);
		preferredSize.width = cellSize * grid.getNumCols() + (grid.getNumCols() + 1) * gap;
		preferredSize.height = cellSize * grid.getNumRows() + (grid.getNumRows() + 2) * gap;
		setPreferredSize(preferredSize);
	}
	
	public void tick(long millis)
	{
		grid.tick();
		repaint();
		pause(millis);
	}
	
	private void pause(long millis)
	{
		try { Thread.sleep(millis); } catch (InterruptedException e) {}
	}
	
	public void paint(Graphics g)
	{
		g.setColor(gapColor);
		g.fillRect(0, 0, getWidth(), getHeight());
		for(Grid.Cell cell : grid)
			paint(g, cell);
	}
	
	private void paint(Graphics g, Grid.Cell cell)
	{		
		int x = cell.pos.x * (cellSize + gap) + gap;
		int y = (grid.getNumCols() - cell.pos.y - 1) * (cellSize + gap) + gap + gap;
		g.setColor(grid.isAlive(cell) ? aliveColor : deadColor);
		g.fillRect(x, y, cellSize, cellSize);
	}
	
	public int getGap()
	{
		return gap;
	}
	
	public int getCellSize()
	{
		return cellSize;
	}
	
	public int getNumRows()
	{
		return grid.getNumRows();
	}
	
	public int getNumCols()
	{
		return grid.getNumCols();
	}
	
	public void toggle(int row, int col)
	{
		grid.get(row, col).status = !grid.getStatus(row, col);
		repaint();
	}
}
