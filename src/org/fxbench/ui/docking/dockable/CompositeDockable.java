package org.fxbench.ui.docking.dockable;


/**  
 * A dockable that is composed of multiple, different dockables. It is possible to select one of the dockables.
 * 
 * @author Heidi Rakels.
 */
public interface CompositeDockable extends Dockable
{
	
	// Interface methods.

	/**
	 * Gets the number of child dockables of this composite dockable.
	 * 
	 * @return 		The number of child dockables of this composite dockable.
	 */
	public int getDockableCount();
	
	/**
	 * Gets the child dockable with the specified index.
	 * 
	 * @return 		The child dockable with the specified index.
	 * @throws 		IndexOutOfBoundsException 	If the index is out of range (index < 0 || index >= getDockableCount()).
	 */
	public Dockable getDockable(int index) throws IndexOutOfBoundsException;
	
	/**
	 * Gets the selected dockable. This dockable should be one of the child dockables. If no dockable is 
	 * selected, null is returned.
	 * 
	 * @return 		The selected dockable. Null if no dockable is selected.
	 */
	public Dockable getSelectedDockable();

}
