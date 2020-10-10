package org.fxbench.ui.docking.drag;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.fxbench.ui.docking.DockingManager;
import org.fxbench.ui.docking.dock.CompositeTabDock;
import org.fxbench.ui.docking.dock.Dock;
import org.fxbench.ui.docking.dock.FloatDock;
import org.fxbench.ui.docking.dock.TabDock;
import org.fxbench.ui.docking.dockable.CompositeDockable;
import org.fxbench.ui.docking.dockable.DefaultCompositeDockable;
import org.fxbench.ui.docking.dockable.Dockable;
import org.fxbench.ui.docking.dockable.DockingMode;
import org.fxbench.ui.docking.drag.dockretriever.DockRetriever;
import org.fxbench.ui.docking.drag.dockretriever.StaticDockRetriever;
import org.fxbench.ui.docking.drag.painter.DefaultRectanglePainter;
import org.fxbench.ui.docking.drag.painter.DockableDragPainter;
import org.fxbench.ui.docking.drag.painter.SwDockableDragPainter;
import org.fxbench.ui.docking.util.CollectionUtil;
import org.fxbench.ui.docking.util.DockingUtil;

/**
 * <p>
 * This is a class for dragging dockables in a {@link org.fxbench.ui.docking.dock.CompositeTabDock}.
 * The dockables of one child dock can be dragged by dragging the tab. All the dockables of all the child docks
 * can be dragged by dragging another part of the tabbed pane. A {@link org.fxbench.ui.docking.dockable.CompositeDockable}
 * is created with the dockables of the javax.swing.JTabbedPane.
 * </p>
 * <p>
 * The {@link org.fxbench.ui.docking.dock.Dock}s that are used in the application should inherit 
 * from the java.awt.Component class.
 * </p>
 * <p>
 * With this class a tab of the javax.swing.JTabbedPane can also be dragged. This happens, when one tab is dragged,
 * and the mouse is over the header of the JTabbedPane.
 * </p>
 * 
 * @author Heidi Rakels.
 */
public class StaticCompositeTabDragger implements Dragger
{

	// Fields.

	// For docking.
	/** The dockRetriever. */
	private DockRetriever			dockRetriever					= new StaticDockRetriever();
	/** The dock of the dockable before dragging. */
	private CompositeTabDock 		originDock;
	/** The root dock of the dock, where the dragged dockable is currently docked. */
	private Dock					currentRootDock;
	/** The child of the root dock of the dock, where the dragged dockable is currently docked. */
	private Dock					currentChildOfRootDock;
	/** The current location of the mouse in screen coordinates. */
	private Point 					screenLocation					= new Point();		
	/** This is the current location of the mouse in the dock where the dockable will be docked for the current mouse location. 
	 * We keep it as field because we don't want to create every time a new point.*/
	private Point					locationInDestinationDock		= new Point();	
	/** This is the current location of the mouse in the tabbed pane. 
	 * We keep it as field because we don't want to create every time a new point.*/
	private Point 					currentOffsetInTabbedPane		= new Point();	
	/** The offset of the clicked point. */
	private Point 					dockableOffset					= new Point();		
	/** The dockable that is dragged. */
	private Dockable 				draggedDockable;			
	
	// Tabs.
	/** The tabbed pane that contained the dragged dockable. */
	private JTabbedPane 			sourceTabbedPane;
	/** The index of the tab that contains the dragged dockable, when the dragged dockable is not 
	 * a composite dockable. */
	private int 					oldTabIndex;
	
	// Cursors.
	/** Manages the cursors used for dragging dockables. */
	private DragCursorManager 		cursorManager 					= new DragCursorManager();
		
	// For painting.
	/** Paints the rectangle where the dockable will be docked for the current mouse location. */
	private DockableDragPainter 	dockableDragPainter;
	/** The rectangle where the dockable will be docked for the current mouse location. */ 
	private Rectangle 				dockableDragRectangle 			= new Rectangle();

	// Constructors.

	/**
	 * Constructs a dragger with a default painter for painting the dragged dockables: 
	 * a {@link SwDockableDragPainter} with a {@link DefaultRectanglePainter}.
	 */
	public StaticCompositeTabDragger()
	{
		this(new SwDockableDragPainter(new DefaultRectanglePainter()));
	}
	
	/**
	 * Constructs a dragger with the given painter for painting the dragged dockables.
	 * 
	 * @param	dockableDragPainter 		The painter for painting the dragged dockables.
	 */
	public StaticCompositeTabDragger(DockableDragPainter dockableDragPainter)
	{
		this.dockableDragPainter = dockableDragPainter;
	}

	// Implementations of Dragger.
	
	public boolean startDragging(MouseEvent mouseEvent) 
	{
		
		// Get the mouse position and the component of the mouse event. 
		Component mouseComponent = (Component)mouseEvent.getSource();
		int x = mouseEvent.getX();
		int y = mouseEvent.getY();

		// Reset the fields.
		reset();

		// Initialize the fields for docking.

		// Is the mouse component a JTabbedPane?
		if (mouseComponent instanceof JTabbedPane)
		{
			// Is the ancestor component a CompositeTabDock?
			Component ancestorComponent = (Component) SwingUtilities.getAncestorOfClass(Component.class, mouseComponent);
			if (ancestorComponent instanceof CompositeTabDock)
			{
				// Does the dock has child docks docked in it?
				CompositeTabDock tabDock = (CompositeTabDock) ancestorComponent;
				if (tabDock.getChildDockCount() > 0)
				{
					// We can start dragging.
					sourceTabbedPane = (JTabbedPane)mouseComponent;
					originDock = tabDock;
					
					// Calculate the dockable offset.
					dockableOffset.setLocation(x, y);
					dockableOffset = SwingUtilities.convertPoint(mouseComponent, dockableOffset, sourceTabbedPane);
	
					// Get the selected tab and its dockable.
					oldTabIndex = sourceTabbedPane.indexAtLocation(dockableOffset.x, dockableOffset.y);
					if (oldTabIndex >= 0)
					{
						// One tab is selected. The dockable that is docked in the tab will be dragged.
						Dock childDock = (Dock)sourceTabbedPane.getComponentAt(oldTabIndex);
						draggedDockable = DockingUtil.createDockable(childDock);
//						if (draggedDockable instanceof CompositeDockable)
//						{
//							draggedDockable.setState(DockableState.NORMAL, childDock);
//						}
						if (draggedDockable != null)
						{
							// Make sure the offset is not larger than the dockable size.
							Dimension size = DockingUtil.getDockablePreferredSize(draggedDockable, DockingMode.TAB);
							if (size != null)
							{
								if (dockableOffset.x > size.getWidth())
								{
									dockableOffset.x = (int)(Math.round(size.getWidth()));
								}
								if (dockableOffset.y > size.getHeight())
								{
									dockableOffset.y = (int)(Math.round(size.getHeight()));
								}
							}

							// We have a dockable to drag.
							return true;
						}
					}
					else
					{
						// No tab is selected.
		
						// Are there any tabs?
						if (sourceTabbedPane.getTabCount() <= 0)
						{
							return false;
						}
						
						// Check if the component of the selected dockable is not clicked.
						Component selectedComponent = sourceTabbedPane.getSelectedComponent();
						Point helpPoint = new Point(mouseEvent.getX(), mouseEvent.getY());
						helpPoint = SwingUtilities.convertPoint(mouseComponent, helpPoint, selectedComponent);
						if (selectedComponent.contains(helpPoint))
						{
							return false;
						}
						
						// A composite dockable is created with the dockables that are docked in the different tab.
						// This composite dockable is dragged.
						// Create the composite dockable.
						draggedDockable = DockingUtil.createDockable(originDock);
//						if (draggedDockable instanceof CompositeDockable)
//						{
//							draggedDockable.setState(DockableState.NORMAL, originDock);
//						}
						
						// Make sure the offset is not larger than the dockable size.
						Dimension size = DockingUtil.getCompositeDockablePreferredSize((CompositeDockable)draggedDockable, DockingMode.TAB);
						if (dockableOffset.x > size.getWidth())
						{
							dockableOffset.x = (int)(Math.round(size.getWidth()));
						}
						if (dockableOffset.y > size.getHeight())
						{
							dockableOffset.y = (int)(Math.round(size.getHeight()));
						}

						// We have a dockable to drag.
						return true;
					}
				}
			}
		}
		
		// We can not drag.
		return false;
		
	}
	
	/**
	 * Searches the dock where the dockable will be docked for the current mouse location.
	 * A rectangle is painted that shows, where the dockable will be docked.
	 * The cursor shows, if we can dock, or if we cannot dock for the current location.
	 */
	public void drag(MouseEvent mouseEvent) 
	{	
		
		// Get the mouse position and the component of the mouse event. 
		Component mouseComponent = (Component)mouseEvent.getSource();

		// Are we dragging a tab?
		if (tabDragged(mouseEvent) >= 0)
		{
			// Clear previous paintings.
			clearPainting();
			
			// Set the cursor.
			cursorManager.setCursor(mouseComponent, retrieveCanDockCursor());

			return;
		}

		// Get the mouse location in screen coordinates.
		computeScreenLocation(mouseEvent);

		// Get the destination dock for this position for the dockable that we are dragging.
		Dock[] destinationDocks = dockRetriever.retrieveHighestPriorityDock(screenLocation, draggedDockable);
		if (destinationDocks == null)
		{
			// We have no destination dock any more. 
			dockableDragPainter.paintDockableDrag(draggedDockable, null, null, screenLocation);

			// Set the 'cannot dock' cursor.
			cursorManager.setCursor(mouseComponent, retrieveCanNotDockCursor());

			return;
		}
		Dock destinationDock = destinationDocks[0];

		// Do we have a destination dock?
		if (destinationDock != null)
		{
			if ((!destinationDock.equals(originDock)) || (!isFloating()))
			{
				// Does the destination dock inherit from java.awt.Component or is it the float dock?
				if (destinationDock instanceof Component)
				{
					// Get the docking rectangle from the destination dock.
					locationInDestinationDock.setLocation(screenLocation.x, screenLocation.y);
					SwingUtilities.convertPointFromScreen(locationInDestinationDock, (Component)destinationDock);
					destinationDock.retrieveDockingRectangle(draggedDockable, locationInDestinationDock,dockableOffset, dockableDragRectangle);
	
					// Paint the new rectangle.
					dockableDragPainter.paintDockableDrag(draggedDockable, destinationDock, dockableDragRectangle, locationInDestinationDock);
						
					// Set the 'can dock' cursor.
					cursorManager.setCursor((Component)destinationDock, retrieveCanDockCursor());

					
				}
				else if (destinationDock instanceof FloatDock)
				{
					
					// Are we in the special situation that we will move a child dock of the float dock?
					boolean move = false;
					
					// Get the root dock and the dock under the root.
					Dock rootDock = originDock;
					Dock dockUnderRoot = null;
					while (rootDock.getParentDock() != null)
					{
						dockUnderRoot = rootDock;
						rootDock = rootDock.getParentDock();
					}
					
					// Is the root dock the float dock?
					if (rootDock instanceof FloatDock)
					{
						// Is the dockable already in this dock and are there no others?
						List childrenOfDockable = new ArrayList();
						List childrenOfDock = new ArrayList();
						DockingUtil.retrieveDockables(draggedDockable, childrenOfDockable);
						DockingUtil.retrieveDockables(dockUnderRoot, childrenOfDock);
						if (sameElements(childrenOfDockable, childrenOfDock))
						{
							move = true;
						}
					}
	
//					// We cannot paint on the screen, but maybe we can paint in the pane of the origin dock.
//					if (move)
//					{
//						locationInDestinationDock.setLocation(screenLocation.x - dockableOffset.x, screenLocation.y - dockableOffset.y);
//						SwingUtilities.convertPointFromScreen(locationInDestinationDock, (Component) originDock);
//						dockableDragRectangle.setLocation(locationInDestinationDock);
//						Window window = SwingUtilities.getWindowAncestor((Component) dockUnderRoot);
//						dockableDragRectangle.setSize(window.getSize());
//						locationInDestinationDock.setLocation(locationInDestinationDock.x + dockableOffset.x, locationInDestinationDock.y + dockableOffset.y);
//					}
//					else
//					{
//						// Get the docking rectangle for the destination float dock.
//						destinationDock.retrieveDockingRectangle(draggedDockable, screenLocation, dockableOffset, dockableDragRectangle);
//	
//						// Convert this rectangle to the origindock.
//						locationInDestinationDock.setLocation(dockableDragRectangle.x, dockableDragRectangle.y);
//						SwingUtilities.convertPointFromScreen(locationInDestinationDock, (Component) originDock);
//						dockableDragRectangle.setLocation(locationInDestinationDock);
//						locationInDestinationDock.setLocation(locationInDestinationDock.x + dockableOffset.x, locationInDestinationDock.y + dockableOffset.y);
//					}
//					
//					// Paint the new rectangle.
//					dockableDragPainter.paintDockableDrag(draggedDockable, originDock, dockableDragRectangle, locationInDestinationDock);
	
					// We cannot paint on the screen, but maybe we can paint in the owner of the screen dock.
					if (move)
					{
						locationInDestinationDock.setLocation(screenLocation.x - dockableOffset.x, screenLocation.y - dockableOffset.y);
						//SwingUtilities.convertPointFromScreen(locationInDestinationDock, (Component) originDock);
						dockableDragRectangle.setLocation(locationInDestinationDock);
						Window window = SwingUtilities.getWindowAncestor((Component) dockUnderRoot);
						dockableDragRectangle.setSize(window.getSize());
						//locationInDestinationDock.setLocation(locationInDestinationDock.x + dockableOffset.x, locationInDestinationDock.y + dockableOffset.y);
						locationInDestinationDock.setLocation(locationInDestinationDock.x + dockableOffset.x, locationInDestinationDock.y + dockableOffset.y);
					}
					else
					{
						// Get the docking rectangle for the destination float dock.
						destinationDock.retrieveDockingRectangle(draggedDockable, screenLocation, dockableOffset, dockableDragRectangle);
	
						// Convert this rectangle to the origindock.
						locationInDestinationDock.setLocation(dockableDragRectangle.x, dockableDragRectangle.y);
						//SwingUtilities.convertPointFromScreen(locationInDestinationDock, (Component) originDock);
						dockableDragRectangle.setLocation(locationInDestinationDock);
						locationInDestinationDock.setLocation(locationInDestinationDock.x + dockableOffset.x, locationInDestinationDock.y + dockableOffset.y);
					}
					
					// Paint the new rectangle.
					dockableDragPainter.paintDockableDrag(draggedDockable, destinationDock, dockableDragRectangle, locationInDestinationDock);

					
					// Set the 'can dock' cursor.
					cursorManager.setCursor(mouseComponent, retrieveCanDockCursor());
				}
				else
				{
					// Currently this should not happen. All docks, except the float dock inherit from java.awt.Component.
					// We have a dock where we cannot paint. 
					dockableDragPainter.paintDockableDrag(draggedDockable, destinationDock, null, screenLocation);
					
					// Set the 'can dock' cursor.
					cursorManager.setCursor(mouseComponent, retrieveCanDockCursor());
				}
			}
			else
			{
				// The destination dock is the current dock and we are floating.
				destinationDock = currentRootDock;
				locationInDestinationDock.setLocation(screenLocation.x - dockableOffset.x, screenLocation.y - dockableOffset.y);
				SwingUtilities.convertPointFromScreen(locationInDestinationDock, (Component) originDock);
				dockableDragRectangle.setLocation(locationInDestinationDock);
				Window window = SwingUtilities.getWindowAncestor((Component) currentChildOfRootDock);
				dockableDragRectangle.setSize(window.getSize());
				
				// Paint the new rectangle.
				dockableDragPainter.paintDockableDrag(draggedDockable, originDock, dockableDragRectangle, locationInDestinationDock);
			
				// Set the 'can dock' cursor.
				cursorManager.setCursor(mouseComponent, retrieveCanDockCursor());

			}
		}
		else
		{
			// We have no destination dock any more. 
			dockableDragPainter.paintDockableDrag(draggedDockable, null, null, screenLocation);

			// Set the 'cannot dock' cursor.
			cursorManager.setCursor(mouseComponent, retrieveCanNotDockCursor());
		}
		
	}
	
	/**
	 * Resets the cursor and other fields. Cleans up what was painted before.
	 * Leaves the dragged dockable in its origin dock.
	 */
	public void cancelDragging(MouseEvent mouseEvent) 
	{
		
		// Reset the old cursor.
		cursorManager.resetCursor();

		// Clean up what was painted for dragging.
		clearPainting();
		
		// No dragging anymore.
		reset();

	}

	/**
	 * Resets the cursor. Cleans up what was painted before.
	 * Searches a destination dock for the last mouse location and tries to dock the dragged dockable in
	 * this dock. 
	 */
	public void stopDragging(MouseEvent mouseEvent)
	{

		// Reset the old cursor.
		cursorManager.resetCursor();
		
		// Clear what we painted.
		clearPainting();

		// Are we dragging a tab?
		int tabIndex = tabDragged(mouseEvent);
		if (tabIndex >= 0)
		{

			// Move the tabs.
			if ((oldTabIndex >= 0) && (tabIndex != oldTabIndex))
			{
				Point locationInOriginDock = new Point(mouseEvent.getPoint().x, mouseEvent.getPoint().y);
				locationInOriginDock = SwingUtilities.convertPoint((Component)mouseEvent.getSource(), locationInOriginDock, (Component)originDock);				
				DockingManager.getDockingExecutor().changeDocking(draggedDockable, originDock, locationInOriginDock, new Point(0, 0));
			}

			// No dragging anymore.
			reset();

			return;

		}

		// Get the mouse location in screen coordinates.
		computeScreenLocation(mouseEvent);

		// Get the destination dock.
		Dock[] destinationDocks = dockRetriever.retrieveHighestPriorityDock(screenLocation, draggedDockable);
		if (destinationDocks == null)
		{
			return;
		}
		Dock destinationDock = destinationDocks[0];

		// Is the destination dock different from the origin?
		if (destinationDock != null)
		{
			if (!destinationDock.equals(originDock))
			{
				// Get the mouse location for the new dock.
				locationInDestinationDock.setLocation(screenLocation.x, screenLocation.y);
				if (destinationDock instanceof Component)
				{
					SwingUtilities.convertPointFromScreen(locationInDestinationDock, (Component)destinationDock);
				}
		
				// Check if we can move the dock of the dockable in the float dock.
				if (destinationDock instanceof FloatDock)
				{
					// Get the root dock and the dock under the root.
					Dock rootDock = originDock;
					Dock dockUnderRoot = null;
					while (rootDock.getParentDock() != null)
					{
						dockUnderRoot = rootDock;
						rootDock = rootDock.getParentDock();
					}
					
					// Is the root dock the float dock?
					if (rootDock instanceof FloatDock)
					{
						// Is the dockable already in this dock and are there no others?
						List childrenOfDockable = new ArrayList();
						List childrenOfDock = new ArrayList();
						DockingUtil.retrieveDockables(draggedDockable, childrenOfDockable);
						DockingUtil.retrieveDockables(dockUnderRoot, childrenOfDock);
						if (sameElements(childrenOfDockable, childrenOfDock))
						{
							((FloatDock)rootDock).moveDock(dockUnderRoot, locationInDestinationDock, dockableOffset);
							return;
						}
					}
				}
				
				// Remove the dockable from the old dock, add to the new dock.
				// Use the docking manager for the addition and removal, because the listeners have to informed.
				DockingManager.getDockingExecutor().changeDocking(draggedDockable, destinationDock, locationInDestinationDock, dockableOffset);
		
				// Clean the dock from which the dockable is removed.
				DockingManager.getDockingExecutor().cleanDock(originDock, false);

			}
			else
			{
				// Is the dockable floating?
				if (isFloating())
				{
					destinationDock = currentRootDock;
					locationInDestinationDock.setLocation(screenLocation.x, screenLocation.y);
					((FloatDock)currentRootDock).moveDock(currentChildOfRootDock, locationInDestinationDock, dockableOffset);
				}
			}

		}

		// No dragging anymore.
		reset();
	}
	
	public void showPopupMenu(MouseEvent mouseEvent)
	{
		
		// Get the mouse position and the component of the mouse event. 
		Component mouseComponent = (Component)mouseEvent.getSource();
		int x = mouseEvent.getX();
		int y = mouseEvent.getY();

		// Is the deepest component a JTabbedPane?
		Component pressedComponent = SwingUtilities.getDeepestComponentAt(mouseComponent, x, y); 
		JTabbedPane pressedTabbedPane = null;
		if (pressedComponent instanceof JTabbedPane) 
		{
			pressedTabbedPane = (JTabbedPane)pressedComponent;
		}
		else
		{
			pressedTabbedPane = (JTabbedPane)SwingUtilities.getAncestorOfClass(JTabbedPane.class, pressedComponent);
		}
		if (pressedTabbedPane != null)
		{
			// Is the ancestor component a TabbedDock?
			Component ancestorComponent = (Component) SwingUtilities.getAncestorOfClass(Component.class, pressedTabbedPane);
			if (ancestorComponent instanceof TabDock)
			{
				TabDock clickedDock = (TabDock) ancestorComponent;

				// Calculate the dockable offset.
				dockableOffset.setLocation(x, y);
				dockableOffset = SwingUtilities.convertPoint(mouseComponent, dockableOffset, pressedTabbedPane);

				// Get the selected tab and its dockable.
				int oldTabIndex = pressedTabbedPane.indexAtLocation(dockableOffset.x, dockableOffset.y);
				
				// Create the popup menu.
				Dockable clickedDockable = null;
				if (oldTabIndex >= 0)
				{
					Component tabComponent = pressedTabbedPane.getComponentAt(oldTabIndex);
					clickedDockable = clickedDock.retrieveDockableOfComponent(tabComponent);
				}
				Dockable[] dockableArray = new Dockable[clickedDock.getDockableCount()];
				int selectedIndex = -1;
				for (int index = 0; index < clickedDock.getDockableCount(); index++)
				{
					dockableArray[index] = clickedDock.getDockable(index);
					if (dockableArray[index].equals(clickedDockable))
					{
						selectedIndex = index;
					}
				}
				CompositeDockable compositeDockable = new DefaultCompositeDockable(dockableArray, selectedIndex);
				JPopupMenu popupMenu = DockingManager.getComponentFactory().createPopupMenu(clickedDockable, compositeDockable);
				
				// Show the popup menu.
				if (popupMenu != null)
				{
					popupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
				}
			}
		}	

	}

	// Getters / Setters.

	/**
	 * Gets the painter for painting the dragged dockables.
	 * 
	 * @return	dockableDragPainter 		The painter for painting the dragged dockables.
	 */
	public DockableDragPainter getDockableDragPainter()
	{
		return dockableDragPainter;
	}

	/**
	 * Sets the painter for painting the dragged dockables.
	 * 
	 * @param dockableDragPainter 			The painter for painting the dragged dockables.
	 */
	public void setDockableDragPainter(DockableDragPainter dockableDragPainter)
	{
		this.dockableDragPainter = dockableDragPainter;
	}

	// Protected methods.

	/**
	 * Gets the cursor that is used for dragging a dockable,
	 * when the dockable can be docked in an underlying dock.
	 * 
	 * @return							The cursor that is used for dragging a dockable,
	 * 									when the dockable can be docked in an underlying dock.
	 */
	protected Cursor retrieveCanDockCursor()
	{
		return DockingManager.getCanDockCursor();
	}

	/**
	 * Gets the cursor that is used for dragging a dockable,
	 * when the dockable cannot be docked in an underlying dock.
	 * 
	 * @return							The cursor that is used for dragging a dockable,
	 * 									when the dockable cannot be docked in an underlying dock.
	 */
	protected Cursor retrieveCanNotDockCursor()
	{
		return DockingManager.getCanNotDockCursor();
	}

	// Private metods.

	/**
	 * Resets to the state when there is no dragging. All the fields are set to null.
	 */
	private void reset()
	{
		
		originDock = null;
		draggedDockable = null;

	}
	
	/**
	 * Clears up what was painted before.
	 */
	private void clearPainting()
	{
		dockableDragPainter.clear();
	}
	
	/**
	 * Computes the location in screen coordinates of the current mouse position.
	 * 
	 * @param 		mouseEvent		The mouse event that contains information about the current location of the mouse.
	 */
	private void computeScreenLocation(MouseEvent mouseEvent)
	{
		
		screenLocation.setLocation(mouseEvent.getX(), mouseEvent.getY());
		SwingUtilities.convertPointToScreen(screenLocation, (Component)mouseEvent.getSource());
	
	}
	
	/**
	 * Determines if a tab is being dragged.
	 * 
	 * @param 		mouseEvent		The mouse event that contains information about the current location of the mouse.
	 * @return						The index of the tab label that is under the mouse. When no tab is under the mouse
	 * 								-1 is returned.
	 */
	private int tabDragged(MouseEvent mouseEvent)
	{
		// we can only drag a tab if one dockable is dragged.
		if (!(draggedDockable instanceof CompositeDockable) && (sourceTabbedPane != null)) 
		{
			// Compute the location of the mouse in the tabbed pane.
			currentOffsetInTabbedPane.setLocation(mouseEvent.getX(), mouseEvent.getY());
			currentOffsetInTabbedPane = SwingUtilities.convertPoint((Component)mouseEvent.getSource(), currentOffsetInTabbedPane, sourceTabbedPane);

			// Return the index of the tabthat is under the mouse.
			return sourceTabbedPane.indexAtLocation(currentOffsetInTabbedPane.x, currentOffsetInTabbedPane.y);
		}
		
		// A composite dockable is dragged. No tab can be dragged.
		return -1;
	}
	
	/**
	 * Determines if the given lists contain the same elements. We suppose that all the elements of the given lists 
	 * are different.
	 * 
	 * @param 	firstList 					The first list.
	 * @param 	secondList 					The second list.
	 * @return 								True if the given lists contain the same elements, false otherwise.
	 */
	private boolean sameElements(List firstList, List secondList)
	{
		
		// The size hould be the same, otherwise stop.
		if (firstList.size() != secondList.size())
		{
			return false;
		}
		
		// Iterate over the elements of the first list.
		for (int index = 0; index < firstList.size(); index++)
		{
			// Check if the element is also in the second list.
			if (!secondList.contains(firstList.get(index)))
			{
				return false;
			}
		}
		
		// They heve the same elements.
		return true;
	}
	
	/**
	 * Determines if the dragged dock is currently floating.
	 * It is floating, when its root dock is a {@link FloatDock} and
	 * if the dockables in the dragged dock are the only dockables in the child docks of the float dock.
	 * 
	 * @return		True if the dragged dockable is currently floating, false otherwise.
	 */
	private boolean isFloating()
	{
		
		// Get the root dock and the dock under the root.
		currentRootDock = originDock;
		currentChildOfRootDock = null;
		while (currentRootDock.getParentDock() != null)
		{
			currentChildOfRootDock = currentRootDock;
			currentRootDock = currentRootDock.getParentDock();
		}
		
		// Is the root dock the float dock?
		if (currentRootDock instanceof FloatDock)
		{
			// Is the dockable already in this dock and are there no others?
			List childrenOfDockable = new ArrayList();
			List childrenOfDock = new ArrayList();
			DockingUtil.retrieveDockables(draggedDockable, childrenOfDockable);
			DockingUtil.retrieveDockables(currentChildOfRootDock, childrenOfDock);
			if (CollectionUtil.sameElements(childrenOfDockable, childrenOfDock))
			{
				return true;
			}
		}
		
		return false;

	}

}
