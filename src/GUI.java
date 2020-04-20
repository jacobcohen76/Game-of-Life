import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;

public class GUI extends JFrame implements MouseListener, MouseMotionListener, KeyListener
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
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		getContentPane().add(paintPanel);
		paintPanel.addMouseListener(this);
		paintPanel.addMouseMotionListener(this);
		this.addKeyListener(this);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	@SuppressWarnings("unused")
	public static void main(String args[])
	{
		int numRows = 100;
		int numCols = 100;
		
		int cellSize = 8;
		int gap = 0;
		
		long clockSpeed = 100L;
		
		Color aliveColor = Color.RED;
		Color deadColor = Color.CYAN;
		Color gapColor = Color.WHITE;
		
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
		if(e.getButton() == MouseEvent.BUTTON1)
			paintPanel.toggle(getPos(e));
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

	@Override
	public void keyTyped(KeyEvent e)
	{

		
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_SPACE)
			if(playing == false)
				executor.execute(new Play(Thread.MAX_PRIORITY));
			else
				playing = false;
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		
	}
}
