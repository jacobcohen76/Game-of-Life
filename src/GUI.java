import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * In the context of an MVC design pattern the GUI Object
 * acts as the controller. Kind of counter-intuitive, but it is what it is.
 * 
 * @author Jacob Cohen
 */
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
	private JMenuBar mainMenu;
	private JMenu file;
	private JMenuItem saveAs;
	private JMenuItem load;
	private File mostRecentDirectory;
	private java.awt.Point currentMousePosition;
	private volatile boolean changed;
	
	public GUI(Grid grid, int cellSize, int gap, long clockSpeed, Color aliveColor, Color deadColor, Color gapColor, long repaintRate, List<? extends Image> icons)
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
		mainMenu = new JMenuBar();
		file = new JMenu("File");
		saveAs = new JMenuItem("Save As");
		saveAs.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				saveAsActionPerformed(e);
			}
		});
		file.add(saveAs);
		
		load = new JMenuItem("Load");		
		load.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				loadActionPerformed(e);
			}
		});
		file.add(load);
		mainMenu.add(file);
		setJMenuBar(mainMenu);
		mostRecentDirectory = null;
		
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
		setIconImages(icons);
		
		setVisible(true);
		Dimension d = paintPanel.getPreferredSize();
		paintPanel.shiftX = (getContentPane().getWidth() - d.width) / 2;
		paintPanel.shiftY = (getContentPane().getHeight() - d.height) / 2;
		executor.execute(new RepaintRepeater(repaintRate));
//		if(paintPanel.shiftX < 0)
//			paintPanel.shiftX = 0;
//		if(paintPanel.shiftY < 0)
//			paintPanel.shiftY = 0;
	}
	
	private void saveAsActionPerformed(ActionEvent e)
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(mostRecentDirectory);
    	chooser.setFileFilter(new FileNameExtensionFilter("Game of Life (*.gol)", new String[] { "gol" }));        	
		chooser.showOpenDialog(null);
		chooser.setFileHidingEnabled(true);
		File selected = chooser.getSelectedFile();
		if(selected != null)
		{
			String dir = selected.toString();
			int index = dir.lastIndexOf('.');
			if(index < 0 || dir.substring(index).toLowerCase().contentEquals(".gol") == false)
				dir += ".gol";
			else if(dir.substring(index).contentEquals(".gol") == false)
				dir = dir.substring(0, index) + dir.substring(index).toLowerCase();
			save(new File(dir));
		}
	}
	
	private void loadActionPerformed(ActionEvent e)
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(mostRecentDirectory);
    	chooser.setFileFilter(new FileNameExtensionFilter("Game of Life (*.gol)", new String[] { "gol" }));        	
		chooser.showOpenDialog(null);
		chooser.setFileHidingEnabled(true);
		File selected = chooser.getSelectedFile();
		if(selected != null)
		{
			load(selected);
		}
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
				if(changed)
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
			while(playing == true)
			{
				paintPanel.tick(clockSpeed);
				changed |= true;
			}
		}
	}
	
	public void save(File f)
	{
		if(playing == false)
		{
			try
			{
				SaveableData saveableData = paintPanel.getSaveableData();
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
				oos.writeObject(saveableData);
				oos.close();
			}
			catch(Exception ex)
			{
				
			}
		}
	}
	
	public void load(File f)
	{
		if(playing == false)
		{
			try
			{
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
				SaveableData saveableData = (SaveableData)ois.readObject();
				ois.close();
				paintPanel.load(saveableData);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	public void play()
	{
		playing = true;
		executor.execute(new Play(Thread.MAX_PRIORITY));
		setTitle("The Game of Life ▶ Running");
	}
	
	public void pause()
	{
		playing = false;
		setTitle("The Game of Life ❚❚ Paused");
	}
	
	public void togglePause()
	{
		if(playing)
			pause();
		else
			play();
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
			togglePause();
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
