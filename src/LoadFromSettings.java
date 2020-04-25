import java.awt.Color;
import java.io.File;
import java.util.Scanner;

public class LoadFromSettings
{
	public static long clockSpeed;
	public static long repaintRate;
	
	public static int numRows;
	public static int numCols;
	
	public static int cellSize;
	public static int gap;
	
	public static Color aliveColor;
	public static Color deadColor;
	public static Color gapColor;
	
	public static void loadSettings()
	{
		try
		{
			File file = new File("resources\\settings.txt");
			Scanner scan = new Scanner(file);
			scan.next();
			scan.next();
			numRows = scan.nextInt();
			scan.next();
			scan.next();
			numCols = scan.nextInt();
			scan.next();
			scan.next();
			cellSize = scan.nextInt();
			scan.next();
			scan.next();
			gap = scan.nextInt();
			scan.next();
			scan.next();
			clockSpeed = scan.nextLong();
			scan.next();
			scan.next();
			repaintRate = scan.nextLong();
			scan.next();
			scan.next();
			aliveColor = new Color(scan.nextInt(16));
			scan.next();
			scan.next();
			deadColor = new Color(scan.nextInt(16));
			scan.next();
			scan.next();
			gapColor = new Color(scan.nextInt(16));
			scan.close();
		}
//		catch (FileNotFoundException e)
//		{
//			e.printStackTrace();
//			loadDefaults();
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			loadDefaults();
//		}
//		catch(Error e)
//		{
//			e.printStackTrace();
//			loadDefaults();
//		}
		catch(Throwable t)
		{
			t.printStackTrace();
			loadDefaults();
		}
	}
	
	private static void loadDefaults()
	{
		numRows = 800;
		numCols = 800;
		cellSize = 1;
		gap = 1;
		clockSpeed = 100L;
		repaintRate = 7L;
		aliveColor = Color.BLACK;
		deadColor = Color.WHITE;
		gapColor = Color.GRAY;
	}
}
