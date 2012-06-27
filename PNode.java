import java.awt.Point;

public class PNode implements Comparable
{
	Point prev;
	Point point;
	double cost;
	double heuristic;
	double totalCost;
	boolean visited;
	boolean perminant;
	
	public PNode()
	{
		visited = false;
		perminant = false;
	}
	
	public PNode(Point inPoint, double inCost, double inHeuristic)
	{
		point = inPoint;
		cost = inCost;
		heuristic = inHeuristic;
		totalCost = inCost + inHeuristic;
		visited = true;
	}
	
	public void setAll(Point inPrev, Point inPoint, double inCost, double inHeuristic)
	{
		prev = inPrev;
		point = inPoint;
		cost = inCost;
		heuristic = inHeuristic;
		totalCost = inCost + inHeuristic;
		visited = true;
	}
	
	public boolean isPerminant()
	{
		return perminant;
	}
	
	public void setPerminant()
	{
		perminant = true;
	}
	
	public boolean isVisited()
	{
		return visited;
	}
	
	public void setVisisted()
	{
		visited = true;
	}
	
	public Point getPoint()
	{
		return point;
	}
	
	public Point getPrev()
	{
		return prev;
	}
	
	public double getCost()
	{
		return cost;
	}
	
	public double getHeuristic()
	{
		return heuristic;
	}

	public double getTotalCost()
	{
		return totalCost;
	}

	public void setPrev(Point inPrev)
	{
		prev = inPrev;
	}
	
	public void setCost(double inCost)
	{
		cost = inCost;
		totalCost = cost + heuristic;
	}
	
	public void setHeuristic(double inHeuristic)
	{
		heuristic = inHeuristic;
		totalCost = cost + heuristic;
	}
	
	public void setTotalCost(double inTotalCost)
	{
		totalCost = inTotalCost;
	}
	
	public int compareTo(Object anotherPNode) throws ClassCastException
	{
		if(!(anotherPNode instanceof PNode))
		{
			throw new ClassCastException("A PNode object expected.");
		}
		
		double anotherPNodeTotalCost = ((PNode)anotherPNode).getTotalCost();
		double result = this.totalCost - anotherPNodeTotalCost;
		
		if(result < 0)
		{
			return -1;
		}
		else if(result > 0)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
	
	public boolean equals(Object other) 
	{
		if (this == other) return true;
		if ( !(other instanceof PNode) ) return false;

		final PNode node = (PNode) other;

		if ( !(node.getPoint().equals( this.getPoint() )) ) return false;
		if ( !(node.getTotalCost() == this.getTotalCost() ) ) return false;

		return true;
	}
	
	public int getHashKey()
	{
		int lowX = point.x & 0xFFFF;
		int lowY = point.y & 0xFFFF;
		int hash = (lowX << 16) | lowY;
		return hash;
	}

	public int hashCode() 
	{
		int result;
		result = getPoint().hashCode();
		result = 29 * result + (int)this.getTotalCost();
		return result;
	}
}