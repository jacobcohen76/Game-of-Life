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
	private boolean firstPaint = true;
	
	private Color aliveColor;
	private Color deadColor;
	private Color gapColor;
	
	public int rowsToDisplay;
	public int colsToDisplay;
	public int relativeRow;
	public int relativeCol;
	
	public int shiftX;
	public int shiftY;

	public PaintPanel(Grid toPaint, int cellSize, int gap, Color aliveColor, Color deadColor, Color gapColor)
	{
		this.cellSize = cellSize;
		this.gap = gap;
		
		this.aliveColor = aliveColor;
		this.deadColor = deadColor;
		this.gapColor = gapColor;
		
		rowsToDisplay = 500;
		colsToDisplay = 500;
		relativeRow = 0;
		relativeCol = 0;
		shiftX = 0;
		shiftY = 0;
		
		grid = toPaint;		
	}
	
	public Dimension getPreferredSize()
	{
		Dimension preferredSize = new Dimension(0, 0);
		preferredSize.width = cellSize * colsToDisplay + (colsToDisplay + 1) * gap;
		preferredSize.height = cellSize * rowsToDisplay + (rowsToDisplay + 2) * gap;
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
		}
		
		for(int row = relativeRow; row < rowsToDisplay && row < (rowsToDisplay + relativeRow); row++)
		{
			for(int col = relativeCol; col < colsToDisplay && col < (colsToDisplay + relativeCol); col++)
			{
				Grid.Cell cell = grid.get(row, col);
				if(cell.prevStatus != cell.status)
					paint(g, cell);
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
		for(Grid.Cell cell : grid)
			paint(g, cell);
	}
	
	private int dx = 0;
	private int dy = 0;
	
	public void zoom(int amount, java.awt.Point p)
	{	
		Point maintainPosition = getPos(p);
		cellSize -= amount;
		if(cellSize <= 0)
			cellSize = 1;
		shiftX = p.x - (maintainPosition.x - relativeCol) * (gap + cellSize);
		shiftY = (maintainPosition.y - rowsToDisplay - relativeRow + 1) * (gap + cellSize) + p.y;
		
//		Point ungetP = ungetPos(maintainPosition);
//		shiftY -= ungetP.y - p.y;
//		ungetP = ungetPos(maintainPosition);
//		System.out.println(ungetP + " " + p + " " + cellSize);
//		int dx = p.x - ungetP.x;
//		
////		shiftX -= dx;
//		int dy = p.y - ungetP.y;
////		shiftX -= dy;
//		System.out.println(dx + " " + dy);
//		System.out.println(ungetPos(maintainPosition));
//		this.dx = dx;
//		this.dy = dy;
//		shiftX -= dx;
//		shiftY += dy;
		repaint();
	}
	
	public Point getPos(java.awt.Point p)
	{
		int x = (p.x - shiftX) / (gap + cellSize) + relativeCol;
		int y = ((shiftY - p.y) / (gap + cellSize) - 1) + rowsToDisplay + relativeRow;
		return new Point(x, y);
	}
	
	public Point getPos(Point p)
	{
		int x = (p.x - shiftX) / (gap + cellSize) + relativeCol;
		int y = ((shiftY - p.y) / (gap + cellSize) - 1) + rowsToDisplay + relativeRow;
		return new Point(x, y);
	}
	
	public Point ungetPos(java.awt.Point p)
	{
		int x = (p.x - relativeCol) * (gap + cellSize) + shiftX;
		int y = shiftY - (p.y - rowsToDisplay - relativeRow) * (gap + cellSize) + 1;
		return new Point(x, y);
	}
	
	public Point ungetPos(Point p)
	{
		int x = (p.x - relativeCol) * (gap + cellSize) + shiftX;
		int y = shiftY - (p.y - rowsToDisplay - relativeRow) * (gap + cellSize) + 1;
		return new Point(x, y);
	}
	
	private void paint(Graphics g, Grid.Cell cell)
	{		
		int x = (cell.pos.x - relativeCol) * (gap + cellSize) + shiftX;;
		int y = shiftY - (cell.pos.y - rowsToDisplay - relativeRow) * (gap + cellSize) + 1;
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
