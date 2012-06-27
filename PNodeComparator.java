import java.awt.Point;
import java.util.Comparator;

public class PNode implements Comparable
{
	public int compare(Object node, Object anotherNode) 
	{
		double totalCost2 = ((PNode) anotherNode).getTotalCost();
		double totalCost1 = ((PNode) node).getTotalCost();
		
		
		return 1;
	}

}