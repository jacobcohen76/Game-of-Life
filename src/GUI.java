import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;

public class GUI extends JFrame implements MouseListener, MouseMotionListener, KeyListener, MouseWheelListener
{
	private static final long serialVersionUID = 6411499808530678723L;
	private static final int TOGGLE = 0, LIFE = 1, KILL = 2;
	
	private long clockSpeed;
	private int button;
	private boolean playing;
	private int statusMode;
	private int strokeMode;
	private ExecutorService executor;
	private PaintPanel paintPanel;
	private java.awt.Point currentMousePosition;
	private volatile boolean changed;
	
	public GUI(Grid grid, int cellSize, int gap, long clockSpeed, Color aliveColor, Color deadColor, Color gapColor, long repaintRate)
	{
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.clockSpeed = clockSpeed;
		button = MouseEvent.NOBUTTON;
		playing = false;
		statusMode = TOGGLE;
		strokeMode = LIFE;
		executor = Executors.newFixedThreadPool(2);
		paintPanel = new PaintPanel(grid, cellSize, gap, aliveColor, deadColor, gapColor);
		currentMousePosition = new java.awt.Point(0, 0);
		changed = false;
		setTitle("The Game of Life ❚❚ Paused");
		
		addComponentListener(new ComponentAdapter()
		{
            public void componentResized(ComponentEvent e)
            {
            	resizedEvent(e);
            }
		});
		
		addComponentListener(new ComponentAdapter()
		{
            public void componentMoved(ComponentEvent e)
            {
            	movedEvent(e);
            }
		});
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(paintPanel, BorderLayout.CENTER);
		paintPanel.addMouseListener(this);
		paintPanel.addMouseMotionListener(this);
		paintPanel.addMouseWheelListener(this);
		this.addKeyListener(this);
		pack();
		
		Dimension d = paintPanel.getPreferredSize();
		paintPanel.shiftX = (paintPanel.getWidth() - d.width) / 2;
		paintPanel.shiftY = (paintPanel.getHeight() - d.height) / 2;
		if(paintPanel.shiftX < 0)
			paintPanel.shiftX = 0;
		if(paintPanel.shiftY < 0)
			paintPanel.shiftY = 0;
		
		setLocationRelativeTo(null);
		executor.execute(new RepaintRepeater(repaintRate));
		setVisible(true);
	}
	
	@SuppressWarnings("unused")
	public static void main(String args[])
	{
		System.setProperty("sun.java2d.opengl", "true");
		
		LoadFromSettings.loadSettings();
		int numRows = LoadFromSettings.numRows;
		int numCols = LoadFromSettings.numCols;
		int cellSize = LoadFromSettings.cellSize;
		int gap = LoadFromSettings.gap;
		long clockSpeed = LoadFromSettings.clockSpeed;
		long repaintRate = LoadFromSettings.repaintRate;
		Color aliveColor = LoadFromSettings.aliveColor;
		Color deadColor = LoadFromSettings.deadColor;
		Color gapColor = LoadFromSettings.gapColor;
		
		Grid grid = new Grid(numRows, numCols);		
		GUI gui = new GUI(grid, cellSize, gap, clockSpeed, aliveColor, deadColor, gapColor, repaintRate);
	}
	
	private class RepaintRepeater extends Thread
	{
		private long delay;
		private long start;
		
		public RepaintRepeater(long delay)
		{
			this.delay = delay;
			start = 0L;
		}
		
		public void run()
		{
			while(true)
			{
				if(changed || drawnLine)
				{
					changed &= false;
					start = System.currentTimeMillis();
					repaint();
					try
					{
						Thread.sleep(delay - (System.currentTimeMillis() - start));
					}
					catch(Exception ex)
					{
						
					}
				}
			}
		}
	}
	
	private class Play extends Thread
	{
		public Play(int priority)
		{
			this.setPriority(priority);
		}
		
		public void run()
		{
			setTitle("The Game of Life ▶ Running");
			playing = true;
			while(playing == true)
			{
				paintPanel.tick(clockSpeed);
				changed |= true;
			}
			setTitle("The Game of Life ❚❚ Paused");
		}
	}
	
	public void resizedEvent(ComponentEvent e)
	{
		paintPanel.resetFirstPaint();
	}
	
	public void movedEvent(ComponentEvent e)
	{
		paintPanel.resetFirstPaint();
	}
	
	private Point getPos(MouseEvent e)
	{
		return paintPanel.getPos(e.getPoint());
	}
	
	@Override
	public void mouseClicked(MouseEvent e)
	{
		button = e.getButton();
		if(playing == false && e.getButton() == MouseEvent.BUTTON1)
		{
			switch(statusMode)
			{
			case TOGGLE:
				paintPanel.toggle(getPos(e));
				break;
			case LIFE:
				paintPanel.set(getPos(e), true);
				break;
			case KILL:
				paintPanel.set(getPos(e), false);
				break;
			}
			changed |= true;
		}
	}
	
	java.awt.Point pressedPoint = null;

	@Override
	public void mousePressed(MouseEvent e)
	{
		button = e.getButton();
		pressedPoint = e.getPoint();
		prev = e.getPoint();
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		button = e.getButton();
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		
	}
	public boolean drawnLine;

	@Override
	public void mouseExited(MouseEvent e)
	{
		
	}
	
	private java.awt.Point prev;
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		if(playing == false && MouseEvent.BUTTON1 == button)
		{
			switch(strokeMode)
			{
			case LIFE:
				paintPanel.markStroke(e.getPoint(), Grid.ALIVE);
				break;
			case KILL:
				paintPanel.markStroke(e.getPoint(), Grid.DEAD);
				break;
			}
		}
		else if(MouseEvent.BUTTON3 == button)
		{
			paintPanel.shiftX += e.getX() - prev.x;
			paintPanel.shiftY += e.getY() - prev.y;
			prev = e.getPoint();
			changed |= true;
			paintPanel.resetFirstPaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		currentMousePosition = e.getPoint();
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			if(playing == false)
				executor.execute(new Play(Thread.MAX_PRIORITY));
			else
				playing = false;
		}
		else if(e.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			//cycles through toggle, life, and kill
			switch(statusMode)
			{
			case TOGGLE:
				statusMode = LIFE;
				break;
			case LIFE:
				statusMode = KILL;
				break;
			case KILL:
				statusMode = TOGGLE;
				break;
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_SHIFT)
		{
			strokeMode = KILL;
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_SHIFT)
		{
			strokeMode = LIFE;
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		changed |= paintPanel.zoom(e.getWheelRotation(), currentMousePosition);
		paintPanel.resetFirstPaint();
	}
}
