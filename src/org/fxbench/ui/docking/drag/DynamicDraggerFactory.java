package org.fxbench.ui.docking.drag;


import org.fxbench.ui.docking.dock.Dock;
import org.fxbench.ui.docking.dock.LeafDock;
import org.fxbench.ui.docking.dock.TabDock;
import org.fxbench.ui.docking.dockable.Dockable;

/**
 * <p>
 * This dragger factory creates draggers that remove and add the dragged {@link org.fxbench.ui.docking.dockable.Dockable}s 
 * to new {@link org.fxbench.ui.docking.dock.Dock}s dynamically during dragging.
 * </p>
 * 
 * @author Heidi Rakels.
 */
public class DynamicDraggerFactory implements DraggerFactory
{

	// Implementations of DockableDraggerFactory.

	/**
	 * It creates for a {@link org.fxbench.ui.docking.dock.TabDock} a {@link DynamicTabDragger}.
	 * For other types of docks a {@link DynamicDragger} is returned.
	 */
	public Dragger createDragger(Dock dock)
	{
		if (dock instanceof TabDock)
		{
			return new DynamicTabDragger();
		}
		if (dock instanceof LeafDock)
		{
			return new DynamicDragger();
		}
		return null;
	}

	/**
	 * It creates a {@link DynamicDragger}.
	 */
	public Dragger createDragger(Dockable dockable)
	{
		return new DynamicDockableDragger(dockable);
	}

}
