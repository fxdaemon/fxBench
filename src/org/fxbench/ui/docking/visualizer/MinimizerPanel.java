package org.fxbench.ui.docking.visualizer;

import java.awt.Component;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.fxbench.ui.docking.DockingManager;
import org.fxbench.ui.docking.component.SelectableDockableHeader;
import org.fxbench.ui.docking.dock.Position;
import org.fxbench.ui.docking.dockable.Dockable;
import org.fxbench.ui.docking.dockable.DockableState;
import org.fxbench.ui.docking.util.PropertiesUtil;

/**
 * A visualizer that shows minimized dockables in a line in a panel.
 * 
 * @author Heidi Rakels.
 */
public class MinimizerPanel extends JPanel implements Visualizer
{

	// Static fields.

	/** The value for the orientation when the minimized dockables are placed in a horizontal line. */
	public static final int		ORIENTATION_HORIZONTAL			= 0;
	/** The value for the orientation when the minimized dockables are placed in a vertical line. */
	public static final int		ORIENTATION_VERTICAL			= 1;

	/** The name of the <code>dockableIds</code> property. */
	private static final String PROPERTY_DOCKABLE_IDS 					= "dockableIds";
	/** The name of the <code>selectedDockableIds</code> property. */
	private static final String PROPERTY_SELECTED_DOCKABLE_IDS 			= "selectedDockableIds";
	

	// Fields.

	/** The position that has to be used for creating the minimize headers
	 * with the method {@link org.fxbench.ui.docking.component.SwComponentFactory#createMinimizeHeader(Dockable, int)}.
	 * Possible values are constants defined by the class {@link org.fxbench.ui.docking.dock.Position}. */
	private int			headerPosition			= Position.TOP;
	/** The orientation of the panel. */
	private int 		orientation				= ORIENTATION_VERTICAL;			
	/** The minimized dockables of this panel. */
	private List 		minimizedDockables 		= new ArrayList();
	/** Mapping between the minimized dockables and their minimizd components. */
	private Map			minimizedHeaders		= new HashMap();
	/** Listens to selection changes of the headers. */
	private SelectionChangeListener	selectionChangeListener	= new SelectionChangeListener();

	// Constructors.

	/**
	 * Constructs a visualizer that shows minimized dockables in a panel.
	 */
	public MinimizerPanel()
	{
		
		this(ORIENTATION_VERTICAL);
		
	}

	/**
	 * Constructs a visualizer that shows minimized dockables in a panel.
	 * 
	 * @param	orientation		The normal content of the panel.
	 */
	public MinimizerPanel(int orientation)
	{
		
		this.orientation = orientation;
		rebuildUI();
		
	}

	// Implementations of Visualizer.

	public int getState()
	{
		return DockableState.MINIMIZED;
	}

	public boolean canVisualizeDockable(Dockable dockableToVisualize)
	{
		
		// Check the dockable is not null.
		if (dockableToVisualize == null)
		{
			throw new NullPointerException("Dockable to minimize null.");
		}
		
		// Is the dockable already minimized in this panel?
		if (minimizedDockables.contains(dockableToVisualize))
		{
			return false;
		}

		return true;
		
	}
	
	public void visualizeDockable(Dockable dockableToVisualize)
	{
		
		// Check the dockable is not null.
		if (dockableToVisualize == null)
		{
			throw new NullPointerException("Dockable to minimize null.");
		}
		
		// Is the dockable already minimized in this panel?
		if (minimizedDockables.contains(dockableToVisualize))
		{
			return;
		}
		
		// Set the dockable minimized.
		dockableToVisualize.setState(DockableState.MINIMIZED, this);

		// Visualize.
		minimizedDockables.add(dockableToVisualize);
		SelectableDockableHeader dockableHeader = (SelectableDockableHeader)DockingManager.getComponentFactory().createMinimizeHeader(dockableToVisualize, headerPosition);
		dockableHeader.addPropertyChangeListener(selectionChangeListener);
		minimizedHeaders.put(dockableToVisualize, dockableHeader);
		removeAll();
		for (int index = 0; index < minimizedDockables.size(); index++)
		{
			add((Component)minimizedHeaders.get(minimizedDockables.get(index)));
		}
		revalidate();
		repaint();
		
	}
	
	public int getVisualizedDockableCount()
	{
		return minimizedDockables.size();
	}
	
	public Dockable getVisualizedDockable(int index) throws IndexOutOfBoundsException
	{
		
		// Check if the index is in the bounds.
		if ((index < 0) || (index >= getVisualizedDockableCount()))
		{
			throw new IndexOutOfBoundsException("Index " + index);
		}

		return (Dockable)minimizedDockables.get(index);
		
	}

	public void removeVisualizedDockable(Dockable dockableToRemove)
	{
		
		// Check if the dockable is minimized in this minimizer.
		if (!minimizedDockables.contains(dockableToRemove))
		{
			throw new IllegalArgumentException("The dockable is not minimized in this minimizer.");
		}
		
		SelectableDockableHeader minimizedComponent = (SelectableDockableHeader)minimizedHeaders.get(dockableToRemove);
		minimizedComponent.removePropertyChangeListener(selectionChangeListener);
		remove((Component)minimizedComponent);
		minimizedHeaders.remove(dockableToRemove);
		minimizedDockables.remove(dockableToRemove);
		revalidate();
		repaint();

	}

	/**
	 * Loads the properties of this maximizer. The dockables that were maximized,
	 * when the model was saved, are not maximized again.
	 */
	public void loadProperties(String prefix, Properties properties, Map dockablesMap, Window owner) throws IOException
	{
		
		// Get the orientation.
		int orientation = ORIENTATION_HORIZONTAL;
		orientation = PropertiesUtil.getInteger(properties, prefix + "orientation", orientation);
		setOrientation(orientation);

		// Get the position of the header.
		int headerPosition = Position.TOP;
		headerPosition = PropertiesUtil.getInteger(properties, prefix + "headerPosition", headerPosition);
		setHeaderPosition(headerPosition);

		// Load the IDs of the dockables.
		String[] dockableIdArray = new String[0];
		dockableIdArray = PropertiesUtil.getStringArray(properties, prefix + PROPERTY_DOCKABLE_IDS, dockableIdArray);
		
		// Iterate over the IDs of the dockables.
		for (int index = 0; index < dockableIdArray.length; index++)
		{
			// Try to get the dockable.
			Object dockableObject = dockablesMap.get(dockableIdArray[index]);
			if (dockableObject != null)
			{
				if (dockableObject instanceof Dockable)
				{
					Dockable dockable = (Dockable)dockableObject;
					
					// Try to add the dockable.
					visualizeDockable(dockable);
					dockable.setState(DockableState.MINIMIZED, this);
				}
				else
				{
					throw new IOException("The values in the dockables mapping should be of type org.fxbench.ui.docking.Dockable.");
				}
			}
		}
		
		// Load the IDs of the selected dockables.
		String[] selectedDockableIdArray = new String[0];
		selectedDockableIdArray = PropertiesUtil.getStringArray(properties, prefix + PROPERTY_SELECTED_DOCKABLE_IDS, selectedDockableIdArray);

		// Deselect all the dockables.
		deselectAllMinimizedHeaders(null);

		// Iterate over the IDs of the selected dockables.
		for (int index = 0; index < selectedDockableIdArray.length; index++)
		{
			// Try to get the dockable.
			Object dockableObject = dockablesMap.get(selectedDockableIdArray[index]);
			if (dockableObject != null)
			{
				if (dockableObject instanceof Dockable)
				{
					// Select the dockable.
					Dockable dockable = (Dockable)dockableObject;
					SelectableDockableHeader dockableHeader = (SelectableDockableHeader)minimizedHeaders.get(dockable);
					selectMinimizedHeader(dockableHeader, true);
				}
			}
		}

	}

	/**
	 * Saves the properties of this maximizer. The dockables that are maximized,
	 * are not saved.
	 */
	public void saveProperties(String prefix, Properties properties)
	{
		
		// Save the position of the header.
		PropertiesUtil.setInteger(properties, prefix + "headerPosition", headerPosition);
		
		// Save the orientation.
		PropertiesUtil.setInteger(properties, prefix + "orientation", orientation);

		// Save the IDs of the dockables and the IDs of the selected dockables.
		String[] dockableIdArray = new String[getVisualizedDockableCount()];
		List selectedDockableIDs = new ArrayList(1);
		for (int index = 0; index < dockableIdArray.length; index++)
		{
			// Get the ID of the dockable.
			Dockable minimizedDockable = getVisualizedDockable(index);
			dockableIdArray[index] = minimizedDockable.getID();
			SelectableDockableHeader dockableHeader = (SelectableDockableHeader)minimizedHeaders.get(minimizedDockable);
			if (dockableHeader.isSelected())
			{
				selectedDockableIDs.add(minimizedDockable.getID());
			}
		}
		PropertiesUtil.setStringArray(properties, prefix + PROPERTY_DOCKABLE_IDS, dockableIdArray);
		String[] selectedDockableIdArray = new String[selectedDockableIDs.size()];
		selectedDockableIdArray = (String[])selectedDockableIDs.toArray(selectedDockableIdArray);
		PropertiesUtil.setStringArray(properties, prefix + PROPERTY_SELECTED_DOCKABLE_IDS, selectedDockableIdArray);


	}

	// Getters / Setters.

	/**
	 * <p>
	 * Gets the position that has to be used for creating the minimize headers
	 * with the method {@link org.fxbench.ui.docking.component.SwComponentFactory#createMinimizeHeader(Dockable, int)}.
	 * </p>
	 * <p>
	 * The default value is {@link org.fxbench.ui.docking.dock.Position#TOP}.
	 * </p>
	 * 
	 * @return						The position that has to be used for creating the minimize headers
	 * 								with the method {@link org.fxbench.ui.docking.component.SwComponentFactory#createMinimizeHeader(Dockable, int)}.
	 * 								Possible values are constants defined by the class {@link org.fxbench.ui.docking.dock.Position}, i.e.:
	 * 								<ul>
	 * 								<li>{@link org.fxbench.ui.docking.dock.Position#LEFT},</li> 
	 * 								<li>{@link org.fxbench.ui.docking.dock.Position#RIGHT},</li> 
	 * 								<li>{@link org.fxbench.ui.docking.dock.Position#TOP},</li> 
	 * 								<li>{@link org.fxbench.ui.docking.dock.Position#BOTTOM}.</li> 
	 * 								</ul>
	 */
	public int getHeaderPosition()
	{
		return headerPosition;
	}

	/**
	 * Sets the position that has to be used for creating the minimize headers
	 * with the method {@link org.fxbench.ui.docking.component.SwComponentFactory#createMinimizeHeader(Dockable, int)}.
	 * 
	 * @param 	newHeaderPosition	The position that has to be used for creating the minimize headers
	 * 								with the method {@link org.fxbench.ui.docking.component.SwComponentFactory#createMinimizeHeader(Dockable, int)}.
	 * 								Possible values are constants defined by the class {@link org.fxbench.ui.docking.dock.Position}, i.e.:
	 * 								<ul>
	 * 								<li>{@link org.fxbench.ui.docking.dock.Position#LEFT},</li> 
	 * 								<li>{@link org.fxbench.ui.docking.dock.Position#RIGHT},</li> 
	 * 								<li>{@link org.fxbench.ui.docking.dock.Position#TOP},</li> 
	 * 								<li>{@link org.fxbench.ui.docking.dock.Position#BOTTOM}.</li> 
	 * 								</ul>
	 */
	public void setHeaderPosition(int newHeaderPosition)
	{

		// Do we have a new value?
		if (newHeaderPosition != headerPosition)
		{
			// Set the new headerPosition.
			this.headerPosition = newHeaderPosition;
			
			// Rebuild the UI.
			rebuildUI();
		}		

	}
	
	/**
	 * <p>
	 * Gets the orientation of the minimizer panel. 
	 * </p>
	 * <p>
	 * This can be ORIENTATION_HORIZONTAL or ORIENTATION_VERTICAL. 
	 * The default is ORIENTATION_HORIZONTAL.
	 * </p>
	 * 
	 * @return						The orientation of the minimizer panel.
	 */
	public int getOrientation()
	{
		return orientation;
	}

	/**
	 * <p>
	 * Sets the orientation of the minimizer panel. 
	 * </p>
	 * <p>
	 * This can be ORIENTATION_HORIZONTAL or ORIENTATION_VERTICAL. 
	 * </p>
	 * 
	 * @param newOrientation		The orientation of the minimizer dock.
	 */
	public void setOrientation(int newOrientation)
	{
		
		// Do we have a new value?
		if (newOrientation != orientation)
		{
			// Set the new orientation.
			this.orientation = newOrientation;
			
			// Rebuild the UI.
			rebuildUI();
		}		

	}

	// Private metods.

	/**
	 * Removes everything from the panel.
	 * The layout is set again. 
	 * All the headers are created again.
	 * All the headers are added again.
	 */
	private void rebuildUI()
	{
		
		// Remove everything.
		this.removeAll();
		
		// Set the layout.
		int layout = BoxLayout.Y_AXIS;
		if (orientation == ORIENTATION_HORIZONTAL)
		{
			layout = BoxLayout.X_AXIS;
		}
		setLayout(new BoxLayout(this, layout));
		
		// Create all the headers again.
		for (int index = 0; index < minimizedDockables.size();index++)
		{
			// Get the header.
			Dockable dockable = (Dockable)minimizedDockables.get(index);
			SelectableDockableHeader oldMinimizeHeader = (SelectableDockableHeader)minimizedHeaders.get(dockable);
			oldMinimizeHeader.removePropertyChangeListener(selectionChangeListener);
			boolean selected = oldMinimizeHeader.isSelected();
			
			// Create the header again.
			SelectableDockableHeader minimizeHeader = (SelectableDockableHeader)DockingManager.getComponentFactory().createMinimizeHeader(dockable, headerPosition);
			minimizeHeader.setSelected(selected);
			minimizeHeader.addPropertyChangeListener(selectionChangeListener);
			minimizedHeaders.put(dockable, minimizeHeader);

			// Add the header.
			add((Component)minimizeHeader);
		}
	}
	
	/**
	 * Deselects all the headers, except the given object.
	 * 
	 * @param	notTodeselectObject		This object should not be deselected. Can be null.
	 */
	private void deselectAllMinimizedHeaders(Object notTodeselectObject)
	{
		
		// Iterate over all the headers.
		Iterator headerIterator = minimizedHeaders.values().iterator();
		while (headerIterator.hasNext())
		{
			SelectableDockableHeader selectableDockableHeader = (SelectableDockableHeader)headerIterator.next();
			if (!selectableDockableHeader.equals(notTodeselectObject))
			{
				selectableDockableHeader.removePropertyChangeListener(selectionChangeListener);
				selectableDockableHeader.setSelected(false);
				selectableDockableHeader.addPropertyChangeListener(selectionChangeListener);
			}
		}
	}
	
	/**
	 * Selects the header.
	 * 
	 * @param	objectToSelect			This object should be selected. Not null.
	 * @param	selected				True when the header has to be selectd, false otherwise.
	 */
	private void selectMinimizedHeader(Object objectToSelect, boolean selected)
	{
		
		// Iterate over all the headers.
		Iterator headerIterator = minimizedHeaders.values().iterator();
		while (headerIterator.hasNext())
		{
			SelectableDockableHeader selectableDockableHeader = (SelectableDockableHeader)headerIterator.next();
			if (selectableDockableHeader.equals(objectToSelect))
			{
				selectableDockableHeader.removePropertyChangeListener(selectionChangeListener);
				selectableDockableHeader.setSelected(selected);
				selectableDockableHeader.addPropertyChangeListener(selectionChangeListener);
			}
		}
	}
	
	// Private classes.

	private class SelectionChangeListener implements PropertyChangeListener
	{
		
		// Implementations of PropertyChangeListener.

		public void propertyChange(PropertyChangeEvent propertyChangeEvent)
		{
			
			if (propertyChangeEvent.getPropertyName().equals("selected"))
			{
				Object newValue = propertyChangeEvent.getNewValue();
				if (newValue instanceof Boolean)
				{
					boolean newSelected = ((Boolean)newValue).booleanValue();
					Object source = propertyChangeEvent.getSource();
					if (newSelected)
					{
						// Deselect all the headers a select the selected.
						deselectAllMinimizedHeaders(source);
					}
					else
					{
						// Deselect the header.
						selectMinimizedHeader(source, false);
					}
				}
			}
			
		}
		
	}
	
}
