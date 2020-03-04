import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;

public class GUI extends JFrame implements MouseListener
{
	private static final long serialVersionUID = 6411499808530678723L;
	
	private PaintPanel paintPanel;
	private boolean running = false;
	private long clockSpeed;

	public GUI(Grid grid, int cellSize, int gap, long clockSpeed, Color aliveColor, Color deadColor, Color gapColor)
	{
		this.clockSpeed = clockSpeed;
		
		setTitle("Initialize Starting State, and then Right-Click to Begin");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		paintPanel = new PaintPanel(grid, cellSize, gap, aliveColor, deadColor, gapColor);
		getContentPane().add(paintPanel);
		paintPanel.addMouseListener(this);
		pack();
		setVisible(true);
	}
	
	@SuppressWarnings("unused")
	public static void main(String args[])
	{
		int numRows = 100;
		int numCols = 100;
		
		int cellSize = 5;
		int gap = 1;
		
		long clockSpeed = 100L;
		
		Color aliveColor = Color.WHITE;
		Color deadColor = Color.BLACK;
		Color gapColor = Color.DARK_GRAY;
		
		Grid grid = new Grid(numRows, numCols);		
		GUI gui = new GUI(grid, cellSize, gap, clockSpeed, aliveColor, deadColor, gapColor);
	}
	
	private class Play extends Thread
	{
		public void run()
		{
			setTitle("Running");
			running = true;
			while(running == true)
				paintPanel.tick(clockSpeed);
			setTitle("Paused");
		}
	}
	
	private ExecutorService executor = Executors.newFixedThreadPool(1);

	@Override
	public void mouseClicked(MouseEvent e)
	{
		if(e.getButton() == MouseEvent.BUTTON1 && running == false)
		{
			int x = e.getX() / (paintPanel.getGap() + paintPanel.getCellSize());
			int y = paintPanel.getNumCols() - e.getY() / (paintPanel.getGap() + paintPanel.getCellSize()) - 1;
			paintPanel.toggle(y, x);
		}
		else if(running == false)
			executor.execute(new Play());
		else
			running = false;
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		
	}
}
