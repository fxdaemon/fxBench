package org.fxbench.ui.docking.util;

import java.util.List;

/**
 * This class contains a collection of static utility methods for java.util.Collection objects.
 * 
 * @author Heidi Rakels.
 */
public class CollectionUtil
{

	/**
	 * Determines if the given lists contain the same elements. We suppose that all the elements of the given lists 
	 * are different.
	 * 
	 * @param 	firstList 					The first list.
	 * @param 	secondList 					The second list.
	 * @return 								True if the given lists contain the same elements, false otherwise.
	 */
	public static boolean sameElements(List firstList, List secondList)
	{
		
		// The size hould be the same, otherwise stop.
		if (firstList.size() != secondList.size())
		{
			return false;
		}
		
		// Iterate over the elements of the first list.
		for (int index = 0; index < firstList.size(); index++)
		{
			// Check if the element is also in the second list.
			if (!secondList.contains(firstList.get(index)))
			{
				return false;
			}
		}
		
		// They heve the same elements.
		return true;
		
	}

	// Private constructor.
	
	private CollectionUtil()
	{
	}
}
