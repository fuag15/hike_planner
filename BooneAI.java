
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Hashtable;



public class BooneAI implements AIModule
{
	public class node
	{
		double cost;
		Point point;
		node prev;
	}
	
	List<Point> path = new ArrayList<Point>();
	MinHeap openList = new MinHeap();
	PNode values[][];
	int visited[][]; // 0 no visit 1 = looked 2 = done
	PNode CurrentPNode;
	
	public List<Point> createPath(final TerrainMap map)
	{
		/*PNode temp;
		visited = new int[map.getWidth()][map.getHeight()];
		values = new PNode[map.getWidth()][map.getHeight()];
		
		for(int i = 0; i < values.length; i++)
		{
			for(int j = 0; j < values[i].length; j++)
			{
				values[i][j] = new PNode();
			}
		}
		//visitedH = new Hashtable(map.getWidth()*map.getHeight());
		
		Point CurrentPoint = map.getStartPoint();
		PNode CurrentPNode;
		values[CurrentPoint.x][CurrentPoint.y].setAll(new Point(-1, -1), CurrentPoint, 0, this.getHeuristic(map, CurrentPoint, map.getEndPoint()));
		openList.add(values[CurrentPoint.x][CurrentPoint.y]);
		//visitedH.put(CurrentPoint.hashCode(), CurrentPNode);
		//System.out.println(visitedH.containsKey(CurrentPoint.hashCode()));
		
		while(CurrentPoint.equals(map.getEndPoint()) == false)
		{
			CurrentPNode = (PNode)openList.remove();
			CurrentPoint = CurrentPNode.getPoint();
			Point[] neighbors;
			neighbors = map.getNeighbors(CurrentPoint);
			for(int i = 0; i < neighbors.length; i++)
			{
				if(visited[neighbors[i].x][neighbors[i].y] == 0)//we are at a brand new node!
				{
					//set value to next thing and rpev to this
					values[neighbors[i].x][neighbors[i].y].setAll(CurrentPoint, new Point(neighbors[i].x, neighbors[i].y), values[CurrentPoint.x][CurrentPoint.y].getCost() + map.getCost(CurrentPoint, neighbors[i]), this.getHeuristic(map, neighbors[i], map.getEndPoint()));
					visited[neighbors[i].x][neighbors[i].y] = 1;
				}
				else if(visited[neighbors[i].x][neighbors[i].y] == 1)//we have already peeked! but not finalized! if this value is les replace!
				{
					//if cost from here to there is smaller then the one already in it
					if((values[neighbors[i].x][neighbors[i].y].getCost()) > (values[CurrentPoint.x][CurrentPoint.y].getCost() + map.getCost(CurrentPoint, neighbors[i])))
					{
						//update value
						values[neighbors[i].x][neighbors[i].y].setAll(CurrentPoint, new Point(neighbors[i].x, neighbors[i].y), values[CurrentPoint.x][CurrentPoint.y].getCost() + map.getCost(CurrentPoint, neighbors[i]), this.getHeuristic(map, neighbors[i], map.getEndPoint()));
						//add to open list
						openList.add(values[neighbors[i].x][neighbors[i].y]);
					}
				}
				else //(visited[neighbors[i].x][neighbors[i].x] == 2) we are on a node tahts already tested man
				{
					///DO NOTHING
				}
			}
			
			visited[CurrentPoint.x][CurrentPoint.y] = 2;
		}
		
		//while wer not at begining
		while(CurrentPoint.x != -1)
		{
			path.add(CurrentPoint);
			CurrentPoint = values[CurrentPoint.x][CurrentPoint.y].getPrev();
		}
		Collections.reverse(path);
		//return the list
		*/
		// We're done!  Hand it back.
		final Point CurrentPoint = map.getStartPoint();
		return AStar(map, CurrentPoint);
		//return path;
	}
public List<Point> AStar(final TerrainMap map, final Point StartPoint)
       {
               node Current = new node();
               List<node> openList = new ArrayList<node>();                
               List<node> closedList = new ArrayList<node>();
               int[][] explored = new int[1000][1000];

               Current.point=StartPoint;
               Current.cost=0;
               Current.prev=null;
               
               while(false==Current.point.equals(map.getEndPoint()))
               {
                       Point[] neighbors;
                       neighbors=map.getNeighbors(Current.point);
                       for(int i=0;i< neighbors.length;i++)
                       {
                               node t=new node();
                               int p=0;
                               if(explored[neighbors[i].x][neighbors[i].y]==0)
                               {
                                       p=0;
                               }
                               else if(explored[neighbors[i].x][neighbors[i].y]==1)
                               {
                                               for (int j=0; j<openList.size(); j++) {
                                                       if (openList.get(j).point.equals(neighbors[i]))
                                                       {
                                                               p=1;
                                                               t.prev=Current;
                                                               t.cost=map.getCost(Current.point, neighbors[i]) + Current.cost + getHeuristic(map,neighbors[i],map.getEndPoint());

                                                               if (openList.get(j).cost>t.cost) {
                                                                       openList.get(j).cost=t.cost;
                                                                       openList.get(j).prev=t.prev;
                                                               }
                                                               break;
                                                       }
                                               }
                               }
                               else if(explored[neighbors[i].x][neighbors[i].y]==2)
                               {
                                       p=1;
                               }
                               
                               if(p==0)
                               {
                                       t.cost=map.getCost(Current.point, neighbors[i]) + Current.cost +getHeuristic(map,neighbors[i],map.getEndPoint());
                                       t.point=neighbors[i];
                                       t.prev=Current;
                                       openList.add(t);
                                       explored[t.point.x][t.point.y]=1;
                               }
                       
                       }
                       openList.remove(Current);
                       explored[Current.point.x][Current.point.y]=2;
                       Current = openList.get(0);
                       for (int k=0; k<openList.size(); k++) {
                               if (openList.get(k).cost<Current.cost) {
                                       Current=openList.get(k);
                               }
                       }
               }
               
               List<Point> path =new ArrayList<Point>();
               while(Current.prev !=null)
               {
                       path.add(Current.point);
                       Current=Current.prev;
               }
               path.add(Current.point);
               Collections.reverse(path);
               
               
               return path;
       }
 

	
	/*private double getHeuristic(final TerrainMap map, final Point pt1, final Point pt2) // stress test data structure
	{
		return 0;
	}*/
	
	/*private double getHeuristic(final TerrainMap map, final Point pt1, final Point pt2) //e^(f2-f1) THERE IS A BUG
	{
		double chebyshev = Math.max(Math.abs(pt1.x - pt2.x),Math.abs(pt1.y - pt2.y));
		double heightDifference = (double)map.getTile(pt1) - (double)map.getTile(pt2);
		double absoluteHeightDifference = Math.abs(heightDifference);
		double stepWidth;
		
		if(heightDifference == 0)
		{
			return chebyshev;
		}
		else if(heightDifference > 0)//e^(-1) is max p1 above p2
		{
			if(absoluteHeightDifference > chebyshev) // if the height is further off than the distance
			{
				return Math.exp(absoluteHeightDifference - chebyshev) + (Math.exp((double)-1.0) * (chebyshev - 1));
			}
			else if(absoluteHeightDifference < chebyshev) // if the height is less than the distance
			{
				return (Math.exp((double)-1.0) * absoluteHeightDifference) + (chebyshev - absoluteHeightDifference); 
			}
			else // height is the distance
			{
				return Math.exp((double)-1.0) * absoluteHeightDifference;
			}
		}
		else//(heightDifference < 0)//e^255 is max p1 below p2
		{
			if(absoluteHeightDifference > chebyshev)//if the height is further off than the distance
			{
				stepWidth = Math.floor(absoluteHeightDifference / chebyshev);
				return Math.exp(stepWidth) * chebyshev;
			}
			else if(absoluteHeightDifference < chebyshev) // if the height is less than the distance
			{
				return chebyshev;
			}
			else // height is the distance
			{
				return chebyshev;
			}
		}
	}*/
	
	private double getHeuristic(final TerrainMap map, final Point pt1, final Point pt2) //f2/(f1+1) ///BUG
	{
		double p1H = (double)map.getTile(pt1);
		double p2H = (double)map.getTile(pt2);
		double estCost = 1;
		//double chebyshev = Math.max(Math.abs(pt1.x - pt2.x),Math.abs(pt1.y - pt2.y));
		double heightDifference = p1H - p2H;
		double absoluteHeightDifference = Math.abs(heightDifference);
		//double chunkWidth;
		//double total = 0;
		//double tempH;

		if(heightDifference >= 0) // p1 is above p2
		{
			return 0;
		}
		else // p1 is below p2
		{
			return 1;
		}
	}
}