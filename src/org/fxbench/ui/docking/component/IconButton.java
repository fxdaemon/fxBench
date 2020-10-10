package org.fxbench.ui.docking.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.border.Border;

/**
 * <p>
 * A component that can be used as button to execute a given action.
 * </p>
 * <p>
 * It has a fixed size. The icon of the given action will be displayed on this component.
 * </p>
 * <p>
 * When the button is clicked, the action is performed.
 * </p>
 * 
 * @author Heidi Rakels.
 */
class IconButton extends JLabel
{

	// Static fields.

	/** The button will always have this size. */
	private static final Dimension DEFAULT_SIZE = new Dimension(14, 14);
	/** The border width of the button. */
	private static final int BORDER = 1;
	/** The empty border of the button. */
	private static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER, BORDER);
	/** The border of the button when the mouse is over it. */
	//private static final Border LINE_BORDER = BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER, BORDER);
	private static final Border LINE_BORDER = BorderFactory.createLineBorder(Color.gray);
	
	// Fields.

	/** The action that will be performed when this button is clicked. */
	private Action action;

	// Constructors.

	/**
	 * Constructs an icon button with the given action. 
	 * 
	 * @param	action 		The action that will be performed when this button is clicked.
	 */
	public IconButton(Action action)
	{
		
		setDimensions();
		setAction(action);
		addMouseListener(new ClickListener());
		setBorder(EMPTY_BORDER);
		setOpaque(false);
		
	}
	
	// Getters / Setters.

	/**
	 * Gets the action that will be performed when this button is clicked.
	 * 
	 * @return				The action that will be performed when this button is clicked.
	 */
	public Action getAction()
	{
		return action;
	}

	/**
	 * Sets the action that will be performed when this button is clicked.
	 * 
	 * @param action		The action that will be performed when this button is clicked.
	 */
	public void setAction(Action action)
	{
		
		if (action == null)
		{
			throw new IllegalArgumentException("Acion is null.");
		}
		
		this.action = action;
		if (action instanceof AbstractAction)
		{
			Icon icon = (Icon)action.getValue(Action.SMALL_ICON);
			this.setIcon(icon);
			String description = (String)action.getValue(Action.SHORT_DESCRIPTION);
			this.setToolTipText(description);
		}
		
	}

	// Private classes.

	/**
	 * This mouse listener performs the action when the mouse is clicked.
	 */
	private class ClickListener extends MouseAdapter
	{

		// Overwritten methods of MouseAdapter.

		public void mouseClicked(MouseEvent mouseEvent)
		{
			// Create the action event.
			ActionEvent actionEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, (String)action.getValue(Action.SHORT_DESCRIPTION), mouseEvent.getModifiers());
			
			// Perform the action.
			action.actionPerformed(actionEvent);
		}

		public void mouseEntered(MouseEvent mouseEvent)
		{
			setBorder(LINE_BORDER);
		}

		public void mouseExited(MouseEvent mouseEvent)
		{
			setBorder(EMPTY_BORDER);
		}
		
	}
	
	// Private metods.

	/**
	 * Sets the preferred, maximum and minimum size of the button.
	 */
	private void setDimensions()
	{
		
		setPreferredSize(DEFAULT_SIZE);
		setMaximumSize(DEFAULT_SIZE);
		setMinimumSize(DEFAULT_SIZE);
		
	}

	
}
