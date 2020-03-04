
public class Testing
{
	public static void main(String args[])
	{
		Grid grid = new Grid(17, 17);		
		int row;
		
		row = 2;
		grid.setStatus(row, 4, true);
		grid.setStatus(row, 5, true);
		grid.setStatus(row, 6, true);
		grid.setStatus(row, 4+6, true);
		grid.setStatus(row, 5+6, true);
		grid.setStatus(row, 6+6, true);
		
		row = 7;
		grid.setStatus(row, 4, true);
		grid.setStatus(row, 5, true);
		grid.setStatus(row, 6, true);
		grid.setStatus(row, 4+6, true);
		grid.setStatus(row, 5+6, true);
		grid.setStatus(row, 6+6, true);
		
		row = 9;
		grid.setStatus(row, 4, true);
		grid.setStatus(row, 5, true);
		grid.setStatus(row, 6, true);
		grid.setStatus(row, 4+6, true);
		grid.setStatus(row, 5+6, true);
		grid.setStatus(row, 6+6, true);
		
		row = 14;
		grid.setStatus(row, 4, true);
		grid.setStatus(row, 5, true);
		grid.setStatus(row, 6, true);
		grid.setStatus(row, 4+6, true);
		grid.setStatus(row, 5+6, true);
		grid.setStatus(row, 6+6, true);
		
		row = 4;
		grid.setStatus(row, 2, true);
		grid.setStatus(row, 7, true);
		grid.setStatus(row, 9, true);
		grid.setStatus(row, 14, true);
		
		row = 5;
		grid.setStatus(row, 2, true);
		grid.setStatus(row, 7, true);
		grid.setStatus(row, 9, true);
		grid.setStatus(row, 14, true);
		
		row = 6;
		grid.setStatus(row, 2, true);
		grid.setStatus(row, 7, true);
		grid.setStatus(row, 9, true);
		grid.setStatus(row, 14, true);
		
		row = 10;
		grid.setStatus(row, 2, true);
		grid.setStatus(row, 7, true);
		grid.setStatus(row, 9, true);
		grid.setStatus(row, 14, true);
		
		row = 11;
		grid.setStatus(row, 2, true);
		grid.setStatus(row, 7, true);
		grid.setStatus(row, 9, true);
		grid.setStatus(row, 14, true);
		
		row = 12;
		grid.setStatus(row, 2, true);
		grid.setStatus(row, 7, true);
		grid.setStatus(row, 9, true);
		grid.setStatus(row, 14, true);
		
		while(true)
		{
			print(grid);
			grid.tick();
			System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
			try
			{
				Thread.sleep(1000);
			}
			catch(Exception ex)
			{
				
			}
		}
	}
	
	private static void print(Grid grid)
	{
		for(int row = grid.getNumRows() - 1; row >= 0; row--)
		{
			for(int col = 0; col < grid.getNumCols(); col++)
				System.out.print(grid.getStatus(row, col) ? "#" : "_");
			System.out.print("\n");
		}
	}
}
