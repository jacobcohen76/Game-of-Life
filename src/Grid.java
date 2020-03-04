import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class Grid implements Iterable<Grid.Cell>
{
	public static final boolean ALIVE = true;
	public static final boolean DEAD = false;
	
	private int numRows;
	private int numCols;
	private Cell[] array;
	private HashMap<Cell, Integer> livingNeighborMap;
	
	public Grid(int numRows, int numCols)
	{
		this.numRows = numRows;
		this.numCols = numCols;
		array = new Cell[numRows * numCols];
		livingNeighborMap = new HashMap<Cell, Integer>();
		
		for(int row = 0; row < numRows; row++)
			for(int col = 0; col < numCols; col++)
				set(row, col, new Cell(new Point(col, row)));
		
		for(int row = 0; row < numRows; row++)
			for(int col = 0; col < numCols; col++)
				get(row, col).initNeighbors();
	}
	
	public int getNumRows()
	{
		return numRows;
	}
	
	public int getNumCols()
	{
		return numCols;
	}
	
	public boolean isWithinBounds(int row, int col)
	{
		boolean withinBounds = true;
		
		withinBounds &= 0 <= row;
		withinBounds &= 0 <= col;
		withinBounds &= row < numRows;
		withinBounds &= col < numCols;
		
		return withinBounds;
	}
	
	public boolean isWithinBounds(Point pos)
	{
		return isWithinBounds(pos.y, pos.x);
	}
	
	private int indexOf(int row, int col)
	{
		return (col + (row * numCols));
	}
	
	private Cell get(int index)
	{
		return array[index];
	}
	
	public Cell get(int row, int col)
	{
		return isWithinBounds(row, col) ? get(indexOf(row, col)) : null;
	}
	
	public Cell get(Point pos)
	{
		return get(pos.y, pos.x);
	}
	
	public Cell set(int row, int column, Cell data)
	{
		Cell prev = get(row, column);
		array[indexOf(row, column)] = data;
		return prev;
	}
	
	public Cell set(Point pos, Cell data)
	{
		return set(pos.y, pos.x, data);
	}
	
	private HashSet<Cell> getNeighbors(Cell cell)
	{
		HashSet<Cell> neighbors = new HashSet<Cell>();
		neighbors.add(cell.getRelative(Vector.NORTH));
		neighbors.add(cell.getRelative(Vector.EAST));
		neighbors.add(cell.getRelative(Vector.SOUTH));
		neighbors.add(cell.getRelative(Vector.WEST));
		neighbors.add(cell.getRelative(Vector.NORTHEAST));
		neighbors.add(cell.getRelative(Vector.SOUTHEAST));
		neighbors.add(cell.getRelative(Vector.SOUTHWEST));
		neighbors.add(cell.getRelative(Vector.NORTHWEST));
		return neighbors;	
	}
	
	private void updateLivingNeighborMap()
	{
		for(Cell cell : array)
			livingNeighborMap.put(cell, cell.getNumLivingNeighbors());
	}
	
	public void tick()
	{
		updateLivingNeighborMap();
		for(Cell cell : array)
			cell.updateStatus();
	}
	
	public boolean isAlive(Cell cell)
	{
		return (cell != null) && cell.status == ALIVE;
	}
	
	public boolean isDead(Cell cell)
	{
		return (cell == null) || cell.status == DEAD;
	}
	
	public class Cell
	{
		protected Point pos;
		protected boolean status;
		private HashSet<Cell> neighbors;
		
		public Cell(Point pos, boolean status)
		{
			this.pos = pos;
			this.status = status;
			neighbors = null;
		}
		
		public Cell(Point pos)
		{
			this(pos, DEAD);
		}
		
		public Cell()
		{
			this(null);
		}
		
		protected void initNeighbors()
		{
			neighbors = getNeighbors(this);
		}
		
		protected void updateStatus()
		{
			int numLivingNeighbors = livingNeighborMap.get(this);
			if(isDead(this))
				status = numLivingNeighbors == 3;
			else if(numLivingNeighbors != 2 && numLivingNeighbors != 3)
				status = DEAD;
		}
		
		protected int getNumLivingNeighbors()
		{
			int numLivingNeighbors = 0;
			for(Cell cell : neighbors)
				numLivingNeighbors += isAlive(cell) ? 1 : 0;
			return numLivingNeighbors;
		}
		
		protected Cell getRelative(Vector v)
		{
			return get(Point.add(pos, v));
		}
		
		public boolean equals(Object obj)
		{
			if(this == obj)
				return true;
			else if(obj instanceof Cell)
				return obj.hashCode() == this.hashCode();
			else
				return false;
		}
		
		public int hashCode()
		{
			return pos.toString().hashCode();
		}
		
		public String toString()
		{
			return pos + ", ? = " + status; 
		}
	}
	
	public boolean getStatus(int row, int col)
	{
		return get(row, col).status;
	}
	
	public boolean getStatus(Point pos)
	{
		return getStatus(pos.y, pos.x);
	}
	
	public void setStatus(int row, int col, boolean status)
	{
		get(row, col).status = status;
	}
	
	public void setStatus(Point pos, boolean status)
	{
		setStatus(pos.y, pos.x, status);
	}
	
	private class GridIterator implements Iterator<Cell>
	{
		private int row;
		private int col;
		
		public GridIterator()
		{
			row = numRows - 1;
			col = -1;
		}

		public boolean hasNext()
		{
			return (row != 0 || col != (numCols - 1));
		}

		public Cell next()
		{
			if(col == (numCols - 1))
			{
				row--;
				col = 0;
			}
			else
				col++;
			
			return get(row, col);
		}
	}
	
	public Iterator<Cell> iterator()
	{
		return new GridIterator();
	}
}
