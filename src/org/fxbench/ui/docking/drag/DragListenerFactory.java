package org.fxbench.ui.docking.drag;

import org.fxbench.ui.docking.dock.Dock;
import org.fxbench.ui.docking.dockable.Dockable;

/**
 * This is an interface for factories that create {@link DragListener}s.
 * 
 * @author Heidi Rakels.
 */
public interface DragListenerFactory
{
	
	// The factory method.
	
	/**
	 * Creates a drag listener for the given dock.
	 * 
	 * @param	dock		The drag listener will listen to drag events on this dock.
	 * @return				The drag listener.
	 */
	public DragListener createDragListener(Dock dock);
	
	/**
	 * Creates a drag listener for the given dockable.
	 * 
	 * @param	dockable	The drag listener will listen to drag events of this dockable.
	 * @return				The drag listener.
	 */
	public DragListener createDragListener(Dockable dockable);
	
}
