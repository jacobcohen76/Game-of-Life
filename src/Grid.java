import java.util.Iterator;
import java.util.LinkedList;

/**
 * The model of our MVC design pattern. Models the grid and
 * the building block Cells that it is made up of.
 * 
 * @author Jacob Cohen
 */
public class Grid implements Iterable<Grid.Cell>
{	
	public static final boolean ALIVE = true;
	public static final boolean DEAD = false;
	
	private int numRows;
	private int numCols;
	private int rowPad;
	private int colPad;
	private Cell[] array;
	
	private LinkedList<Cell> aliveList;
	public LinkedList<Cell> changedList;
	
	public Grid(int numRows, int numCols, int rowPad, int colPad)
	{
		numRows += 2 * rowPad;
		numCols += 2 * colPad;
		
		this.numRows = numRows;
		this.numCols = numCols;
		this.rowPad = 0;
		this.colPad = 0;
		
		array = new Cell[numRows * numCols];
		
		for(int row = 0; row < numRows; row++)
			for(int col = 0; col < numCols; col++)
				set(row, col, new Cell(new Point(col - colPad, row - rowPad)));
		
		this.numRows -= 2 * rowPad;
		this.numCols -= 2 * colPad;		
		this.rowPad = rowPad;
		this.colPad = colPad;
		
		for(int row = -rowPad; row < (this.numRows + rowPad); row++)
			for(int col = -colPad; col < (this.numCols + colPad); col++)
				get(row, col).initNeighbors();
		
		aliveList = new LinkedList<Cell>();
		changedList = new LinkedList<Cell>();
	}
	
	public Grid(int numRows, int numCols)
	{
		this(numRows, numCols, 3, 3);
	}
	
	public int getNumRows()
	{
		return numRows;
	}
	
	public int getNumCols()
	{
		return numCols;
	}
	
	public SaveableData getSaveableData()
	{
		SaveableData data = new SaveableData();
		data.alive = new boolean[numRows][numCols];
		
		for(int i = 0; i < data.alive.length; i++)
			for(int j = 0; j < data.alive[i].length; j++)
				data.alive[i][j] = get(i, j).status;
		
		data.numRows = numRows;
		data.numCols = numCols;
		data.rowPad = rowPad;
		data.colPad = colPad;
		
		return data;
	}
	
	public void loadSaveableData(SaveableData data)
	{
		numRows = data.numRows + 2 * data.rowPad;
		numCols = data.numCols + 2 * data.colPad;
		rowPad = 0;
		colPad = 0;
		
		array = new Cell[numRows * numCols];
		
		for(int row = 0; row < numRows; row++)
			for(int col = 0; col < numCols; col++)
				set(row, col, new Cell(new Point(col - data.colPad, row - data.rowPad)));
		
		rowPad = data.rowPad;
		colPad = data.colPad;
		numRows = data.numRows;
		numCols = data.numCols;
		
		for(int i = 0; i < data.alive.length; i++)
			for(int j = 0; j < data.alive[i].length; j++)
				get(i, j).status = data.alive[i][j];
		for(int row = -rowPad; row < (this.numRows + rowPad); row++)
			for(int col = -colPad; col < (this.numCols + colPad); col++)
				get(row, col).initNeighbors();
	}
	
	public boolean isWithinBounds(int row, int col)
	{
		boolean withinBounds = true;
		
		withinBounds &= -rowPad <= row;
		withinBounds &= -colPad <= col;
		withinBounds &= row < (numRows + rowPad);
		withinBounds &= col < (numCols + colPad);
		
		return withinBounds;
	}
	
	public boolean isWithinRealBounds(int row, int col)
	{
		boolean withinBounds = true;
		
		withinBounds &= 0 <= row;
		withinBounds &= 0 <= col;
		withinBounds &= row < numRows;
		withinBounds &= col < numCols;
		
		return withinBounds;
	}
	
	public boolean isWithinRealBounds(Point pos)
	{
		return isWithinRealBounds(pos.y, pos.x);
	}
	
	public boolean isWithinBounds(Point pos)
	{
		return isWithinBounds(pos.y, pos.x);
	}
	
	private int indexOf(int row, int col)
	{
		return ((col + colPad) + (row + rowPad) * (numCols + 2 * colPad));
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
	
	/**
	 * used to get an array of pointers to the 8 (or less if this
	 * Cell is located in a corner) of a given Cell
	 * 
	 * @param cell
	 * the cell to get the neighbors of
	 * 
	 * @return
	 * an array of pointers to the 8 neighbors surround a given Cell,
	 * can be less than 8 if it is in a corner
	 */
	private Cell[] getNeighbors(Cell cell)
	{
		LinkedList<Cell> neighbors = new LinkedList<Cell>();
		
		neighbors.add(cell.getRelative(Vector.NORTH));
		neighbors.add(cell.getRelative(Vector.EAST));
		neighbors.add(cell.getRelative(Vector.SOUTH));
		neighbors.add(cell.getRelative(Vector.WEST));
		neighbors.add(cell.getRelative(Vector.NORTHEAST));
		neighbors.add(cell.getRelative(Vector.SOUTHEAST));
		neighbors.add(cell.getRelative(Vector.SOUTHWEST));
		neighbors.add(cell.getRelative(Vector.NORTHWEST));
		
		Iterator<Cell> itr = neighbors.iterator();
		while(itr.hasNext())
			if(itr.next() == null)
				itr.remove();
		
		Cell[] neighborArray = new Cell[neighbors.size()];
		
		int i = 0;
		for(Cell neighbor : neighbors)
			neighborArray[i++] = neighbor;
		
		return neighborArray;
	}
	
	/**
	 * Updates the internal integer value stored in each cell that counts
	 * how many of its neighbors are still alive
	 */
	private void updateLivingNeighborMap()
	{
		for(Cell cell : array)
			cell.updateLivingNeighbors();
	}
	
	/**
	 * Updates the status of which Cells are alive and dead.
	 * Major efficiency boost is achieved by maintaining a list
	 * of living Cells and only Checking the Cells in this list,
	 * and their dead neighbors.
	 */
	private void updateStatus()
	{
		Iterator<Cell> itr = aliveList.iterator();
		LinkedList<Cell> toAdd = new LinkedList<Cell>();
		while(itr.hasNext())
		{
			Cell cell = itr.next();
			cell.updateStatus();
			for(Cell neighbor : cell.neighbors)
			{
				if(neighbor.status == DEAD)
				{
					neighbor.updateStatus();
					if(neighbor.status == ALIVE)
						toAdd.push(neighbor);
				}
			}
			if(cell.status == DEAD)
				itr.remove();
		}
		aliveList.addAll(toAdd);
	}
	
	/**
	 * Moves forward 1 generation
	 */
	public void tick()
	{
		updateLivingNeighborMap();
		updateStatus();
	}
	
	public boolean isAlive(Cell cell)
	{
		return (cell != null) && cell.status == ALIVE;
	}
	
	public boolean isDead(Cell cell)
	{
		return (cell == null) || cell.status == DEAD;
	}
	
	/**
	 * Molecular building block of our Grid, kind of like
	 * a Node.
	 * 
	 * @author Jacob Cohen
	 */
	public class Cell
	{	
		protected Point pos;
		protected Cell[] neighbors;
		private int numLivingNeighbors;
		public boolean status;
		public boolean prevStatus;
		
		public Cell(Point pos, boolean status)
		{
			this.pos = pos;
			this.status = status;
			prevStatus = status;
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
			prevStatus = status;
			if(isDead(this))
				status = numLivingNeighbors == 3;
			else if(numLivingNeighbors != 2 && numLivingNeighbors != 3)
				status = DEAD;
		}
		
		public void updateLivingNeighbors()
		{
			numLivingNeighbors = 0;
			for(Cell cell : neighbors)
				if(isAlive(cell))
					numLivingNeighbors++;
		}
		
		/**
		 * gets the Cell whose position is equal
		 * to the position of this Cell plus some
		 * Vector v, so if we are at the position
		 * (100, 100), and the Vector v is <1, 3>
		 * this method will return the Cell located
		 * at (101, 103).
		 * 
		 * @param v
		 * the relative distance from the Cell position
		 * we want to get
		 * 
		 * @return
		 * the Cell located at the position Vector v units
		 * away from the relative position of this Cell
		 */
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
		
		/**
		 * Perfect hash, abuses the fact that each Cell in our Grid will
		 * have a unique Position, so we can use the Cantor Pairing function
		 * to get a Perfect hash for the unique ordered pair of its position
		 */
		public int hashCode()
		{
			return pos.hashCode();
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
	
	/**
	 * Sets the status of the Cell at the specified row and column position,
	 * and updates the internal values necessary such as our alive list, and
	 * and the previous status of that Cell.  If this used at the same time
	 * update status is called a concurrency error may occur. This is because
	 * of a critical section that occurs when iterating through our alive list,
	 * and this method tried to add to that list at the same time it is being
	 * iterated through. To prevent this, simply do not call this method when
	 * the game is running.
	 *  
	 * @param row
	 * the row index of the Cell to set the status of
	 * 
	 * @param col
	 * the column index of the Cell to set the status of
	 * 
	 * @param status
	 * the status to set the Cell at the specified row and column index of, use
	 * true for ALIVE, and false for DEAD.
	 */
	public void setStatus(int row, int col, boolean status)
	{
		if(isWithinBounds(row, col))
		{
			Cell cell = get(row, col);
			cell.status = status;
			if(status == ALIVE)
				aliveList.add(cell);
		}
	}
	
	public void toggle(int row, int col)
	{
		setStatus(row, col, !get(row, col).status);
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
