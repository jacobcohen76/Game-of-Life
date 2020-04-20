import java.io.Serializable;

public class SaveableData implements Serializable
{
	private static final long serialVersionUID = 3034086311795590653L;
	
	public boolean[][] alive;
	public int numRows;
	public int numCols;
	public int rowPad;
	public int colPad;
}
