import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JPanel;

public class PaintPanel extends JPanel
{
	private static final long serialVersionUID = 8915539899387579004L;
	
	private int cellSize;
	private int gap;
	private volatile Grid grid;
	
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
	}
	
	public Dimension getPreferredSize()
	{
		Dimension preferredSize = new Dimension(0, 0);
		preferredSize.width = cellSize * grid.getNumCols() + (grid.getNumCols() + 1) * gap;
		preferredSize.height = cellSize * grid.getNumRows() + (grid.getNumRows() + 2) * gap;
		return preferredSize;
	}
	
	public void save(File file) throws FileNotFoundException, IOException
	{
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
		oos.writeObject(grid.getSaveableData());
		oos.close();
	}
	
	public void load(File file) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
		grid.loadSaveableData((SaveableData)ois.readObject());
		ois.close();
	}
	
	public void tick(long millis)
	{
		long begin, end;
		
		begin = System.currentTimeMillis();
		grid.tick();
		repaint();
		end = System.currentTimeMillis();
		millis -= end - begin;
		
		pause(millis);
	}
	
	private void pause(long millis)
	{
		if(millis > 0)
		{
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {}
		}
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
		int y = (grid.getNumRows() - cell.pos.y - 1) * (cellSize + gap) + gap + gap;
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
	
	public void toggle(Point pos)
	{
		toggle(pos.y, pos.x);
	}
	
	public void set(int row, int col, boolean status)
	{
		grid.setStatus(row, col, status);
	}
	
	public void set(Point pos, boolean status)
	{
		grid.setStatus(pos, status);
	}
}
