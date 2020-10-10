package org.fxbench.ui.docking.drag;

import org.fxbench.ui.docking.dock.Dock;
import org.fxbench.ui.docking.dockable.Dockable;

/**
 * This drag listener factory returns a {@link DefaultDragListener} as drag listener.
 * 
 * @author Heidi Rakels.
 */
public class DefaultDragListenerFactory implements DragListenerFactory
{
	
	// Implementations of DragListenerFactory.

	public DragListener createDragListener(Dock dock) {
		return new DefaultDragListener(dock);
	}

	public DragListener createDragListener(Dockable dockable) {
		return new DefaultDragListener(dockable);
	}
	
}
