package org.fxbench.ui.docking.dock.factory;

import java.awt.Dimension;
import java.util.Properties;

import org.fxbench.ui.docking.dock.CompositeTabDock;
import org.fxbench.ui.docking.dock.Dock;
import org.fxbench.ui.docking.dock.SplitDock;
import org.fxbench.ui.docking.dockable.CompositeDockable;
import org.fxbench.ui.docking.dockable.Dockable;
import org.fxbench.ui.docking.dockable.DockingMode;
import org.fxbench.ui.docking.util.DockingUtil;
import org.fxbench.ui.docking.util.PropertiesUtil;

/**
 * This dock factory tries to create a dock for a dockable with the child dock factory. 
 * When no child dock could be created and the dockable is a composite, a 
 * {@link org.fxbench.ui.docking.dock.CompositeTabDock} is created.
 * Only when the dockable cannot be added, it delegates the creation of the dock to the 
 * alternative dock factory.
 * 
 * @author Heidi Rakels
 */
public class CompositeTabDockFactory implements CompositeDockFactory 
{
	
	/** When the dockable cannot be added the creation of the dock is delegated
	 * to this alternative dock factory. */
	private DockFactory alternativeDockFactory 	= new LeafDockFactory();
	/** This is the factory that is used to create a dock for a single dockable. */
	private DockFactory childDockFactory 		= new SingleDockFactory(null);

	// Implementations of DockFactory.

	public Dock createDock(Dockable dockable, int dockingMode)
	{	
		
		// Try to create a dock with the child dock factory.
		Dock dock = childDockFactory.createDock(dockable, dockingMode);
		if (dock != null)
		{
			return dock;
		}
		
		// Check if the docking modes of the dockable contain TAB. 
		int dockPositions = dockable.getDockingModes();
		if ((dockPositions & DockingMode.TAB) != 0)
		{
			// Create the split dock.
			return new CompositeTabDock(this);
		}
		
		// This factory could not create a dock. Let the alternative factory try to create one.
		return alternativeDockFactory.createDock(dockable, dockingMode);
		
	}
	
	public Dimension getDockPreferredSize(Dockable dockable, int dockingMode)
	{
		
		// Try to create a dock with the child dock factory.
		Dock dock = childDockFactory.createDock(dockable, dockingMode);
		if (dock != null)
		{
			return childDockFactory.getDockPreferredSize(dockable, dockingMode);
		}

		// Check if the docking modes of the dockable contain TAB. 
		int dockPositions = dockable.getDockingModes();
		if ((dockPositions & DockingMode.TAB) != 0)
		{
			if (dockable instanceof CompositeDockable)
			{
				return DockingUtil.getCompositeDockablePreferredSize((CompositeDockable)dockable, DockingMode.TAB);
			}
			return dockable.getContent().getPreferredSize();
		}
		
		// This factory could not create a dock. Let the alternative factory try to create one.
		return alternativeDockFactory.getDockPreferredSize(dockable, dockingMode);
		
	}

	public void saveProperties(String prefix, Properties properties)
	{
		
		// Save the class of the leaf child dock factory and its properties.
		String leafChildDockFactoryClassName = childDockFactory.getClass().getName();
		PropertiesUtil.setString(properties, prefix + "childDockFactory", leafChildDockFactoryClassName);
		childDockFactory.saveProperties(prefix + "childDockFactory.", properties);

		// Save the class of the alternative child dock factory and its properties.
		String alternativeDockFactoryClassName = alternativeDockFactory.getClass().getName();
		PropertiesUtil.setString(properties, prefix + "alternativeDockFactory", alternativeDockFactoryClassName);
		alternativeDockFactory.saveProperties(prefix + "alternativeDockFactory.", properties);

	}
	
	public void loadProperties(String prefix, Properties properties)
	{
		
		// Load the class and properties of the leaf child dock factory.
		try
		{
			String leafChildDockFactoryClassName = LeafDockFactory.class.getName();
			leafChildDockFactoryClassName = PropertiesUtil.getString(properties, prefix + "childDockFactory", leafChildDockFactoryClassName);
			Class leafChildDockFactoryClazz = Class.forName(leafChildDockFactoryClassName);
			childDockFactory = (DockFactory)leafChildDockFactoryClazz.newInstance();
			childDockFactory.loadProperties(prefix + "childDockFactory.", properties);
		}
		catch (ClassNotFoundException exception)
		{
			System.out.println("Could not create the child dock factory.");
			exception.printStackTrace();
			childDockFactory = new TabDockFactory();
		}
		catch (IllegalAccessException exception)
		{
			System.out.println("Could not create the child dock factory.");
			exception.printStackTrace();
			childDockFactory = new TabDockFactory();
		}
		catch (InstantiationException exception)
		{
			System.out.println("Could not create the child dock factory.");
			exception.printStackTrace();
			childDockFactory = new TabDockFactory();
		}

		// Load the class and properties of the alternative dock factory.
		try
		{
			String alternativeDockFactoryClassName = LeafDockFactory.class.getName();
			alternativeDockFactoryClassName = PropertiesUtil.getString(properties, prefix + "alternativeDockFactory", alternativeDockFactoryClassName);
			Class alternativeDockFactoryClazz = Class.forName(alternativeDockFactoryClassName);
			alternativeDockFactory = (DockFactory)alternativeDockFactoryClazz.newInstance();
			alternativeDockFactory.loadProperties(prefix + "alternativeDockFactory.", properties);
		}
		catch (ClassNotFoundException exception)
		{
			System.out.println("Could not create the alternative dock factory.");
			exception.printStackTrace();
			alternativeDockFactory = new LeafDockFactory();
		}
		catch (IllegalAccessException exception)
		{
			System.out.println("Could not create the alternative dock factory.");
			exception.printStackTrace();
			alternativeDockFactory = new LeafDockFactory();
		}
		catch (InstantiationException exception)
		{
			System.out.println("Could not create the alternative dock factory.");
			exception.printStackTrace();
			alternativeDockFactory = new LeafDockFactory();
		}

	}
	
	// Getters / Setters.

	/**
	 * Gets the alternative dock factory. When the dockable cannot be added to a split dock, 
	 * the creation of the dock is delegated to this alternative dock factory.
	 * 
	 * @return								The alternative dock factory.
	 */
	public DockFactory getAlternativeDockFactory()
	{
		return alternativeDockFactory;
	}

	/**
	 * Sets the alternative dock factory. When the dockable cannot be added to a split dock, 
	 * the creation of the dock is delegated to this alternative dock factory.
	 * 
	 * @param 	alternativeDockFactory		The alternative dock factory. Should not be null.
	 * @throws 	IllegalArgumentException    When the alternative dock factory is null.
	 */
	public void setAlternativeDockFactory(DockFactory alternativeDockFactory)
	{
		
		if (alternativeDockFactory == null)
		{
			throw new IllegalArgumentException("The alternative dock factory cannot be null.");
		}
		
		this.alternativeDockFactory = alternativeDockFactory;
		
	}

	/**
	 * Gets the leaf child dock factory. This is the factory that is used in the constructor of the {@link SplitDock#SplitDock(DockFactory, CompositeDockFactory)}
	 * for creating the leaf docks of the split dock.
	 * 
	 * @return								The leaf child dock factory.
	 */
	public DockFactory getChildDockFactory()
	{
		return childDockFactory;
	}

	/**
	 * Sets the leaf child dock factory. This is the factory that is used in the constructor of the {@link SplitDock#SplitDock(DockFactory, CompositeDockFactory)}
	 * for creating the leaf docks of the split dock.
	 * 
	 * @param 	leafChildDockFactory		The leaf child dock factory. Should not be null.
	 * @throws 	IllegalArgumentException    When the alternative dock factory is null.
	 */
	public void setChildDockFactory(DockFactory leafChildDockFactory)
	{
		
		if (leafChildDockFactory == null)
		{
			throw new IllegalArgumentException("The leaf dock factory cannot be null.");
		}
		
		this.childDockFactory = leafChildDockFactory;
		
	}
}
