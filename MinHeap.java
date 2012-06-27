import java.util.*;

/**
   This class implements a heap.
*/
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

	private static int getLeftChildIndex(int index)
	{
		return 2 * index;
	}

	private static int getRightChildIndex(int index)
	{
		return 2 * index + 1;
	}

	private static int getParentIndex(int index)
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
