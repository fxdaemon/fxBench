package org.fxbench.ui.docking.drag.painter;

import java.awt.Point;
import java.awt.Rectangle;

import org.fxbench.ui.docking.dock.Dock;
import org.fxbench.ui.docking.dockable.Dockable;


/**
 * <p>
 * This painter paints a representation for a {@link org.fxbench.ui.docking.dockable.Dockable} during dragging. 
 * </p>
 * <p>
 * Usually a rectangle is painted.
 * It shows where the dockable will be docked in a {@link org.fxbench.ui.docking.dock.Dock}, 
 * if the mouse would be released at the current mouse position.
 * </p>
 * 
 * @author Heidi Rakels.
 */
public interface DockableDragPainter
{
	
	// Interface methods.

	/**
	 * <p>
	 * Paints a dockable during dragging. 
	 * </p>
	 * <p>
	 * Usually a rectangle is painted.
	 * It shows where the dockable will be docked in the dock, 
	 * if the mouse would be released at the current mouse position.
	 * </p>
	 * 
	 * @param 	dockable 		The dockable that will be painted.
	 * @param 	dock 			The dock in which the dockable can be docked.
	 * @param 	rectangle		The rectangle that defines, where the given dockable
	 * 							will be docked, if the mouse is released at the current mouse position.
	 * 							The position of the rectangle is relative to the given dock.
	 * @param	mouseLocation	The position of the mouse relative to the given dock.
	 */
	public void paintDockableDrag(Dockable dockable, Dock dock, Rectangle rectangle, Point mouseLocation);
	
	/**
	 * Clears everything what was painted by this painter before.
	 */
	public void clear();
	
}
