package org.fxbench.ui.docking.dockable.action;

import javax.swing.JPopupMenu;

import org.fxbench.ui.docking.dockable.CompositeDockable;
import org.fxbench.ui.docking.dockable.Dockable;

/**
 * This is a factory that creates a popup menu for one dockable or a group of dockables.
 * 
 * @author Heidi Rakels.
 */
public interface PopupMenuFactory
{

	/**
	 * Creates a popup menu for the selected dockable and the other dockables in the dock of the selected dockable. 
	 * If the selected dockable is null,
	 * a popup menu for the composite dockable is created.
	 * 
	 * @param 	selectedDockable	The selected dockable.
	 * @param 	compositeDockable	The dockables in the selected dock.
	 * @return						A popup menu for the selected dockable and the other dockables in the dock.
	 */
	public JPopupMenu createPopupMenu(Dockable selectedDockable, CompositeDockable compositeDockable);
	
}
