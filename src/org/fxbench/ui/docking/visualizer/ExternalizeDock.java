package org.fxbench.ui.docking.visualizer;

import org.fxbench.ui.docking.dock.LeafDock;
import org.fxbench.ui.docking.dockable.Dockable;

/**
 * This is an interface for a leaf dock that can contain an externalized dockable.
 * 
 * @author Heidi Rakels.
 */
public interface ExternalizeDock extends LeafDock 
{

	/**
	 * Adds the dockable to this dock. The dockable is externalized.
	 * 
	 * @param 	dockableToExternalize		The externalized dockable that is added to this dock.
	 */
	public void externalizeDockable(Dockable dockableToExternalize);
	
	/**
	 * Determines if a dockable is externalized in this dock.
	 * 
	 * @return								True if the dockables in this dock are externalized.
	 */
	public boolean isExternalized();

	/**
	 * Sets the externalizer that contains this externalize dock.
	 * 
	 * @param externalizer					The externalizer of this externalize dock.
	 */
	public void setExternalizer(Externalizer externalizer);
	
	/**
	 * Gets the externalizer that contains this externalize dock.
	 * 
	 * @return								The externalizer of this externalize dock.
	 */
	public Externalizer getExternalizer();
	
}
