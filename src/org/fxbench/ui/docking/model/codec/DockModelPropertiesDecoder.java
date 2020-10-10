package org.fxbench.ui.docking.model.codec;

import java.awt.Window;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.fxbench.ui.docking.DockingManager;
import org.fxbench.ui.docking.dock.CompositeDock;
import org.fxbench.ui.docking.dock.Dock;
import org.fxbench.ui.docking.model.DefaultDockingPathModel;
import org.fxbench.ui.docking.model.DockModel;
import org.fxbench.ui.docking.model.DockingPathModel;
import org.fxbench.ui.docking.util.DockingUtil;
import org.fxbench.ui.docking.util.PropertiesUtil;

/**
 * This class reads the <b>.dck</b> file generated by the {@link DockModelPropertiesEncoder}, into a java.util.Properties object.
 * After that, it decodes this properties object into a {@link org.fxbench.ui.docking.model.DockModel} and 
 * {@link org.fxbench.ui.docking.model.DockingPathModel}.
 * 
 * @author Heidi Rakels.
 */
public class DockModelPropertiesDecoder implements DockModelDecoder
{
	
	// Static fields.

	/** The current version of the file. */
	private static final String 	VERSION 			= "1.1";
	/** The name of the <code>version</code> property of the file. */
	private static final String 	PROPERTY_VERSION 			= "version";
	/** The name of the <code>dockModelClass</code> property of a dock model. */
	private static final String 	PROPERTY_CLASS 		= "dockModelClass";

	// Implementations of DockModelDecoder.

	public boolean canDecodeSource(String sourceName)
	{
		return sourceName.endsWith(DockModelPropertiesEncoder.EXTENSION);
	}
	
	public DockModel decode(String sourceName, Map dockablesMap, Map ownersMap, Map visualizersMap) throws IOException
	{
		// Load the properties.
		Properties properties = PropertiesUtil.loadProperties(sourceName);
		Map docks = new HashMap();

		return decodeProperties(properties, sourceName, dockablesMap, ownersMap, visualizersMap, docks);
	}
	
	// Protected methods.
	
	protected DockModel decodeProperties(Properties properties, String sourceName, Map dockablesMap, Map ownersMap, Map visualizersMap, Map docks) throws IOException
	{
		// Check he version.
		String version = null;
		version = PropertiesUtil.getString(properties, PROPERTY_VERSION, version);
		if (!VERSION.equals(version))
		{
			throw new IOException("Unsupported version [" + version + "].");
		}

		// Create the dock model.
		DockModel dockModel = createDockModel(properties);
		DockingManager.setDockModel(dockModel);

		// Load the properties in the model.
		dockModel.loadProperties(sourceName, "dockModel.", properties, dockablesMap, ownersMap, docks, visualizersMap);
		
		// Create the docking paths.
		DockingPathModel dockingPathModel = new DefaultDockingPathModel();
		dockingPathModel.loadProperties("dockingPathModel.", properties, docks);
		DockingManager.setDockingPathModel(dockingPathModel);

		// Remove the empty docks.
		for (int index = 0; index < dockModel.getOwnerCount(); index++)
		{
			Window owner = dockModel.getOwner(index);
			Iterator rootDockKeys = dockModel.getRootKeys(owner);
			while (rootDockKeys.hasNext())
			{
				String rootDockKey = (String)rootDockKeys.next();
				Dock rootDock = dockModel.getRootDock(rootDockKey);
				
				// Remove the empty children from the root dock.
				if (rootDock instanceof CompositeDock)
				{
					DockingUtil.removeEmptyChildren((CompositeDock)rootDock);
				}

			}
		}

		return dockModel;
	}

	// Private methods.

	private DockModel createDockModel(Properties properties) throws IOException
	{
		
		// Create the dock model object with the class name property.
		String className = null;
		className = PropertiesUtil.getString(properties, PROPERTY_CLASS, className);
		Class clazz = null;
		DockModel dockModel = null;
		try 
		{
			clazz = Class.forName(className);
		}
		catch (ClassNotFoundException classNotFoundException)
		{
			throw new IOException("Could not find class [" + className + "] (ClassNotFoundException).");
		}
		try
		{
			dockModel = (DockModel)clazz.newInstance();
		}
		catch (IllegalAccessException illegalAccessException)
		{
			throw new IOException("Illegal acces to class [" + className + "] (IllegalAccessException).");
		}
		catch (InstantiationException instantiationException)
		{
			throw new IOException("Could not instantiate class [" + className + "] (InstantiationException).");
		}
		catch (ClassCastException classCastException)
		{
			throw new IOException("Class [" + className + "] is not a Dock. (ClassCastException).");
		}

		return dockModel;
	}
	
}