package org.fxbench.ui.docking.dock;

import org.fxbench.ui.docking.dockable.Dockable;

/**
 * This is an interface for a leaf dock that can hide an restore one of its dockables.
 * 
 * @author Heidi Rakels.
 */
public interface DockableHider extends LeafDock
{

	/**
	 * <p>
	 * Hides the content of the dockable. The dockable stays docked in this dock
	 * at the same position, but it has to be removed from the components of this dock. 
	 * </p>
	 * <p>
	 * The dock is not empty if it has a hidden dockable.
	 * </p>
	 * 
	 * @param 	dockable			The dockable that is docked in this dock, that has to be hidden.
	 * @throws 	IllegalArgumentException	If the given dockable is not docked in this dock.
	 * @throws	IllegalStateException		If there is already a hidden dockable.
	 */
	public void hideDockable(Dockable dockable);
	
	/**
	 * Gets the number of hidden dockables of this dock.
	 * 
	 * @return 					The number of hidden dockables of this dock.
	 */
	public int getHiddenDockableCount();

	/**
	 * Gets the dockable that is hidden in this dock with the given index.
	 * 
	 * @param	index				The index of the hidden dockable.
	 * @return						The hidden dockable, if there is one; otherwise null.
	 * @throws 	IndexOutOfBoundsException 	If the index is out of range (index < 0 || index >= getHiddenDockableCount()).
	 */
	public Dockable getHiddenDockable(int index);
	
	/**
	 * Restores the dockable that is hidden.
	 *
	 * @param	dockable			The dockable to restore
	 * @throws	IllegalStateException		If the dockable is not hidden.	
	 * @throws	IllegalArgumentException	If the dockable is not docked in this dock.	
	 */
	public void restoreDockable(Dockable dockable);
	
}
