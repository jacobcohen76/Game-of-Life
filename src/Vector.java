
public class Vector implements Cloneable
{
	public int i;
	public int j;
	
	private Vector()
	{
		this.i = 0;
		this.j = 0;
	}
	
	public Vector(int i, int j)
	{
		this.i = i;
		this.j = j;
	}
	
	public Vector clone()
	{
		return new Vector(i, j);
	}
	
	public Vector normalize()
	{
		return Vector.div(this, (int) Math.round(magnitude()));
	}
	
	public Vector(Point a, Point b)
	{
		this.i = b.x - a.x;
		this.j = b.y - a.y;
	}
	
	public double magnitude()
	{
		return Math.sqrt(Vector.dot(this, this));
	}
	
	public boolean equals(Object o)
	{
		if(o == this)
			return true;
		else if(!(o instanceof Vector))
			return false;
		
		Vector v = (Vector) o;
		
		return Double.compare(this.i, v.i) == 0 && Double.compare(this.j, v.j) == 0; 
	}
	
	public Vector rotateCCW(int radians)
	{
		double sin = Math.sin(radians);
		double cos = Math.cos(radians);
		Vector transposed = new Vector();
		transposed.i = (int) Math.round(i * cos - j * sin);
		transposed.j = (int) Math.round(i * sin + j * cos);
		return transposed;
	}
	
	public static Vector getNormalOf(Vector v)
	{
		return new Vector(-v.j, v.i);
	}
	
	public Vector rotateCW(int radians)
	{
		return rotateCCW(-radians);
	}
	
	public static int dot(Vector a, Vector b)
	{
		return ((a.i * b.i) + (a.j * b.j));
	}
	
	public static int cross(Vector a, Vector b)
	{
		return ((a.i * b.j) - (a.j * b.i));
	}
	
	public static boolean isOrthogonal(Vector a, Vector b)
	{
		return Vector.dot(a, b) == 0;
	}
	
	public static double getAngleBetween(Vector u, Vector v)
	{
		return Math.atan2(u.i, u.j) - Math.atan2(v.i, v.j);
	}
	
	public static Vector proj(Vector v, Vector a)
	{
		int coefficient;
		coefficient = Vector.dot(a, v);
		coefficient /= Vector.dot(v, v);
		return mult(coefficient, v);
	}
	
	public static Vector add(Vector a, Vector b)
	{
		int i = a.i + b.i;
		int j = a.j + b.j;
		return new Vector(i, j);
	}
	
	public static Vector add(Vector v, Point p)
	{
		int i = v.i + p.x;
		int j = v.j + p.y;
		return new Vector(i, j);
	}
	
	public static Vector add(Point p, Vector v)
	{
		return add(v, p);
	}
	
	public static Vector sub(Vector a, Vector b)
	{
		int i = a.i - b.i;
		int j = a.j - b.j;
		return new Vector(i, j);
	}
	
	public static Vector sub(Vector v, Point p)
	{
		int i = v.i - p.x;
		int j = v.j - p.y;
		return new Vector(i, j);
	}
	
	public static Vector sub(Point p, Vector v)
	{
		int i = p.x - v.i;
		int j = p.y - v.j;
		return new Vector(i, j);
	}
	
	public static Vector sub(Point a, Point b)
	{
		int i = a.x - b.x;
		int j = a.y - b.y;
		return new Vector(i, j);
	}
	
	public static Vector mult(int coefficient, Vector v)
	{
		int i = coefficient * v.i;
		int j = coefficient * v.j;
		return new Vector(i, j);
	}
	
	public static Vector mult(Vector v, int coefficient)
	{
		return mult(coefficient, v);
	}
	
	public static Vector div(Vector v, int denominator)
	{
		int i = v.i / denominator;
		int j = v.j / denominator;
		return new Vector(i, j);
	}
	
	public String toString()
	{
		return "<" + i + ", " + j + ">";
	}
	
	public static final Vector EAST = new Vector(+1, 0);
	public static final Vector WEST = new Vector(-1, 0);
	public static final Vector NORTH = new Vector(0, +1);
	public static final Vector SOUTH = new Vector(0, -1);
	
	public static final Vector NORTHEAST = Vector.add(NORTH, EAST);
	public static final Vector NORTHWEST = Vector.add(NORTH, WEST);
	public static final Vector SOUTHWEST = Vector.add(SOUTH, WEST);
	public static final Vector SOUTHEAST = Vector.add(SOUTH, EAST);
}
