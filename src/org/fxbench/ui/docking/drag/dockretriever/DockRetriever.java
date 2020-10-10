package org.fxbench.ui.docking.drag.dockretriever;

import java.awt.Point;


import org.fxbench.ui.docking.dock.Dock;
import org.fxbench.ui.docking.dockable.Dockable;

/**
 * <p>
 * When a dockable is dragged, a dock retriever searches the {@link org.fxbench.ui.docking.dock.Dock} 
 * under the screen location of the mouse, to which the {@link org.fxbench.ui.docking.dockable.Dockable} 
 * should be added, if the mouse was released at that location. 
 * </p>
 * <p>
 * If there are multiple docks under the mouse location, to which the dockable 
 * can be added, the dock that has the highest priority, is retrieved.
 * </p>
 * 
 * @author Heidi Rakels.
 */
public interface DockRetriever
{

	// Interface methods.

	/**
	 * Retrieves the dock that has the highest priority for adding the dockable, 
	 * when the dockable is dragged over the given screen location.
	 * 
	 * @param 	screenLocation 		The location of the mouse on the screen.
	 * @param 	dockable 			The dockable that is dragged and has to be added to a new dock.
	 * @return 						The dock that has the highest priority for adding the dockable for the given screen location.
	 */
	public Dock[] retrieveHighestPriorityDock(Point screenLocation, Dockable dockable);

}
