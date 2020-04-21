import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;

public class GUI extends JFrame implements MouseListener, MouseMotionListener, KeyListener, MouseWheelListener
{
	private static final long serialVersionUID = 6411499808530678723L;
	
	private long clockSpeed;
	private int button;
	private boolean playing;
	
	private ExecutorService executor;
	private PaintPanel paintPanel;
	
	public GUI(Grid grid, int cellSize, int gap, long clockSpeed, Color aliveColor, Color deadColor, Color gapColor)
	{
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.clockSpeed = clockSpeed;
		button = MouseEvent.NOBUTTON;
		playing = false;
		
		executor = Executors.newFixedThreadPool(1);
		paintPanel = new PaintPanel(grid, cellSize, gap, aliveColor, deadColor, gapColor);
		
		setTitle("The Game of Life");
		
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
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	@SuppressWarnings("unused")
	public static void main(String args[])
	{
		int numRows = 800;
		int numCols = 800;
		
		int cellSize = 1;
		int gap = 0;
		
		long clockSpeed = 100L;
		
		Color aliveColor = Color.WHITE;
		Color deadColor = Color.BLACK;
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
		if(e.getButton() == MouseEvent.BUTTON1)
			paintPanel.toggle(getPos(e));
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
	
	private java.awt.Point prev;

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if(MouseEvent.BUTTON1 == button)
			paintPanel.set(getPos(e), button == MouseEvent.BUTTON1);
		else if(MouseEvent.BUTTON3 == button)
		{
			paintPanel.shiftX += e.getX() - prev.x;
			paintPanel.shiftY += e.getY() - prev.y;
			prev = e.getPoint();
			repaint();
		}
	}
	
	private java.awt.Point currentMousePosition = new java.awt.Point(0, 0);

	@Override
	public void mouseMoved(MouseEvent e)
	{
		currentMousePosition = e.getPoint();
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		
	}
	
	boolean ctrl = false;
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_SPACE)
			if(playing == false)
				executor.execute(new Play(Thread.MAX_PRIORITY));
			else
				playing = false;
		if(e.getKeyCode() == KeyEvent.VK_S)
		{
			try {
				paintPanel.save(new File("E:\\grid"));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_L)
		{
			try {
				paintPanel.load(new File("E:\\grid"));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		paintPanel.zoom(e.getWheelRotation(), currentMousePosition);
	}
}
