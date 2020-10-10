package org.fxbench.ui.docking.dock.factory;

/**
 * This dock factory creates composite docks. When a composite dock is created, this composite dock needs a child dock
 * factory. This interface provides the methods to get and set the child dock factory for the created composite docks. 
 * 
 * @author Heidi Rakels.
 */
public interface CompositeDockFactory extends DockFactory
{

	/**
	 * Gets the child dock factory. This is the factory that creates the child docks for the composite docks 
	 * that this factory creates ({@link org.fxbench.ui.docking.dock.CompositeDock#setChildDockFactory(DockFactory)}).
	 * 
	 * @return								The child dock factory for the created composite docks.
	 */
	public DockFactory getChildDockFactory();

	/**
	 * Sets the child dock factory. This is the factory that creates the child docks for the composite docks that 
	 * this factory creates ({@link org.fxbench.ui.docking.dock.CompositeDock#setChildDockFactory(DockFactory)}).
	 * 
	 * @param 	childDockFactory			The child dock factory for the created composite docks. Should not be null.
	 * @throws 	IllegalArgumentException    When the dock factory is null.
	 */
	public void setChildDockFactory(DockFactory childDockFactory);
	
}
