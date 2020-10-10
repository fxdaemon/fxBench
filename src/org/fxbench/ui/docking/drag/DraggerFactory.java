package org.fxbench.ui.docking.drag;


import org.fxbench.ui.docking.dock.Dock;
import org.fxbench.ui.docking.dockable.Dockable;

/**
 * This is an interface for a factory that creates {@link org.fxbench.ui.docking.drag.Dragger}s
 * for the dockables of a given {@link org.fxbench.ui.docking.dock.Dock} or for a given {@link Dockable}.
 * 
 * @author Heidi Rakels.
 */
public interface DraggerFactory
{
	/**
	 * Creates a dragger for the dockables of the given dock.
	 * 
	 * @param 	dock 		The dock for which a dragger should be created.
	 * @return 				The created dragger.
	 */
	public Dragger createDragger(Dock dock);
	
	/**
	 * Creates a dragger for the given dockable.
	 * 
	 * @param 	dockable 	The dockable for which a dragger should be created.
	 * @return 				The created dragger.
	 */
	public Dragger createDragger(Dockable dockable);
}
