package org.fxbench.ui.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.fxbench.BenchApp;
import org.fxbench.ui.docking.DockingManager;
import org.fxbench.ui.docking.component.DefaultSwComponentFactory;
import org.fxbench.ui.docking.component.DockHeader;
import org.fxbench.ui.docking.component.PointDockHeader;
import org.fxbench.ui.docking.dock.BorderDock;
import org.fxbench.ui.docking.dock.CompositeLineDock;
import org.fxbench.ui.docking.dock.LeafDock;
import org.fxbench.ui.docking.dock.Position;
import org.fxbench.ui.docking.dock.SingleDock;
import org.fxbench.ui.docking.dock.SplitDock;
import org.fxbench.ui.docking.dock.TabDock;
import org.fxbench.ui.docking.dock.factory.TabDockFactory;
import org.fxbench.ui.docking.dock.factory.ToolBarDockFactory;
import org.fxbench.ui.docking.dockable.Dockable;
import org.fxbench.ui.docking.model.DefaultDockingPath;
import org.fxbench.ui.docking.model.DockModel;
import org.fxbench.ui.docking.model.DockingPath;
import org.fxbench.ui.docking.model.DockingPathModel;
import org.fxbench.ui.docking.model.FloatDockModel;
import org.fxbench.ui.docking.model.codec.DockModelPropertiesDecoder;
import org.fxbench.ui.docking.model.codec.DockModelPropertiesEncoder;
import org.fxbench.ui.panel.AccountPanel;
import org.fxbench.ui.BenchFrame;
import org.fxbench.ui.BenchToolBar;
import org.fxbench.ui.panel.SymbolPanel;
import org.fxbench.ui.docking.util.LookAndFeelUtil;
import org.fxbench.ui.docking.visualizer.SingleMaximizer;
import org.fxbench.util.ResourceManager;
import org.fxbench.util.properties.PropertyManager;
import org.fxbench.util.properties.PropertySheet;

/**
 * The structure of the application window is like this:
 * 		First there is a border dock for tool bars with buttons. 
 * 		Inside that border dock is a minimizer that minimizes the dockables at the borders. 
 * 		Inside the minimizer is a maximizer. 
 * 		Inside the maximizer is the root dock for all the normal docks.
 * 
 * When the application is stopped, the workspace is saved.
 * When the application is restarted again, the workspace is recovered.
 * The dockables, docks, minimized dockables, and button dockables
 * are showed in the same state as when they were closed.
 * 
 * This example uses a float dock model.
 * The model can be saved with a dock model encoder.
 * The model can be reloaded with a dock model decoder.
 * 
 * @author yld
 */
public class BenchPanel extends JPanel
{
	// Static fields.
	public static final int 			BENCH_WIDTH 			= 1024;
	public static final int 			BENCH_HEIGHT 			= 768;
	public static final String 			BENCH_PROP 				= "fxbench.properties";
	public static final String 			CENTER_DOCKING_PATH_ID  = "centerDockingPathId";
//	public static BenchLookAndFeel[] 	LAF_LIST;

	private String frameId = "fxBench";	//The ID for the owner window.
	private FloatDockModel dockModel; //The model with the docks, dockables, and visualizers
	
	public static final String CHART_OVERLAP_TABBED = "Tabbed";
	public static final String CHART_OVERLAP_SPLIT = "Split";
	private String chartOverlapMode;
	
	// All the dockables of the application.
	BenchToolBar benchToolBar;
	private Dockable symbolDockable;
	private List<Dockable> chartDockableList;
	private Dockable accountDockable;
	private Dockable summaryDockable;
	private Dockable orderDockable;
	private Dockable openPositionDockable;
	private Dockable closedPositionDockable;
	private Dockable messageDockable;
	private CompositeLineDock chartDock;
	
	// Constructors.
	public BenchPanel(BenchFrame frame)
	{
		super(new BorderLayout());
		ResourceManager resmng = BenchApp.getInst().getResourceManager();
		
		chartOverlapMode = getChartOverlayProperty();
		if (chartOverlapMode == null || chartOverlapMode.length() == 0) {
			chartOverlapMode = CHART_OVERLAP_TABBED;
		}

		// Set our custom component factory.
		DockingManager.setComponentFactory(new BenchComponentFactory());

		// Create a maximizer.
		SingleMaximizer maximizer = new SingleMaximizer();
		
		// Create the SymbolPanel.
		SymbolPanel symbolPanel = new SymbolPanel(frame, resmng);
		symbolDockable = symbolPanel.createDockable();
		
		// Create the ChartPanel.
		chartDockableList = new ArrayList<Dockable>();
		for(Entry<String, List<PropertySheet>> entry : PropertyManager.getInstance().getChartPropSheets().entrySet()){
			ChartPanel chartPanel = new ChartPanel(frame, entry.getKey(), entry.getValue());
			Dockable chartDockable = chartPanel.createDockable();
			chartDockableList.add(chartDockable);
		}			

		// Create the AccountPanel
		AccountPanel accountPanel = new AccountPanel(frame, resmng);
		accountDockable = accountPanel.createDockable();
		
		// Create the SummaryPanel
		SummaryPanel sumaryPanel = new SummaryPanel(frame, resmng);
		summaryDockable = sumaryPanel.createDockable();
		
		// Create the OrderPanel
		OrderPanel ordersPanel = new OrderPanel(frame, resmng);
		orderDockable = ordersPanel.createDockable();
		
		// Create the OpenPositionsPanel
		OpenPositionPanel openPositionsPanel = new OpenPositionPanel(frame, resmng);
		openPositionDockable = openPositionsPanel.createDockable();
		
		// Create the ClosedPositionsPanel
		ClosedPositionPanel closedPositionsPanel = new ClosedPositionPanel(frame, resmng);
		closedPositionDockable = closedPositionsPanel.createDockable();
		
		// Create the MessagesPanel
		MessagePanel messagesPanel = new MessagePanel(frame, resmng);
		messageDockable = messagesPanel.createDockable();

		benchToolBar = new BenchToolBar(this);
		
		// Try to decode the dock model from file.
		BenchDockModelPropertiesDecoder dockModelDecoder = new BenchDockModelPropertiesDecoder();
		if (dockModelDecoder.canDecodeSource(BENCH_PROP)) {
			try  {
				// Create the map with the dockables, that the decoder needs.
				Map dockablesMap = new HashMap();
				dockablesMap.put(symbolDockable.getID(), symbolDockable);
				for (Dockable dockable : chartDockableList) {
					dockablesMap.put(dockable.getID(), dockable);
				}
				dockablesMap.put(accountDockable.getID(), accountDockable);
				dockablesMap.put(summaryDockable.getID(), summaryDockable);
				dockablesMap.put(orderDockable.getID(), orderDockable);
				dockablesMap.put(openPositionDockable.getID(), openPositionDockable);
				dockablesMap.put(closedPositionDockable.getID(), closedPositionDockable);
				dockablesMap.put(messageDockable.getID(), messageDockable);
				
				// Create the map with the owner windows, that the decoder needs.
				Map ownersMap = new HashMap();
				ownersMap.put(frameId, frame);
				
				// Create the map with the visualizers, that the decoder needs.
				Map visualizersMap = new HashMap();
				visualizersMap.put("maximizer", maximizer);

				// Decode the file.
				dockModel = (FloatDockModel)dockModelDecoder.decode(BENCH_PROP, dockablesMap, ownersMap, visualizersMap);
			}
			catch (FileNotFoundException fileNotFoundException){
				System.out.println("Could not find the file [" + BENCH_PROP + "] with the saved dock model.");
				System.out.println("Continuing with the default dock model.");
			}
			catch (IOException ioException){
				System.out.println("Could not decode a dock model: [" + ioException + "].");
				ioException.printStackTrace();
				System.out.println("Continuing with the default dock model.");
			}
		}

		// These are the root docks.
		SplitDock benchSplitDock = null;
		BorderDock minimizerBorderDock = null;
		DockingPath centerDockingPath = null;
		if (dockModel == null) {	
			// Create the dock model for the docks because they could not be retrieved from a file.
			dockModel = new FloatDockModel(BENCH_PROP);
			dockModel.addOwner(frameId, frame);
			
			// Give the dock model to the docking manager.
			DockingManager.setDockModel(dockModel);
	
			// Create the left tab docks, and add the dockables to tab docks.
			SplitDock leftSplitDock = new SplitDock();
			TabDock leftTabbedDock = new TabDock();
			leftTabbedDock.addDockable(symbolDockable, new Position(0));
			leftSplitDock.addChildDock(leftTabbedDock, new Position(Position.CENTER));
			
			// Create the bottom tab docks, and add the dockables to tab docks.
			SplitDock bottomSplitDock = new SplitDock();
			TabDock bottomTabbedDock = new TabDock();
			bottomTabbedDock.addDockable(accountDockable, new Position(0));
			bottomTabbedDock.addDockable(orderDockable, new Position(1));
			bottomTabbedDock.addDockable(openPositionDockable, new Position(2));
			bottomTabbedDock.addDockable(closedPositionDockable, new Position(3));
			bottomTabbedDock.addDockable(summaryDockable, new Position(4));
			bottomTabbedDock.addDockable(messageDockable, new Position(5));
			bottomSplitDock.addChildDock(bottomTabbedDock, new Position(Position.CENTER));
			
			// Create the center(chart) tab docks, and add the dockables to tab docks.
			chartDock = new CompositeLineDock(
					CompositeLineDock.ORIENTATION_VERTICAL, true, new TabDockFactory());
			loadChartDock();
			
			SplitDock rightSplitDock = new SplitDock();
			rightSplitDock.addChildDock(chartDock, new Position(Position.CENTER));
			rightSplitDock.addChildDock(bottomSplitDock, new Position(Position.BOTTOM));
			rightSplitDock.setDividerLocation(380);
			
			benchSplitDock = new SplitDock();
			benchSplitDock.addChildDock(leftSplitDock, new Position(Position.LEFT));
			benchSplitDock.addChildDock(rightSplitDock, new Position(Position.RIGHT));
			benchSplitDock.setDividerLocation(240);		

			// Add the root dock to the dock model.
			dockModel.addRootDock("benchDock", benchSplitDock, frame);

			// Add the maximizer to the dock model.
			dockModel.addVisualizer("maximizer", maximizer, frame);
			
			// Create the border dock of the minimizer.
			minimizerBorderDock = new BorderDock(new ToolBarDockFactory());
			minimizerBorderDock.setMode(BorderDock.MODE_MINIMIZE_BAR);
			minimizerBorderDock.setCenterComponent(maximizer);
	
			// Create the tool bar border dock for the buttons.
			benchToolBar.createToolBar(minimizerBorderDock);
			// Add this dock also as root dock to the dock model.
			dockModel.addRootDock("toolBarBorderDock", benchToolBar.getToolBarBorderDock(), frame);

			// Add the paths of the docked dockables to the model with the docking paths.
			addDockingPath(symbolDockable);
			for (Dockable dockable : chartDockableList) {
				addDockingPath(dockable);
			}
			addDockingPath(accountDockable);
			addDockingPath(orderDockable);
			addDockingPath(summaryDockable);
			addDockingPath(openPositionDockable);
			addDockingPath(closedPositionDockable);
			addDockingPath(messageDockable);
			
			// The docking path where very new windows will be placed.
			centerDockingPath = DefaultDockingPath.copyDockingPath(CENTER_DOCKING_PATH_ID, DockingManager.getDockingPathModel().getDockingPath(symbolDockable.getID()));
			
		} else {
			// Get the root dock from the dock model.
			benchSplitDock = (SplitDock)dockModel.getRootDock("totalDock");
			BorderDock toolBarBorderDock = (BorderDock)dockModel.getRootDock("toolBarBorderDock");
			benchToolBar.setToolBarBorderDock(toolBarBorderDock);
			minimizerBorderDock = (BorderDock)toolBarBorderDock.getChildDockOfPosition(Position.CENTER);
			minimizerBorderDock.setCenterComponent(maximizer);
			
			// Get the docking path where very new windows have to be docked.
			centerDockingPath = dockModelDecoder.getCenterDockingPath();
		}

		// Listen when the frame is closed. The workspace should be saved.
//		frame.addWindowListener(new BenchSaver(centerDockingPath));

		// Add the content to the maximize panel.
		maximizer.setContent(benchSplitDock);
						
		// Add the border dock of the minimizer to the panel.
		add(benchToolBar.getToolBarBorderDock(), BorderLayout.CENTER);
	}
	
	private void loadChartDock() {
		//clear all child dock
		while(chartDock.getChildDockCount() > 0) {
			chartDock.ghostChild(chartDock.getChildDock(0));
		}
		//reset all child dock
		if (chartOverlapMode.equals(CHART_OVERLAP_TABBED)) {
			TabDock chartTabbedDock = new TabDock();
			for (int i = 0; i < chartDockableList.size(); i++) {
				chartTabbedDock.addDockable(chartDockableList.get(i), new Position(i));
			}
			chartDock.addChildDock(chartTabbedDock, new Position(Position.CENTER));
		} else if (chartOverlapMode.equals(CHART_OVERLAP_SPLIT)) {
			for (int i = 0; i < chartDockableList.size(); i++) {
				SingleDock singleDock = new SingleDock();
				singleDock.addDockable(chartDockableList.get(i), new Position(0));
				chartDock.addChildDock(singleDock, new Position(i/*Position.BOTTOM*/));
			}
		}
	}
	
	public FloatDockModel getDockModel() {
		return dockModel;
	}

	public void setDockModel(FloatDockModel dockModel) {
		this.dockModel = dockModel;
	}
	
	public Dockable getSymbolDockable() {
		return symbolDockable;
	}

	public Dockable getAccountDockable() {
		return accountDockable;
	}

	public Dockable getSummaryDockable() {
		return summaryDockable;
	}
	
	public Dockable getOrderDockable() {
		return orderDockable;
	}

	public Dockable getOpenPositionDockable() {
		return openPositionDockable;
	}
	
	public Dockable getClosedPositionDockable() {
		return closedPositionDockable;
	}

	public Dockable getMessageDockable() {
		return messageDockable;
	}
	
	public SymbolPanel getSymbolPanel() {
		return (SymbolPanel)symbolDockable.getContent();
	}
	
	public AccountPanel getAccountPanel() {
		return (AccountPanel)accountDockable.getContent();
	}
	
	public SummaryPanel getSummaryPanel() {
		return (SummaryPanel)summaryDockable.getContent();
	}
	
	public OrderPanel getOrderPanel() {
		return (OrderPanel)orderDockable.getContent();
	}
	
	public OpenPositionPanel getOpenPositionPanel() {
		return (OpenPositionPanel)openPositionDockable.getContent();
	}
	
	public ClosedPositionPanel getClosedPositionPanel() {
		return (ClosedPositionPanel)closedPositionDockable.getContent();
	}
	
	public MessagePanel getMessagePanel() {
		return (MessagePanel)messageDockable.getContent();
	}
	
	public static String getChartOverlayProperty() {
		return PropertyManager.getInstance().getStrVal("preferences.panels.chart.overlap");
	}
	
	public static void setChartOverlayProperty(String val) {
		PropertyManager.getInstance().setProperty("preferences.panels.chart.overlap", val);
	}
	
	public boolean isChartSplitMode() {
		return chartOverlapMode.equals(CHART_OVERLAP_SPLIT);
	}
	
	public String getChartOverlapMode() {
		return chartOverlapMode;
	}

	public void setChartOverlapMode(String overlapMode) {
		if (!this.chartOverlapMode.equals(overlapMode)) {
			this.chartOverlapMode = overlapMode;
			loadChartDock();
//			shiftCharts(ChartPanel.SHIFT_NEXT, 0);
			benchToolBar.setChartToolBarEnable(overlapMode.equals(CHART_OVERLAP_SPLIT));
		}
	}

	public void addChartPanel(ChartPanel chartPanel) {
		Dockable chartDockable = chartPanel.createDockable();
		if (chartOverlapMode.equals(CHART_OVERLAP_TABBED)) {
			TabDock tabDock = (TabDock)chartDock.getChildDock(0);
			tabDock.addDockable(
					chartDockable, new Position(tabDock.getTabbedPane().getTabCount()));
			chartDockableList.add(chartDockable);
		} else if (chartOverlapMode.equals(CHART_OVERLAP_SPLIT)) {
			SingleDock singleDock = new SingleDock();
			singleDock.addDockable(chartDockable, new Position(0));
			chartDock.addChildDock(singleDock, new Position(Position.BOTTOM));
			chartDockableList.add(chartDockable);
		}
	}
	
	public void removeChartPanel(ChartPanel chartPanel) {
		for (Dockable dockable : chartDockableList) {
			if (dockable.getContent() == chartPanel) {
				chartDockableList.remove(dockable);
				break;
			}
		}
	}
	
	public boolean toolBarIsVisible() {
		return benchToolBar.getToolBarBorderDock().isVisible();
	}
	
	public void setToolBarVisible(boolean visible) {
		benchToolBar.getToolBarBorderDock().setVisible(visible);	
	}
	
	public void moveChartsCrossY(ChartPanel srcChartPanel, Point crossPos) {
		for (Dockable dockable : chartDockableList) {
			ChartPanel chartPanel = (ChartPanel)dockable.getContent();
			if (chartPanel != srcChartPanel) {
				chartPanel.moveCrossY(crossPos);
				chartPanel.repaint();
			}
		}
	}
	
	private Date getLatestDateOfCharts() {
		Date latest = new Date(0);
		for (Dockable dockable : chartDockableList) {
			ChartPanel chartPanel = (ChartPanel)dockable.getContent();
			if (latest.compareTo(chartPanel.getChartEndDate()) < 0) {
				latest = chartPanel.getChartEndDate();
			}
		}
		return latest;
	}
	
	private ChartPanel getMinIntervalChart() {
		ChartPanel minIntervalChart = null;
		if (chartDockableList.size() == 0) {
			return null;
		} else {
			minIntervalChart = (ChartPanel)chartDockableList.get(0).getContent();
		}
		for (int i = 1; i < chartDockableList.size(); i++) {
			ChartPanel chartPanel = (ChartPanel)chartDockableList.get(i).getContent();
			if (chartPanel.getInterval().getSeconds() < minIntervalChart.getInterval().getSeconds()) {
				minIntervalChart = chartPanel; 
			}
		}
		return minIntervalChart;
	}
	
	public void prevCharts(int frames) {
		shiftCharts(ChartPanel.SHIFT_PREV, frames);
	}
	
	public void nextCharts(int frames) {
		shiftCharts(ChartPanel.SHIFT_NEXT, frames);
	}
	
	public void shiftCharts(int direction, int frames) {
		Date shiftEndDate = getLatestDateOfCharts();
		ChartPanel minChartPanel = getMinIntervalChart();
		if (shiftEndDate.getTime() == 0 || minChartPanel == null) {
			return;
		}
		
		minChartPanel.setAutoScale(true);
		if (minChartPanel.getChartEndDate().compareTo(shiftEndDate) < 0) {
			shiftEndDate = minChartPanel.shift(shiftEndDate);
			if (shiftEndDate == null) {
				return;
			}
			minChartPanel.refresh();
		}
		int shiftBars = minChartPanel.getShiftFrameBars() * direction * frames;
		if (frames > 0) {
			shiftEndDate = minChartPanel.shift(shiftBars);
			if (shiftEndDate == null) {
				return;
			}
			minChartPanel.refresh();
		}
		for (Dockable dockable : chartDockableList) {
			ChartPanel chartPanel = (ChartPanel)dockable.getContent();
			if (chartPanel != minChartPanel) {
				double multi = chartPanel.getInterval().multiple(minChartPanel.getInterval());
				chartPanel.setAxisXScale(minChartPanel.getAxisXScale() * multi);
				chartPanel.setAutoScale(true);
				chartPanel.shift(Double.valueOf(shiftBars / multi).intValue());
				chartPanel.shift(shiftEndDate);
				chartPanel.refresh();
			}
		}
	}
	
	/**
	 * Creates a docking path for the dockable. This path is added to the docking pah model of the docking
	 * manager.
	 * To create a docking path, the dock model should already be given to the docking manager.
	 * 
	 * @param 	dockable	The dockable for which to create a docking path.
	 * @return				The created docking path.
	 */
	private DockingPath addDockingPath(Dockable dockable) {
		if (dockable.getDock() != null) {
			// Create the docking path of the dockable.
			DockingPath dockingPath = DefaultDockingPath.createDockingPath(dockable);
			DockingManager.getDockingPathModel().add(dockingPath);
			return dockingPath;
		}

		return null;
	}
	
	// Private classes.

	/**
	 * A listener for window closing events. Saves the workspace, when the application window is closed.
	 * 
	 * @author Heidi Rakels.
	 */
	private class BenchSaver implements WindowListener {
		private DockingPath dockingPath;
		
		private BenchSaver(DockingPath centerDockingPath) {
			if (centerDockingPath != null) {
				this.dockingPath = DefaultDockingPath.copyDockingPath("centerDockingPath", centerDockingPath);
			}
		}

		public void windowClosing(WindowEvent windowEvent) {
			// Save the dock model.
			DockModelPropertiesEncoder encoder = new MyDockModelPropertiesEncoder(dockingPath);
			if (encoder.canSave(dockModel)) {
				try {
				//	encoder.save(dockModel);
				} catch (Exception e) {
					System.out.println("Error while saving the dock model.");
					e.printStackTrace();
				}
			} else {
				System.out.println("Could not save the dock model.");
			}
		}

		public void windowDeactivated(WindowEvent windowEvent) {}
		public void windowDeiconified(WindowEvent windowEvent) {}
		public void windowIconified(WindowEvent windowEvent) {}
		public void windowOpened(WindowEvent windowEvent) {}
		public void windowActivated(WindowEvent windowEvent) {}
		public void windowClosed(WindowEvent windowEvent) {}
	}

	private class BenchComponentFactory extends DefaultSwComponentFactory 
	{
		/** The default size of the divider for splitpanes. */
		public static final int DEFAULT_DIVIDER_SIZE = 2;
		
		// Fields.

		/** The size of the divider for splitpanes. */
		private int dividerSize = DEFAULT_DIVIDER_SIZE;

		// Overwritten methods of DefaultSwComponentFactory.

		public JSplitPane createJSplitPane() {
			JSplitPane splitPane = super.createJSplitPane();
			splitPane.setDividerSize(dividerSize);
			return splitPane;
		}
		
		public DockHeader createDockHeader(LeafDock dock, int orientation) {
			return new PointDockHeader(dock, orientation);
		}
		
		/**
		 * Gets the size of the divider for split panes.
		 * 
		 * @return				The size of the divider for split panes.
		 */
		public int getDividerSize() {
			return dividerSize;
		}

		/**
		 * Sets the size of the divider for split panes.
		 * 
		 * @param dividerSize	The size of the divider for split panes.
		 */
		public void setDividerSize(int dividerSize) {
			this.dividerSize = dividerSize;
		}
		
	}
	
	private class MyDockModelPropertiesEncoder extends DockModelPropertiesEncoder 
	{
		private DockingPath centerDockingPath;
		
		private MyDockModelPropertiesEncoder(DockingPath centerDockingPath) {
			this.centerDockingPath = centerDockingPath;
		}

		protected void saveProperties(DockModel dockModel, DockingPathModel dockingPathModel, Properties properties, Map dockKeys) {
			super.saveProperties(dockModel, dockingPathModel, properties, dockKeys);
			if (centerDockingPath != null){
				centerDockingPath.saveProperties(CENTER_DOCKING_PATH_ID, properties, dockKeys);
			}
		}
	}
	
	private class BenchDockModelPropertiesDecoder extends DockModelPropertiesDecoder {
		private DockingPath centerDockingPath;
		protected DockModel decodeProperties(Properties properties, String sourceName, Map dockablesMap, Map ownersMap, Map visualizersMap, Map docks) throws IOException {
			DockModel dockModel = super.decodeProperties(properties, sourceName, dockablesMap, ownersMap, visualizersMap, docks);
			if (dockModel != null) {
				centerDockingPath = new DefaultDockingPath();
				centerDockingPath.loadProperties(CENTER_DOCKING_PATH_ID, properties, docks);
				if (centerDockingPath.getID() == null) {
					System.out.println("The file 'workspace_1_5.dck' of an older version is still available, remove this file.");
					centerDockingPath = null;
				}
			}
			
			return dockModel;
		}
		
		public DockingPath getCenterDockingPath() {
			return centerDockingPath;
		}
		
	}

	// Main method.

	public static BenchPanel createAndShowGUI(BenchFrame frame) { 
		// Create the look and feels.
//		LAF_LIST  = new BenchLookAndFeel[9];	
//		LAF_LIST[0] = new BenchLookAndFeel("Substance", "org.jvnet.substance.skin.SubstanceModerateLookAndFeel", BenchLookAndFeel.THEME_DEAULT);
//		LAF_LIST[1] = new BenchLookAndFeel("Mac", "javax.swing.plaf.mac.MacLookAndFeel", BenchLookAndFeel.THEME_DEAULT);
//		LAF_LIST[2] = new BenchLookAndFeel("Metal", "javax.swing.plaf.metal.MetalLookAndFeel", BenchLookAndFeel.THEME_DEAULT);
//		LAF_LIST[3] = new BenchLookAndFeel("Liquid", "com.birosoft.liquid.LiquidLookAndFeel", BenchLookAndFeel.THEME_DEAULT);
//		LAF_LIST[4] = new BenchLookAndFeel("Windows", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel", BenchLookAndFeel.THEME_DEAULT);
//		LAF_LIST[5] = new BenchLookAndFeel("Nimrod Ocean", "com.nilo.plaf.nimrod.NimRODLookAndFeel", BenchLookAndFeel.THEME_OCEAN);
//		LAF_LIST[6] = new BenchLookAndFeel("Nimrod Gold", "com.nilo.plaf.nimrod.NimRODLookAndFeel", BenchLookAndFeel.THEME_GOLD);
//		LAF_LIST[7] = new BenchLookAndFeel("Nimbus", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel", BenchLookAndFeel.THEME_DEAULT);
//		LAF_LIST[8] = new BenchLookAndFeel("TinyLaF", "de.muntjak.tinylookandfeel.TinyLookAndFeel", BenchLookAndFeel.THEME_DEAULT);

		// Set the first enabled look and feel.
//		try {
//		//	if (LAF_LIST[7].isSupported()) {
//				LAF_LIST[4].setSelected(true);
//				UIManager.setLookAndFeel(LAF_LIST[4].getClassName());
//		//	}
//		} catch (Exception e) { }

		// Remove the borders from the split panes and the split pane dividers.
		LookAndFeelUtil.removeAllSplitPaneBorders();
	
		// Set the default location and size.
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenSize.width > BENCH_WIDTH || screenSize.height > BENCH_HEIGHT) {
			frame.setLocation(0, 0);
			frame.setSize(screenSize.width, screenSize.height);
		} else {
			frame.setLocation((screenSize.width - BENCH_WIDTH) / 2, (screenSize.height - BENCH_HEIGHT) / 2);
			frame.setSize(BENCH_WIDTH, BENCH_HEIGHT);
		}
	
		// Create the panel and add it to the frame.
		BenchPanel benchPanel = new BenchPanel(frame);
		frame.getContentPane().add(benchPanel);
		
		return benchPanel;
	}
}

