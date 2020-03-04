import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;

public class GUI extends JFrame implements MouseListener, MouseMotionListener
{
	private static final long serialVersionUID = 6411499808530678723L;
	
	private long clockSpeed;
	private int button;
	private boolean playing;
	
	private ExecutorService executor;
	private PaintPanel paintPanel;
	
	public GUI(Grid grid, int cellSize, int gap, long clockSpeed, Color aliveColor, Color deadColor, Color gapColor)
	{
		this.clockSpeed = clockSpeed;
		button = MouseEvent.NOBUTTON;
		playing = false;
		
		executor = Executors.newFixedThreadPool(1);
		paintPanel = new PaintPanel(grid, cellSize, gap, aliveColor, deadColor, gapColor);
		
		setTitle("The Game of Life");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		getContentPane().add(paintPanel);
		paintPanel.addMouseListener(this);
		paintPanel.addMouseMotionListener(this);
		pack();
		setVisible(true);
	}
	
	@SuppressWarnings("unused")
	public static void main(String args[])
	{
		int numRows = 100;
		int numCols = 100;
		
		int cellSize = 8;
		int gap = 1;
		
		long clockSpeed = 100L;
		
		Color aliveColor = Color.BLACK;
		Color deadColor = Color.WHITE;
		Color gapColor = Color.LIGHT_GRAY;
		
		Grid grid = new Grid(numRows, numCols);		
		GUI gui = new GUI(grid, cellSize, gap, clockSpeed, aliveColor, deadColor, gapColor);
	}
	
	private class Play extends Thread
	{
		public Play(int priority)
		{
			this.setPriority(priority);
		}
		
		public void run()
		{
			setTitle("▶ Running");
			playing = true;
			while(playing == true)
				paintPanel.tick(clockSpeed);
			setTitle("❚❚ Paused");
		}
	}
	
	private Point getPos(MouseEvent e)
	{
		int x = e.getX() / (paintPanel.getGap() + paintPanel.getCellSize());
		int y = paintPanel.getNumCols() - e.getY() / (paintPanel.getGap() + paintPanel.getCellSize()) - 1;
		return new Point(x, y);
	}
	
	@Override
	public void mouseClicked(MouseEvent e)
	{
		button = e.getButton();
		if(e.getButton() == MouseEvent.BUTTON1 && playing == false)
			paintPanel.toggle(getPos(e));
		else if(playing == false)
			executor.execute(new Play(Thread.MAX_PRIORITY));
		else
			playing = false;
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		button = e.getButton();
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		button = e.getButton();
		paintPanel.repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		paintPanel.set(getPos(e), button == MouseEvent.BUTTON1);
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		
	}
}
