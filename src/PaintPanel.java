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

/**
 * The view in our MVC design pattern.
 * Displays the model so the user can interact with it.
 * 
 * @author Jacob Cohen
 */
public class PaintPanel extends JPanel
{
	private static final long serialVersionUID = 8915539899387579004L;
	
	private int cellSize;
	private int gap;
	private volatile Grid grid;
	private boolean firstPaint = true;
	
	private Color aliveColor;
	private Color deadColor;
	private Color gapColor;
	
	public int shiftX;
	public int shiftY;
	int minSize;
	
	public PaintPanel(Grid toPaint, int cellSize, int gap, Color aliveColor, Color deadColor, Color gapColor)
	{
		this.cellSize = cellSize;
		this.gap = gap;
		minSize = cellSize;
		
		this.aliveColor = aliveColor;
		this.deadColor = deadColor;
		this.gapColor = gapColor;
		
		shiftX = 0;
		shiftY = 0;
		
		grid = toPaint;
	}
	
	public Dimension getPreferredSize()
	{
		int width = (cellSize + gap) * grid.getNumCols();
		int height = (cellSize + gap) * grid.getNumRows();
		return new Dimension(width, height);
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
	
	public void resetFirstPaint()
	{
		firstPaint = true;
	}
	
	public void paint(Graphics g)
	{
		if(firstPaint == true)
		{
			initBackground(g);
			initCells(g);
			firstPaint = false;
		}
		if(toRepaint != null)
		{
			g.setColor(color ? aliveColor : deadColor);
			g.fillRect(toRepaint.x, toRepaint.y, cellSize, cellSize);
			toRepaint = null;
		}
		else
		{
			initBackground(g);
			
			int startingRow = ((shiftY - getHeight()) / (gap + cellSize) - 1) + grid.getNumRows();
			int startingCol = - shiftX / (gap + cellSize);
			int finalRow = (shiftY / (gap + cellSize) - 1) + grid.getNumRows() + 1;
			int finalCol = (getWidth() - shiftX) / (gap + cellSize) + 1;
			
			if(startingRow < 0)
				startingRow = 0;
			if(startingCol < 0)
				startingCol = 0;
			if(finalCol > grid.getNumCols())
				finalCol = grid.getNumCols();
			if(finalRow > grid.getNumRows())
				finalRow = grid.getNumRows();
			
			for(int row = startingRow; row < finalRow; row++)
			{
				for(int col = startingCol; col < finalCol; col++)
				{
					Grid.Cell cell = grid.get(row, col);
					paint(g, cell);
				}
			}
		}
	}
	
	private void initBackground(Graphics g)
	{
		g.setColor(gapColor);
		g.fillRect(0, 0, getWidth(), getHeight());
	}
	
	private void initCells(Graphics g)
	{
		int startingRow = ((shiftY - getHeight()) / (gap + cellSize) - 1) + grid.getNumRows();
		int startingCol = - shiftX / (gap + cellSize);
		int finalRow = (shiftY / (gap + cellSize) - 1) + grid.getNumRows() + 1;
		int finalCol = (getWidth() - shiftX) / (gap + cellSize) + 1;
		
		if(startingRow < 0)
			startingRow = 0;
		if(startingCol < 0)
			startingCol = 0;
		if(finalCol > grid.getNumCols())
			finalCol = grid.getNumCols();
		if(finalRow > grid.getNumRows())
			finalRow = grid.getNumRows();
		
		for(int row = startingRow; row < finalRow; row++)
		{
			for(int col = startingCol; col < finalCol; col++)
			{
				Grid.Cell cell = grid.get(row, col);
				paint(g, cell);
			}
		}
	}
	
	public boolean zoom(int amount, java.awt.Point p)
	{
		if(cellSize == minSize && amount >= 0)
			return false;
		Point maintainPosition = getPos(p);
		Point oldCorner = ungetPos(maintainPosition);
		cellSize -= amount;
		if(cellSize <= minSize)
			cellSize = minSize;
		shiftX = p.x - maintainPosition.x * (gap + cellSize);
		shiftY = (maintainPosition.y - grid.getNumRows() + 1) * (gap + cellSize) + p.y;
		shiftX += oldCorner.x - p.x;
		shiftY += oldCorner.y - p.y;
		return true;
	}
	
	public Point getPos(java.awt.Point p)
	{
		int x = (p.x - shiftX) / (gap + cellSize);
		int y = ((shiftY - p.y) / (gap + cellSize) - 1) + grid.getNumRows();
		return new Point(x, y);
	}
	
	public Point getPos(Point p)
	{
		int x = (p.x - shiftX) / (gap + cellSize);
		int y = ((shiftY - p.y) / (gap + cellSize) - 1) + grid.getNumRows();
		return new Point(x, y);
	}
	
	public Point ungetPos(java.awt.Point p)
	{
		int x = p.x * (gap + cellSize) + shiftX;
		int y = shiftY - (p.y - grid.getNumRows()+ 1) * (gap + cellSize);
		return new Point(x, y);
	}
	
	public Point ungetPos(Point p)
	{
		int x = p.x * (gap + cellSize) + shiftX;
		int y = shiftY - (p.y - grid.getNumRows() + 1) * (gap + cellSize);
		return new Point(x, y);
	}
	
	private void paint(Graphics g, Grid.Cell cell)
	{		
		int x = cell.pos.x * (gap + cellSize) + shiftX;
		int y = shiftY - (cell.pos.y - grid.getNumRows() + 1) * (gap + cellSize);
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
		grid.toggle(row, col);
	}
	
	public void toggle(Point pos)
	{
		toggle(pos.y, pos.x);
	}
	
	public SaveableData getSaveableData()
	{
		SaveableData saveableData = grid.getSaveableData();
		saveableData.cellSize = cellSize;
		saveableData.shiftX = shiftX;
		saveableData.shiftY = shiftY;
		return saveableData;
	}
	
	public void load(SaveableData saveableData)
	{
		grid.loadSaveableData(saveableData);
		shiftX = saveableData.shiftX;
		shiftY = saveableData.shiftY;
		cellSize = saveableData.cellSize;
		repaint();
	}
	
	public void set(int row, int col, boolean status)
	{
		grid.setStatus(row, col, status);
	}
	
	private Point toRepaint = null;
	private boolean color = false;
	
	public void markStroke(java.awt.Point p, boolean status)
	{
		Point pt = getPos(p);
		if(grid.isWithinRealBounds(pt.y, pt.x))
		{
			color = status;
			grid.setStatus(pt, status);
			toRepaint = ungetPos(pt);
			repaint(100, toRepaint.x, toRepaint.y, cellSize, cellSize);
		}
	}
	
	public void set(Point pos, boolean status)
	{
		grid.setStatus(pos, status);
	}
}
