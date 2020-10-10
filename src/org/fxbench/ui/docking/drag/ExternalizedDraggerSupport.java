package org.fxbench.ui.docking.drag;

import java.awt.Point;

import org.fxbench.ui.docking.dock.LeafDock;
import org.fxbench.ui.docking.dockable.Dockable;
import org.fxbench.ui.docking.dockable.DockableState;
import org.fxbench.ui.docking.visualizer.ExternalizeDock;
import org.fxbench.ui.docking.visualizer.Externalizer;

class ExternalizedDraggerSupport
{

	/**
	 * Moves an externalized dockable to the given position with the given offset.
	 * 
	 * @param 	draggedDockable	The externalized dockable that has to be moved.
	 * @param 	location		The location for the externalized dockable.
	 * @param 	offset			The location offset.
	 */
	public static void moveExternalizedDockable(Dockable draggedDockable, Point location, Point offset)
	{
		
		// Check if the dockable is externalized.
		if (draggedDockable.getState() != DockableState.EXTERNALIZED)
		{
			throw new IllegalStateException("The dockable state has to be org.fxbench.ui.docking.dockable.DockableState.EXTERNALIZED");
		}
		LeafDock leafDock = draggedDockable.getDock();
		if (!(leafDock instanceof ExternalizeDock))
		{
			throw new IllegalStateException("The dock of the externalized dockable is a [" + leafDock.getClass() + "]. It should be a [org.fxbench.ui.docking.visualizer.Externalizer].");
		}
		ExternalizeDock childDock = (ExternalizeDock)leafDock;
		Externalizer externalizer = childDock.getExternalizer();
		externalizer.moveExternalizedDockable(draggedDockable, location, offset);
		
	}
}
