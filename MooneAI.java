
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Hashtable;

public class MooneAI implements AIModule
{
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
	
	Stack<Point> pathStack = new Stack<Point>();
	List<Point> path = new ArrayList<Point>();
	//List<PNode> openList = new ArrayList<PNode>();
	MinHeap openList = new MinHeap();
	//TreeSet<Point> closedList = new TreeSet<Point>();
	//TreeSet<PNode> openList = new TreeSet<PNode>();
	List<Point> closedList = new ArrayList<Point>();
	Hashtable visitedH;
	PNode visited[][];
	PNode CurrentPNode;
	
	public List<Point> createPath(final TerrainMap map)
	{
		PNode temp;
		visited = new PNode[map.getWidth()][map.getHeight()];
		//visitedH = new Hashtable(map.getWidth()*map.getHeight());
		
		Point CurrentPoint = map.getStartPoint();
		CurrentPNode = new PNode(CurrentPoint, 0, this.getHeuristic(map, CurrentPoint, map.getEndPoint()));
		visited[CurrentPoint.x][CurrentPoint.y] = CurrentPNode;
		//visitedH.put(CurrentPoint.hashCode(), CurrentPNode);
		openList.add(CurrentPNode);
		//System.out.println(visitedH.containsKey(CurrentPoint.hashCode()));
		
		while(!(openList.size() == 0))
		{
			//get min from open list
			//CurrentPNode = openList.get(0);
			//openList.remove(0);
			CurrentPNode = (PNode)openList.remove();
			//System.out.println(openList.size());
			//openList.remove(CurrentPNode);
			CurrentPoint = CurrentPNode.getPoint();
			//if its value is in the double array with lest cost
			//if((!(closedList.contains(CurrentPoint))) && ((visited[CurrentPoint.x][CurrentPoint.y] == null) || (visited[CurrentPoint.x][CurrentPoint.y].compareTo(CurrentPNode) >= 0)))
			if((!(closedList.contains(CurrentPoint))) && ((visited[CurrentPoint.x][CurrentPoint.y] == null) || (visited[CurrentPoint.x][CurrentPoint.y].compareTo(CurrentPNode) >= 0)))
			{
				//////put its value into the double array
				visited[CurrentPoint.x][CurrentPoint.y] = CurrentPNode;
				//visitedH.remove(CurrentPoint.hashCode());
				//visitedH.put(CurrentPoint.hashCode(), CurrentPNode);
				//get neighbors
				Point neighbors[] = map.getNeighbors(CurrentPoint);
				////for each neighbor 0-8
				for(int i = 0; i < neighbors.length; i++)
				{
					temp = new PNode(neighbors[i], CurrentPNode.getCost() + map.getCost(CurrentPoint, neighbors[i]), this.getHeuristic(map, neighbors[i], map.getEndPoint()));
					temp.setPrev(CurrentPoint);
					////put them in open list
					openList.add(temp);
				}
				closedList.add(CurrentPoint);
				//Collections.sort(openList);
			}
			if(CurrentPoint.equals(map.getEndPoint()) == true)
			{
				break;
			}
		}
		
		//look at final point in visited and trace to start point;
		
		CurrentPoint = map.getEndPoint();
		CurrentPNode = visited[CurrentPoint.x][CurrentPoint.y];
		//CurrentPNode = (PNode)visitedH.get(CurrentPoint.hashCode());
		//put them into a pathStack stack
		do
		{
			pathStack.push(CurrentPNode.getPoint());
			//System.out.println(CurrentPNode.getPoint());
			CurrentPoint = CurrentPNode.getPrev();
			CurrentPNode = visited[CurrentPoint.x][CurrentPoint.y];
			//CurrentPNode = (PNode)visitedH.get(CurrentPoint.hashCode());
		}
		while(CurrentPoint.equals(map.getStartPoint()) == false);
		
		pathStack.push(map.getStartPoint());
		
		//pop the stack into a path list
		while(!(pathStack.empty()))
		{
			path.add(pathStack.pop());
		}
		//return the list
		
		// We're done!  Hand it back.
		return path;
	}
	
	/*private double getHeuristic(final TerrainMap map, final Point pt1, final Point pt2) // stress test data structure
	{
		return 0;
	}*/
	
	private double getHeuristic(final TerrainMap map, final Point pt1, final Point pt2) //e^(f2-f1) THERE IS A BUG
	{
		double chebyshev = Math.max(Math.abs(pt1.x - pt2.x),Math.abs(pt1.y - pt2.y));
		double p1H = (double)map.getTile(pt1);
		double p2H = (double)map.getTile(pt2);
		double heightDifference = p1H - p2H;
		double currentHeight;
		double absoluteHeightDifference = Math.abs(heightDifference);
		double stepWidth;
		double estCost;
		
		if(heightDifference == 0)
		{
			return chebyshev;
		}
		else if(heightDifference > 0)//e^(-1) is max p1 above p2
		{
			double averageMovement = (absoluteHeightDifference/chebyshev);
			currentHeight = p1H - averageMovement;
			estCost = Math.exp(-averageMovement);
			if(averageMovement < 1)
			{
				estCost = 0;
				for(int i = 0; i <= absoluteHeightDifference; i++)
				{
					estCost += Math.exp((double)-1.0);
				}
				estCost += (chebyshev - absoluteHeightDifference);
				return Math.floor(estCost);
			}
			
			while(currentHeight > p2H)
			{
				averageMovement = Math.ceil(averageMovement);
				estCost += Math.exp(-averageMovement);
				currentHeight = currentHeight - averageMovement;
			}
			
			return Math.floor(estCost);
		}
		else//(heightDifference < 0)//e^255 is max p1 below p2
		{
			double averageMovement = (absoluteHeightDifference/chebyshev);
			currentHeight = p1H - averageMovement;
			estCost = Math.exp(averageMovement);
			
			if(averageMovement <= 1)
			{
				estCost = 0;
				for(int i = 0; i < absoluteHeightDifference; i++)
				{
					estCost += Math.exp((double)1.0);
				}
				estCost += (chebyshev - absoluteHeightDifference);
				return Math.floor(estCost);
			}
			
			while(currentHeight < p2H)
			{
				averageMovement = Math.floor(averageMovement);
				estCost += Math.exp(averageMovement);
				currentHeight = currentHeight + averageMovement;
			}
			
			return Math.floor(estCost);
		}
	}
	
	/*private double getHeuristic(final TerrainMap map, final Point pt1, final Point pt2) //f2/(f1+1) ///BUG
	{
		double p1H = (double)map.getTile(pt1);
		double p2H = (double)map.getTile(pt2);
		double chebyshev = Math.max(Math.abs(pt1.x - pt2.x),Math.abs(pt1.y - pt2.y));
		double heightDifference = p1H - p2H;
		double absoluteHeightDifference = Math.abs(heightDifference);
		double chunkWidth;
		double total = 0;
		double tempH;

		if(heightDifference > 0) // p1 is above p2
		{
			return 0;
		}
		else // p1 is below p2
		{
			return 1;
		}
	}*/
	
	public class MinHeap
	{
		private ArrayList<Comparable> elements;
		public MinHeap()
		{
			elements = new ArrayList<Comparable>();
			elements.add(null); 
		}

		public void add(Comparable newElement)
		{
			// Add a new leaf
			elements.add(null);
			int index = elements.size() - 1;
			
			// Demote parents that are larger than the new element
			while (index > 1 && getParent(index).compareTo(newElement) > 0) 
			{
				elements.set(index, getParent(index));
				index = getParentIndex(index);
			}

			// Store the new element into the vacant slot
			elements.set(index, newElement);
		}

		public Comparable peek()
		{
			return elements.get(1);
		}

		public Comparable remove()
		{
			Comparable minimum = elements.get(1);      

			// Remove last element
			int lastIndex = elements.size() - 1;
			Comparable last = elements.remove(lastIndex);

			if (lastIndex > 1)
			{
				elements.set(1, last);
				fixHeap();     
			}

			return minimum;
		}

		private void fixHeap()
		{
			Comparable root = elements.get(1);

			int lastIndex = elements.size() - 1;
			// Promote children of removed root while they are larger than last      

			int index = 1;
			boolean more = true;
			while (more)
			{
				int childIndex = getLeftChildIndex(index);
				if (childIndex <= lastIndex)
				{
					// Get smaller child 

					// Get left child first
					Comparable child = getLeftChild(index);

					// Use right child instead if it is smaller
					if (getRightChildIndex(index) <= lastIndex && getRightChild(index).compareTo(child) < 0)
					{
						childIndex = getRightChildIndex(index);
						child = getRightChild(index);
					}

					// Check if larger child is smaller than root
					if (child.compareTo(root) < 0) 
					{
						// Promote child
						elements.set(index, child);
						index = childIndex;
					}
					else
					{
						// Root is smaller than both children
						more = false;
					}
				}
				else 
				{
					// No children
					more = false; 
				}
			}

			// Store root element in vacant slot
			elements.set(index, root);
		}

		public int size()
		{
			return elements.size() - 1;
		}

		private int getLeftChildIndex(int index)
		{
			return 2 * index;
		}

		private int getRightChildIndex(int index)
		{
			return 2 * index + 1;
		}

		private int getParentIndex(int index)
		{
			return index / 2;
		}

		private Comparable getLeftChild(int index)
		{
			return elements.get(2 * index);
		}

		private Comparable getRightChild(int index)
		{
			return elements.get(2 * index + 1);
		}

		private Comparable getParent(int index)
		{
			return elements.get(index / 2);
		}
	}
}