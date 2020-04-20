import java.io.Serializable;

public class Point implements Cloneable, Serializable
{
	private static final long serialVersionUID = -1947168599465501309L;
	
	public int x;
	public int y;
	
	public Point(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Point clone()
	{
		return new Point(x, y);
	}
	
	public static double distance(Point a, Point b)
	{
		return new Vector(a, b).magnitude();
	}
	
	public Point rotateCCW(int radians, Point center)
	{
		Vector v;
		v = new Vector(this, center);
		v = v.rotateCCW(radians);
		return Point.add(this, v);
	}
	
	public Point rotateCW(int radians, Point center)
	{
		return rotateCCW(-radians, center);
	}
	
	public static Point mid(Point a, Point b)
	{
		return div(add(a, b), 2);
	}
	
	public static Point add(Point a, Point b)
	{
		int x = a.x + b.x;
		int y = a.y + b.y;
		return new Point(x, y);
	}
	
	public static Point add(Point p, Vector v)
	{
		int x = p.x + v.i;
		int y = p.y + v.j;
		return new Point(x, y);
	}
	
	public static Point add(Vector v, Point p)
	{
		return add(p, v);
	}
	
	public static Point add(Vector v, Vector u)
	{
		int x = v.i + u.i;
		int y = v.j + u.j;
		return new Point(x, y);
	}
	
	public static Point sub(Point p, Point b)
	{
		int x = p.x - b.x;
		int y = p.y - b.y;
		return new Point(x, y);
	}
	
	public static Point sub(Point p, Vector v)
	{
		int x = p.x - v.i;
		int y = p.y - v.j;
		return new Point(x, y);
	}
	
	public static Point sub(Vector v, Point p)
	{
		int x = v.i - p.x;
		int y = v.j - p.y;
		return new Point(x, y);
	}
	
	public static Point div(Point p, int denominator)
	{
		int x = p.x / denominator;
		int y = p.y / denominator;
		return new Point(x, y);
	}
	
	public String toString()
	{
		return "(" + x + ", " + y + ")";
	}
}