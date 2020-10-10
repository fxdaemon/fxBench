package org.fxbench.ui.docking.dock.factory;

import java.awt.Dimension;
import java.util.Properties;

import org.fxbench.ui.docking.dock.Dock;
import org.fxbench.ui.docking.dock.TabDock;
import org.fxbench.ui.docking.dockable.CompositeDockable;
import org.fxbench.ui.docking.dockable.Dockable;
import org.fxbench.ui.docking.dockable.DockingMode;
import org.fxbench.ui.docking.util.DockingUtil;
import org.fxbench.ui.docking.util.PropertiesUtil;

/**
 * <p>
 * This dock factory creates a {@link org.fxbench.ui.docking.dock.TabDock} if the given dockable
 * has {@link DockingMode#TAB} as one of its possible docking modes.
 * </p>
 * <p>
 * When the dockable cannot be added to a tab dock, the creation of the dock can be delegated
 * to an alternative dock factory. If this factory is null, then null will be returned.
 * </p>
 * 
 * @author Heidi Rakels
 */
public class TabDockFactory implements DockFactory {

	// Fields.

	/** When the dockable cannot be added to a tab dock, because it has not {@link DockingMode#TAB} as possible
	 * docking mode, the creation of the dock is delegated to this alternative dock factory.
	 * This factory can be null.  */
	private DockFactory 	alternativeDockFactory 	= new LeafDockFactory();
	
	// Constructors.

	/**
	 * Constructs a dock factory that creates {@link TabDock}s.
	 * If no tab dock can be created, a {@link LeafDockFactory} will be used to create the dock.
	 */
	public TabDockFactory()
	{
		this(new LeafDockFactory());
	}

	/**
	 * Constructs a dock factory that creates {@link TabDock}s.
	 * If no tab dock can be created, the given alternative factory will be used to create the dock.
	 * 
	 * @param alternativeDockFactory		If no tab dock can be created, this alternative factory will be used to create the dock.
	 */
	public TabDockFactory(DockFactory alternativeDockFactory)
	{
		this.alternativeDockFactory = alternativeDockFactory;
	}

	// Implementations of DockFactory.

	public Dock createDock(Dockable dockable, int dockingMode)
	{	
		
		// Get the allowed docking modes of the dockable.
		int dockingModes = dockable.getDockingModes();
		if ((dockingModes & DockingMode.TAB) != 0)
		{
			return createTabDock();
		}
		
		// Can we create a dock with the alternative dock factory?
		if (alternativeDockFactory != null)
		{
			return alternativeDockFactory.createDock(dockable, dockingMode);
		}
		
		return null;
		
	}
	
	public Dimension getDockPreferredSize(Dockable dockable, int dockingMode)
	{
		
		// Get the allowed docking modes of the dockable.
		int dockingModes = dockable.getDockingModes();
		if ((dockingModes & DockingMode.TAB) != 0)
		{
			if (dockable instanceof CompositeDockable)
			{
				return DockingUtil.getCompositeDockablePreferredSize((CompositeDockable)dockable, DockingMode.TAB);
			}
			return dockable.getContent().getPreferredSize();
		}
		
		// Can we create a dock with the alternative dock factory?
		if (alternativeDockFactory != null)
		{
			return alternativeDockFactory.getDockPreferredSize(dockable, dockingMode);
		}
		
		return new Dimension(0, 0);

		
	}

	public void saveProperties(String prefix, Properties properties)
	{

		if (alternativeDockFactory != null)
		{
			// Save the class of the alternative child dock factory and its properties.
			String alternativeDockFactoryClassName = alternativeDockFactory.getClass().getName();
			PropertiesUtil.setString(properties, prefix + "alternativeDockFactory", alternativeDockFactoryClassName);
			alternativeDockFactory.saveProperties(prefix + "alternativeDockFactory.", properties);
		}
		
	}
	

	public void loadProperties(String prefix, Properties properties)
	{
		
		// Load the class and properties of the alternative dock factory.
		try
		{
			String alternativeDockFactoryClassName = LeafDockFactory.class.getName();
			if (alternativeDockFactoryClassName != null)
			{
				alternativeDockFactoryClassName = PropertiesUtil.getString(properties, prefix + "alternativeDockFactory", alternativeDockFactoryClassName);
				Class alternativeDockFactoryClazz = Class.forName(alternativeDockFactoryClassName);
				alternativeDockFactory = (DockFactory)alternativeDockFactoryClazz.newInstance();
				alternativeDockFactory.loadProperties(prefix + "alternativeDockFactory.", properties);
			}
			else
			{
				alternativeDockFactory = null;
			}
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
	 * Gets the alternative dock factory. When the dockable cannot be added to a tab dock, 
	 * the creation of the dock is delegated to this alternative dock factory.
	 * 
	 * @return								The alternative dock factory. Can be null.
	 */
	public DockFactory getAlternativeDockFactory()
	{
		return alternativeDockFactory;
	}

	/**
	 * Sets the alternative dock factory. When the dockable cannot be added to a tab dock, 
	 * the creation of the dock is delegated to this alternative dock factory.
	 * 
	 * @param 	alternativeDockFactory		The alternative dock factory. Can be null.
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
	
	protected TabDock createTabDock() {
		return new TabDock();
	}
	
}
