package org.fxbench.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.fxbench.entity.TPosition.BnS;
import org.fxbench.trader.action.ClosePositionAction;
import org.fxbench.trader.action.CreateEntryOrderAction;
import org.fxbench.trader.action.CreateMarketOrderAction;
import org.fxbench.trader.action.ReportAction;
import org.fxbench.trader.action.SetStopLimitAction;
import org.fxbench.ui.auxi.UIManager;
import org.fxbench.ui.docking.DockingManager;
import org.fxbench.ui.docking.dock.BorderDock;
import org.fxbench.ui.docking.dock.CompositeLineDock;
import org.fxbench.ui.docking.dock.LineDock;
import org.fxbench.ui.docking.dock.Position;
import org.fxbench.ui.docking.dock.factory.CompositeToolBarDockFactory;
import org.fxbench.ui.docking.dock.factory.ToolBarDockFactory;
import org.fxbench.ui.docking.dockable.ButtonDockable;
import org.fxbench.ui.docking.dockable.Dockable;
import org.fxbench.ui.docking.dockable.DockingMode;
import org.fxbench.ui.docking.drag.DragListener;
import org.fxbench.ui.docking.util.ToolBarButton;
import org.fxbench.ui.help.HelpManager;
import org.fxbench.ui.panel.BenchPanel;

public class BenchToolBar
{
	private BenchPanel benchPanel;
	private BorderDock toolBarBorderDock;
	private LineDock tradeToolBarDock;
	private LineDock chartToolBarDock;
		
	public BenchToolBar (BenchPanel benchPanel) {
		this.benchPanel = benchPanel;
		createButtons();
	}
	
	private void createButtons() {
		//gets instance of user interface manager
        UIManager uiManager = UIManager.getInst();
        
        //trade ToolBar dock
        tradeToolBarDock = new LineDock(LineDock.ORIENTATION_HORIZONTAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
        
        //market order sell button
		Action marketOrderSellAction = CreateMarketOrderAction.newAction(BnS.SELL.name());
        uiManager.addAction(
        		marketOrderSellAction, "IDS_SELL_TEXT", "IDA_SELL_ICON", null, "IDS_SELL_DESC", "IDS_SELL_DESC");
        tradeToolBarDock.addDockable(createButtonDockable(marketOrderSellAction), new Position(0));
        
        //market order buy button
        Action marketOrderBuyAction = CreateMarketOrderAction.newAction(BnS.BUY.name());
        uiManager.addAction(marketOrderBuyAction, "IDS_BUY_TEXT", "IDA_BUY_ICON", null, "IDS_BUY_DESC", "IDS_BUY_DESC");
        tradeToolBarDock.addDockable(createButtonDockable(marketOrderBuyAction), new Position(1));
        
        //set stop limit button
        Action stopLimitOrderAction = SetStopLimitAction.newAction(null);
        uiManager.addAction(stopLimitOrderAction, "IDS_S_L_TEXT", "IDA_S_L_ICON", null, "IDS_S_L_DESC", "IDS_S_L_DESC");
        tradeToolBarDock.addDockable(createButtonDockable(stopLimitOrderAction), new Position(2));
        
        //close position button
        Action closePositionAction = ClosePositionAction.newAction(null);
        uiManager.addAction(
                closePositionAction, "IDS_CLOSE_TEXT", "IDA_CLOSE_ICON", null, "IDS_CLOSE_DESC", "IDS_CLOSE_DESC");
        tradeToolBarDock.addDockable(createButtonDockable(closePositionAction), new Position(3));
        
        //entry order button
        Action entryOrderAction = CreateEntryOrderAction.newAction(BnS.BUY.name());
        uiManager.addAction(
        	entryOrderAction, "IDS_ENTRY_TEXT", "IDA_ENTRY_ICON", null, "IDS_ENTRY_DESC", "IDS_ENTRY_DESC");
        tradeToolBarDock.addDockable(createButtonDockable(entryOrderAction), new Position(4));

        //report button
        Action reportAction = ReportAction.newAction(null);
        uiManager.addAction(
        	reportAction, "IDS_REPORT_TEXT", "IDA_REPORT_ICON", null, "IDS_REPORT_DESC", "IDS_REPORT_DESC");
        tradeToolBarDock.addDockable(createButtonDockable(reportAction), new Position(5));

        //help button
        Action helpAction = new AbstractAction() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		HelpManager mgr = HelpManager.getInst();
                //shows help window
                mgr.showHelp();
        	}
        };
        uiManager.addAction(helpAction, "IDS_HELP_TEXT", "IDA_HELP_ICON", null, "IDS_HELP_DESC", "IDS_HELP_DESC");
        tradeToolBarDock.addDockable(createButtonDockable(helpAction), new Position(6));
        
        //chart ToolBar dock
        chartToolBarDock = new LineDock(LineDock.ORIENTATION_HORIZONTAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
        
        //prev button
        Action prevAction = (Action)new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				benchPanel.prevCharts(1);
			}
        };
        UIManager.getInst().addAction(prevAction,
                            "IDS_CHART_PREV_TEXT",
                            "ID_CHART_PREV_ICON",
                            null,
                            "IDS_CHART_PREV_DESC",
                            "IDS_CHART_PREV_DESC");
        chartToolBarDock.addDockable(createButtonDockable(prevAction), new Position(0));
        
        //next button
        Action nextAction = (Action)new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				benchPanel.nextCharts(1);
			}
        };
        UIManager.getInst().addAction(nextAction,
                            "IDS_CHART_NEXT_TEXT",
                            "ID_CHART_NEXT_ICON",
                            null,
                            "IDS_CHART_NEXT_DESC",
                            "IDS_CHART_NEXT_DESC");
        chartToolBarDock.addDockable(createButtonDockable(nextAction), new Position(1));
	}

	public void createToolBar(BorderDock centerChildDock) {
		toolBarBorderDock = new BorderDock(new CompositeToolBarDockFactory(), centerChildDock);
		toolBarBorderDock.setMode(BorderDock.MODE_TOOL_BAR);
		CompositeLineDock compositeToolBarDock = new CompositeLineDock(CompositeLineDock.ORIENTATION_HORIZONTAL, false,
				new ToolBarDockFactory(), DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
		toolBarBorderDock.setDock(compositeToolBarDock, Position.TOP);
		compositeToolBarDock.addChildDock(tradeToolBarDock, new Position(0));
		compositeToolBarDock.addChildDock(chartToolBarDock, new Position(1));
		
		setChartToolBarEnable(benchPanel.isChartSplitMode());
	}
	
	public BorderDock getToolBarBorderDock() {
		return toolBarBorderDock;
	}

	public void setToolBarBorderDock(BorderDock toolBarBorderDock) {
		this.toolBarBorderDock = toolBarBorderDock;
	}
	
	public void setChartToolBarEnable(boolean enable) {
		for (int i = 0; i < chartToolBarDock.getDockableCount(); i++) {
			ToolBarButton button = (ToolBarButton)chartToolBarDock.getDockable(i).getContent();
			button.setEnabled(enable);
		}
	}

	/**
	 * Creates a dockable with a button as content.
	 * 
	 * @param id			The ID of the dockable that has to be created.
	 * @param title			The title of the dialog that will be displayed.
	 * @param icon			The icon that will be put on the button.
	 * @param message		The message that will be displayed when the action is performed.
	 * @return				The dockable with a button as content.
	 */
	private Dockable createButtonDockable(Action action) {
		// Create the button.
		ToolBarButton button = new ToolBarButton(action);
		// Create the dockable with the button as component.
		ButtonDockable buttonDockable = new ButtonDockable((String)action.getValue(Action.NAME), button);
		// Add a dragger to the individual dockable.
		createDockableDragger(buttonDockable);
		return buttonDockable;
	}
	
	/**
	 * Adds a drag listener on the content component of a dockable.
	 */
	private void createDockableDragger(Dockable dockable) {	
		// Create the dragger for the dockable.
		DragListener dragListener = DockingManager.getDockableDragListenerFactory().createDragListener(dockable);
		dockable.getContent().addMouseListener(dragListener);
		dockable.getContent().addMouseMotionListener(dragListener);
	}
	
	/**
	 * An action that shows a message in a dialog.
	 */
//	private class MessageAction extends AbstractAction {
//
//		private Component parentComponent;
//		private String message;
//		private String name;
//		
//		public MessageAction(Component parentComponent, String name, Icon icon, String message) {
//			super(null, icon);
//			putValue(Action.SHORT_DESCRIPTION, name);
//			this.message = message;
//			this.name = name;
//			this.parentComponent = parentComponent;
//		}
//
//		public void actionPerformed(ActionEvent actionEvent) {
//			JOptionPane.showMessageDialog(parentComponent,
//					message, name, JOptionPane.INFORMATION_MESSAGE);
//		}
//	}
}
