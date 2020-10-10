/*
* Copyright 2020 FXDaemon
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.fxbench.ui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import org.fxbench.chart.IndicatorChangeDialog;
import org.fxbench.chart.IndicatorPane;
import org.fxbench.chart.IndicatorSelectDialog;
import org.fxbench.chart.OverlayPane;
import org.fxbench.chart.DataItem;
import org.fxbench.chart.Dataset;
import org.fxbench.chart.ta.Indicator;
import org.fxbench.entity.TPriceBar;
import org.fxbench.entity.TPriceBar.Interval;
import org.fxbench.ui.BenchFrame;
import org.fxbench.ui.auxi.UIManager;
import org.fxbench.ui.docking.util.ToolBarButton;
import org.fxbench.util.properties.ChartSchema;
import org.fxbench.util.properties.PropertyManager;
import org.fxbench.util.properties.PropertySheet;
import org.fxbench.util.properties.TemplateManager;
import org.fxbench.util.signal.ISignalListener;
import org.fxbench.util.signal.Signal;
import org.fxbench.util.signal.Signaler;
import org.fxbench.util.signal.Signal.SignalType;

public class ChartPanel extends BasePanel implements ISignalListener
{	
	public final static int SHIFT_NEXT = 1;
	public final static int SHIFT_PREV = -1;
	private final static double AXIS_X_SCALE = 10;		//10pixels / 1peirod
	private final static double ZOOM_X_FACTOR = 0.1;
	private final static int SPLIT_DIVIDER_SIZE = 2;
	
	private ChartToolBar chartToolbar;
	private ChartPopupMenu chartPopupMenu;
	
	private double axisXScale;
	private boolean isLockView;
	
	private int ask_bid;
	private List<TPriceBar> priceBarList;
	private Dataset dataset;
	private Date panelBeginDate;
	private Date chartEndDate;
	
	private ChartSchema chartSchema;
	private String symbol;
	private Interval interval;
	private OverlayPane overlayPane;
	private List<IndicatorPane> indicatorPaneList; 

	public ChartPanel(BenchFrame mainFrame, ChartSchema schema, List<PropertySheet> chartPropSheetList) {
		super(mainFrame);
		
		chartSchema = schema;
		symbol = chartSchema.getSymbol();
		interval = Interval.valueOf(chartSchema.getPeriod());
		
		chartToolbar = new ChartToolBar(this);
		add(chartToolbar, BorderLayout.NORTH);
		chartPopupMenu = new ChartPopupMenu();
		
		ask_bid = TPriceBar.PRICE_ASK;
		axisXScale = AXIS_X_SCALE;
		priceBarList = new ArrayList<TPriceBar>();
		isLockView = getTradeDesk().getTradingServerSession().isLockView();
		chartEndDate = getTradeDesk().getTradingServerSession().getOrginChartEndDate();		

		overlayPane = new OverlayPane(this);
		add(overlayPane.getSplitPane());
		indicatorPaneList = new ArrayList<IndicatorPane>();
		
		for (PropertySheet propSheet : chartPropSheetList) {
			if (propSheet.getName().equals(TemplateManager.PRICEBAR_NODE)) {
				overlayPane.setPriceBar(propSheet);				
			} else if (propSheet.getName().equals(TemplateManager.OVERLAY_NODE)) {
				overlayPane.addIndicator(propSheet);
			} else if (propSheet.getName().equals(TemplateManager.INDICATOR_NODE)) {
				addIndicatorPane(new IndicatorPane(this, propSheet));
			}
		}
		overlayPane.rePosIndicatorLabel();

		getTradeDesk().getPriceBars().subscribe(this, SignalType.ADD);
		getTradeDesk().getPriceBars().subscribe(this, SignalType.CHANGE);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				loadPriceBarFromDesk(getTradeDesk().getServerTime(), 0);		
			}
		});
	}
	
	public ChartPanel(BenchFrame mainFrame, String schema, List<PropertySheet> propSheetList) {
		this(mainFrame, ChartSchema.valueOf(schema), propSheetList);
	}
	
	public ChartPopupMenu getChartPopupMenu() {
		return chartPopupMenu;
	}
	
	public double getAxisXScale() {
		return axisXScale;
	}
	
	public void setAxisXScale(double scale) {
		this.axisXScale = scale;
	}
	
	public double getZoomXFactor() {
		return ZOOM_X_FACTOR;
	}
	
	public int getShiftFrameBars() {
		int shiftBars = overlayPane.getAxisXVisibleBars() - 1;
		if (shiftBars > getTradeDesk().getTradingServerSession().getShiftMaxBars()) {
			shiftBars = getTradeDesk().getTradingServerSession().getShiftMaxBars();
		}
		return shiftBars;
	}
	
	private int getPriceBarLoadSize() {
		int loadBars = overlayPane.getAxisXVisibleBars();
		loadBars = Double.valueOf(loadBars * 1.6).intValue();
		if (loadBars < Indicator.TA_CALCULATE_SIZE) {
			loadBars = Indicator.TA_CALCULATE_SIZE;
		}
		return loadBars;
	}
	
	@Override
	protected String id() {
		return String.valueOf(chartSchema.getNo());
	}

	@Override
	protected String title() {
		return symbol + ", " + interval.name();
	}

	@Override
	protected String tooltip() {
		return title();
	}
	
	private void clear() {
		synchronized (priceBarList) {
			priceBarList.clear();
			dataset.clear();
		}
		axisXScale = AXIS_X_SCALE;
//		isLockView = false;
		overlayPane.initialize();
    	for (IndicatorPane indicatorPane : indicatorPaneList) {
    		indicatorPane.initialize();
    	}
    	refresh();
	}
	
	private TPriceBar loadPriceBarFromDesk(Date takeEndDate, int offset) {
		TPriceBar tail = null;
		List<TPriceBar> takePriceBarList = getTradeDesk().getPriceBars().get(
				symbol, interval, takeEndDate, getPriceBarLoadSize(), offset);
		if (takePriceBarList != null && takePriceBarList.size() > 0) {
			tail = takePriceBarList.get(takePriceBarList.size() - 1);
			synchronized (priceBarList) {
				priceBarList.clear();
				priceBarList.addAll(takePriceBarList);
		        if (ask_bid == TPriceBar.PRICE_ASK) {
		        	dataset = Dataset.valueOf(priceBarList);
		        } else {
		        	dataset = Dataset.valueOfB(priceBarList);
		        }
			}
		}
		return tail;
	}
	
//	private Date getLoadEndDate(Date visibleBeginDate) {
//		int visiblePixels = overlayPane.getAxisXVisiblePixels();
//		Date loadEndDate = null;
//		synchronized (priceBarList) {
//			int count = 0;
//			for (TPriceBar priceBar : priceBarList) {
//				if (priceBar.getStartDate().compareTo(visibleBeginDate) > 0) {
//					count++;
//					if (count == visiblePixels) {
//						loadEndDate = priceBar.getStartDate();
//						break;
//					}
//				}
//			}
//			if (loadEndDate == null) {
//				if (priceBarList.size() > 0) {
//					loadEndDate = priceBarList.get(priceBarList.size() - 1).getStartDate();
//				} else {
//					loadEndDate = visibleBeginDate;
//				}
//			}
//		}
//		return loadEndDate;
//	}
	
	private String getPropSheetPath () {
    	return "preferences.panels.chart.";
    }
	
	private void addIndicatorPane(IndicatorPane indicatorPane) {
		JSplitPane lastSplitPane = null;
		if (indicatorPaneList.size() == 0) {
			lastSplitPane = overlayPane.getSplitPane(); 
		} else {
			lastSplitPane = indicatorPaneList.get(indicatorPaneList.size() - 1).getSplitPane();
		}		
		lastSplitPane.setDividerSize(SPLIT_DIVIDER_SIZE);
		int splitPaneDividerLocation = indicatorPane.getSplitDividerLocation();
		if (splitPaneDividerLocation == 0) {
			splitPaneDividerLocation = Double.valueOf(lastSplitPane.getLeftComponent().getHeight() * 0.618).intValue();
		}
		lastSplitPane.setDividerLocation(splitPaneDividerLocation);
		lastSplitPane.setRightComponent(indicatorPane.getSplitPane());
		indicatorPaneList.add(indicatorPane);
	}
	
	public void removeIndicatorPane(IndicatorPane indicatorPane) {
		int index = indicatorPaneList.indexOf(indicatorPane);
		if (index == -1) {
			return;
		}		
		JSplitPane prevSplitPane = null;
		if (index == 0) {
			prevSplitPane = overlayPane.getSplitPane();
		} else {
			prevSplitPane = indicatorPaneList.get(index - 1).getSplitPane();
		}
		prevSplitPane.setRightComponent(indicatorPane.getSplitPane().getRightComponent());
		indicatorPaneList.remove(index);
	}
	
	public IndicatorPane getIndicatorPane(JSplitPane splitPane) {
		IndicatorPane findIndicatorPane = null;
		for(IndicatorPane indicatorPane : indicatorPaneList) {
			if (indicatorPane.getSplitPane() == splitPane) {
				findIndicatorPane = indicatorPane;
				break;
			}
		}
		return findIndicatorPane;
	}
	
	public Color getBackground() {
    	return PropertyManager.getInstance().getColorVal(getPropSheetPath() + "background");
    }
	
	public Font getMarkerFont() {
		return PropertyManager.getInstance().getFontVal(getPropSheetPath() + "font_mark");
	}
	
	public Color getCrossMarkerForeground() {
		return PropertyManager.getInstance().getColorVal(getPropSheetPath() + "foreground_cross_mark");
	}
	
	public Color getCrossMarkerBackground() {
		return PropertyManager.getInstance().getColorVal(getPropSheetPath() + "background_cross_mark");
	}
	
	public Color getRateMarkerForeground() {
		return PropertyManager.getInstance().getColorVal(getPropSheetPath() + "foreground_rate_mark");
	}
	
	public Color getRateMarkerBackground() {
		return PropertyManager.getInstance().getColorVal(getPropSheetPath() + "background_rate_mark");
	}
	
	public Color getCrossColor() {
		return PropertyManager.getInstance().getColorVal(getPropSheetPath() + "color_cross");
	}
	
	public Color getGridColor() {
		return PropertyManager.getInstance().getColorVal(getPropSheetPath() + "color_grid");
	}

	public int getAsknBid() {
		return ask_bid;
	}

	public void setAsknBid(int asknbid) {
		this.ask_bid = asknbid;
	}

	public Dataset getDataset() {
		return dataset;
	}

	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}
	
	public Dataset getVisibleDataset() {
		synchronized (priceBarList) {
			return dataset == null || dataset.getSize() == 0 ? null :
				dataset.subDataset(dataset.getSize() - overlayPane.getAxisXVisibleBars(), dataset.getSize());
		}
	}
	
//	public List<TPosition> getVisiblePositins() {
//		
//	}
	
	public Date getChartEndDate() {
		return chartEndDate;
	}

	public void setChartEndDate(Date chartEndDate) {
		this.chartEndDate = chartEndDate;
	}

	public Date getPanelBeginDate() {
		return panelBeginDate;
	}
	
//	private Date getPanelBeginDate(int offset) {
//		Date beginDate = chartEndDate;
//		synchronized (priceBarList) {
//			int count = 0;
//			if (offset > 0) {
//				for (int i = priceBarList.size() - 1; i >= 0; i--) {
//					TPriceBar priceBar = priceBarList.get(i);
//					if (priceBar.getStartDate().compareTo(beginDate) < 0) {
//						count++;
//						if (count == offset) {
//							beginDate = priceBar.getStartDate();
//							break;
//						}
//					}
//				}
//			} else if (offset < 0) {
//				offset = Math.abs(offset);
//				for (int i = 0 ; i < priceBarList.size() - 1; i--) {
//					TPriceBar priceBar = priceBarList.get(i);
//					if (priceBar.getStartDate().compareTo(beginDate) > 0) {
//						count++;
//						if (count == offset) {
//							beginDate = priceBar.getStartDate();
//							break;
//						}
//					}
//				}	
//			}
//		}
//		return beginDate;
//	}
		
//	public int getVisibleFromPos() {
//		int periods = getShowPeriods();
//		List<DataItem> itemList = dataset.getDataItems();
//		int pos = itemList.size() - 1;
//		for (int count = 0; pos >= 0; pos--) {
//			if (chartEndDate.compareTo(itemList.get(pos).getDate()) >= 0) {
//				count++;
//				if (count >= periods) {
//					break;
//				}
//			}
//		}
//		return pos;
//	}

	public ChartSchema getChartSchema() {
		return chartSchema;
	}

	public void setChartSchema(ChartSchema chartSchema) {
		this.chartSchema = chartSchema;
	}

	public String getSymbol() {
		return symbol;
	}
	
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Interval getInterval() {
		return interval;
	}

	public void setInterval(Interval interval) {
		this.interval = interval;
	}

	public OverlayPane getOverlayPane() {
		return overlayPane;
	}

	public void setOverlayPane(OverlayPane overlayPane) {
		this.overlayPane = overlayPane;
	}

	public List<IndicatorPane> getIndicatorPaneList() {
		return indicatorPaneList;
	}

	public void setIndicatorPaneList(List<IndicatorPane> indicatorPaneList) {
		this.indicatorPaneList = indicatorPaneList;
	}
	
	public void addIndicator(PropertySheet propSheet) {
		if (propSheet == null) {
			return;
		}		
		if (propSheet.getName() == TemplateManager.OVERLAY_NODE) {
			overlayPane.addIndicator(propSheet);
			overlayPane.rePosIndicatorLabel();
			overlayPane.update();
			overlayPane.repaint();
		} else if(propSheet.getName() == TemplateManager.INDICATOR_NODE) {
			final IndicatorPane indicatorPane = new IndicatorPane(this, propSheet);
			addIndicatorPane(indicatorPane);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					indicatorPane.update();
					indicatorPane.repaint();					
				}
			});
		}
		PropertyManager.getInstance().addChartPropSheet(chartSchema.toString(), propSheet);
	}
	
	public void moveCrossY(Point crossPos) {
		overlayPane.getCrossPoint().x = crossPos.x;
		for (IndicatorPane indicatorPane : indicatorPaneList) {
			indicatorPane.getCrossPoint().x = crossPos.x;
		}
	}
	
	public void setAutoScale(boolean autoScale) {
		overlayPane.setAutoScale(autoScale);
		for (IndicatorPane indicatorPane : indicatorPaneList) {
			indicatorPane.setAutoScale(autoScale);
		}
	}
	
	public boolean isChartBegin() {
		return false;
	}
	
	public boolean isChartEnd() {
		if (chartEndDate.getTime() + interval.getMilliSecond()
				>= getTradeDesk().getServerTime().getTime()) {
			return true;
		} else {
			return false;
		}
	}
	
	public Date shift(int shiftBars) {
		Date endDate = null;		
		if (shiftBars > 0) {
			//Move to the left
			TPriceBar tail = loadPriceBarFromDesk(chartEndDate, shiftBars);
			if (tail != null) {
				endDate = tail.getStartDate();
			}
		} else if (shiftBars < 0) {
			//Move to the right
			shiftBars = Math.abs(shiftBars);
			synchronized (priceBarList) {
				if (shiftBars < priceBarList.size()) {
					endDate = priceBarList.get(priceBarList.size() - 1 - shiftBars).getStartDate();
				}
			}
			if (endDate != null) {
				loadPriceBarFromDesk(endDate, 0);
			}
		}
		if (endDate != null) {
			chartEndDate = endDate;
		}
		return endDate;
	}
	
	//Move to endDate
	public Date shift(Date endDate) {
		TPriceBar tail = loadPriceBarFromDesk(endDate, 0);
		if (tail == null) {
			return null;
		} else {
			chartEndDate = tail.getStartDate();
			return chartEndDate;
		}
	}
	
	public void zoom(double factor) { 
		axisXScale += factor;
		loadPriceBarFromDesk(chartEndDate, 0);
	}
	
	public void refresh() {
    	overlayPane.update();
    	for (IndicatorPane indicatorPane : indicatorPaneList) {
    		indicatorPane.update();
    	}    	    	
    	repaint();
	}

	@Override
	public void onClose() {
		getTradeDesk().getPriceBars().unsubscribe(this, SignalType.ADD);
		getTradeDesk().getPriceBars().unsubscribe(this, SignalType.CHANGE);
		mMainFrame.getBenchPanel().removeChartPanel(this);
		PropertyManager.getInstance().removeChartPropSheet(chartSchema.toString());
	}
	
	@Override
	//come from PriceBars
	public void onSignal(Signaler src, Signal signal) {
		if (signal == null) {
    		return;
    	}
		TPriceBar priceBar = (TPriceBar)signal.getElement();
		if (!priceBar.getSymbol().equals(symbol) || priceBar.getInterval() != interval) {
			return;
		}
		
    	if (signal.getType() == SignalType.ADD) {
    		if (!isLockView) {
    			if (chartEndDate == null || priceBar.getStartDate().compareTo(chartEndDate) > 0) {
    				chartEndDate = priceBar.getStartDate();
//    				chartToolbar.getPrevButton().setEnabled(!isChartBegin());
//    				chartToolbar.getNextButton().setEnabled(!isChartEnd());
    			}
    		}
    		loadPriceBarFromDesk(chartEndDate, 0);
            
    	} else if (signal.getType() == SignalType.CHANGE) {
    		synchronized (priceBarList) {
    			if (priceBarList.size() > 0) {
    				TPriceBar tail = priceBarList.get(priceBarList.size() - 1);
    				if (priceBar.getStartDate().getTime() <= tail.getStartDate().getTime() + interval.getMilliSecond()) {
    					priceBarList.set(priceBarList.size() - 1, priceBar);
		    			if (dataset != null) {
		    				if (ask_bid == TPriceBar.PRICE_ASK) {
		    					dataset.setDataItem(DataItem.valueOf(priceBar));
		    				} else {
		    					dataset.setDataItem(DataItem.valueOfB(priceBar));
		    				}
		    			}
    				}
    			}
    		}
			if (ask_bid == TPriceBar.PRICE_ASK) {
				overlayPane.getRateMarker().setVal(Double.valueOf(priceBar.getAskClose()));
			} else if (ask_bid == TPriceBar.PRICE_BID) {
				overlayPane.getRateMarker().setVal(Double.valueOf(priceBar.getBidClose()));
			}
		}
    	
    	refresh();
	}

	private class ChartToolBar extends JToolBar
	{
		private ChartPanel chartPanel;
		private JComboBox symbolComboBox;
		private JComboBox intervalComboBox;
		private ToolBarButton askButton;
		private ToolBarButton bidButton;
		private JButton prevButton;
		private JButton nextButton;
		
		public ChartToolBar(ChartPanel panel) {
			chartPanel = panel;
			setLayout(new FlowLayout(FlowLayout.LEFT));
			setFloatable(false);
			setBorder(new EtchedBorder());
			
			//symbol ComboBox
			symbolComboBox = new JComboBox();
			symbolComboBox.setFocusable(true);
			symbolComboBox.setModel(new DefaultComboBoxModel(getTradeDesk().getOffers().getSymbolList()));
			symbolComboBox.setSelectedItem(chartPanel.getSymbol());
			Dimension symbolDimension = symbolComboBox.getPreferredSize();
			symbolComboBox.setPreferredSize(new Dimension(symbolDimension.width + 5, symbolDimension.height));
			symbolComboBox.addItemListener(new ItemListener() {
	            public void itemStateChanged(ItemEvent itemEvent) {
	            	chartSchemaChanged((String)itemEvent.getItem(), chartPanel.getInterval());
	            }
	        });
			add(symbolComboBox);
			
			//interval ComboBox
			intervalComboBox = new JComboBox();
			intervalComboBox.setFocusable(true);
			intervalComboBox.setModel(new DefaultComboBoxModel(TPriceBar.SHOW_INTERVAL_LIST));
			intervalComboBox.setSelectedItem(chartPanel.getInterval().name());
			Dimension intervalDimension = intervalComboBox.getPreferredSize();
			intervalComboBox.setPreferredSize(new Dimension(intervalDimension.width + 5, intervalDimension.height));
			intervalComboBox.addItemListener(new ItemListener() {
	            public void itemStateChanged(ItemEvent itemEvent) {
	            	chartSchemaChanged(chartPanel.getSymbol(), Interval.valueOf((String)itemEvent.getItem()));
	            }
	        });
			add(intervalComboBox);
			
			addSeparator();
			
			//ask button
			Action askAction = (Action)new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					askButton.setBorderPainted(true);
	            	bidButton.setBorderPainted(false);
	            	if (chartPanel.getAsknBid() != TPriceBar.PRICE_ASK) {
	            		chartPanel.setAsknBid(TPriceBar.PRICE_ASK);
	            	}
				}
	        };
	        UIManager.getInst().addAction(askAction, "Ask", null, null, "Ask", "Ask");
			askButton = new ToolBarButton(askAction);
			if (chartPanel.getAsknBid() == TPriceBar.PRICE_ASK) {
				askButton.setBorderPainted(true);
			}
			add(askButton);
			
			//ask button
			Action bidAction = (Action)new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					askButton.setBorderPainted(false);
	            	bidButton.setBorderPainted(true);
	            	if (chartPanel.getAsknBid() != TPriceBar.PRICE_BID) {
	            		chartPanel.setAsknBid(TPriceBar.PRICE_BID);
	            	}
				}
	        };
	        UIManager.getInst().addAction(bidAction, "Bid", null, null, "Bid", "Bid");
			bidButton = new ToolBarButton(bidAction);
			if (chartPanel.getAsknBid() == TPriceBar.PRICE_BID) {
				bidButton.setBorderPainted(true);
			}
			add(bidButton);
			
			addSeparator();
			
			//new indicator button
	        Action newIndicatorAction = (Action)new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					IndicatorSelectDialog.createAndShowDialog(chartPanel);
				}
	        };
	        UIManager.getInst().addAction(newIndicatorAction,
	                            "IDS_NEW_INDICATOR_TEXT",
	                            "ID_NEW_INDICATOR_ICON",
	                            null,
	                            "IDS_NEW_INDICATOR_DESC",
	                            "IDS_NEW_INDICATOR_DESC");
	        JButton newIndicatorButton = UIManager.getInst().createButton(newIndicatorAction);
	        add(newIndicatorButton);
	        
	        //prev button
	        Action prevAction = (Action)new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					chartPanel.setAutoScale(true);
					chartPanel.shift(getShiftFrameBars() * SHIFT_PREV);
//					prevButton.setEnabled(!chartPanel.isChartBegin());
//					nextButton.setEnabled(true);
					chartPanel.refresh();
				}
	        };
	        UIManager.getInst().addAction(prevAction,
	                            "IDS_CHART_PREV_TEXT",
	                            "ID_CHART_PREV_ICON",
	                            null,
	                            "IDS_CHART_PREV_DESC",
	                            "IDS_CHART_PREV_DESC");
	        prevButton = UIManager.getInst().createButton(prevAction);
	        add(prevButton);
	        
	        //next button
	        Action nextAction = (Action)new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					chartPanel.setAutoScale(true);
					chartPanel.shift(getShiftFrameBars() * SHIFT_NEXT);
//					prevButton.setEnabled(true);
//					nextButton.setEnabled(!chartPanel.isChartEnd());
					chartPanel.refresh();
				}
	        };
	        UIManager.getInst().addAction(nextAction,
	                            "IDS_CHART_NEXT_TEXT",
	                            "ID_CHART_NEXT_ICON",
	                            null,
	                            "IDS_CHART_NEXT_DESC",
	                            "IDS_CHART_NEXT_DESC");
	        nextButton = UIManager.getInst().createButton(nextAction);
	        add(nextButton);
		}
		
		private void chartSchemaChanged(String symbol, Interval interval) {
        	ChartSchema newChartSchema = PropertyManager.getInstance().replaceChartSchema(
        			chartPanel.getChartSchema().toString(), symbol, interval.name());
        	if (newChartSchema != null) {
        		chartPanel.setChartSchema(newChartSchema);
        		chartPanel.setSymbol(symbol);
        		chartPanel.setInterval(interval);
        		chartPanel.setTitle(chartPanel.title());
        		chartPanel.setTooltip(chartPanel.tooltip());
        		chartPanel.clear();
            	TPriceBar tail = loadPriceBarFromDesk(getTradeDesk().getServerTime(), 0);
            	if (tail != null) {
            		chartEndDate = tail.getStartDate();
            		refresh();
            	} else {
            		chartEndDate = getTradeDesk().getTradingServerSession().getOrginChartEndDate();	
            	}
        	}
		}
		
		public JButton getPrevButton() {
			return prevButton;
		}
		
		public JButton getNextButton() {
			return nextButton;
		}
	}
	
	public class ChartPopupMenu extends JPopupMenu {
		private Point clickPosition;
		
		public ChartPopupMenu() {
			JMenuItem chgIndicatorItem = UIManager.getInst().createMenuItem(
					"Change indicator", null, null, "Change indicator");
			chgIndicatorItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent aE) {
	            	Indicator indicator = null;
	            	Component invoker = getInvoker();
	            	if (invoker instanceof OverlayPane) {
	            		indicator = overlayPane.getIndicator(clickPosition);
	            	} else if (invoker instanceof IndicatorPane) {
	            		IndicatorPane indicatorPane = (IndicatorPane)invoker;
	            		if (indicatorPane.getIndicator().getLabelBounds().contains(clickPosition)) {
	            			indicator = indicatorPane.getIndicator();
	            		}
	            	}
	            	if (indicator != null) {
	            		IndicatorChangeDialog.createAndShowDialog(indicator);
	            	}
	            }
	        });
			add(chgIndicatorItem);
		
			JMenuItem delIndicatorItem = UIManager.getInst().createMenuItem(
					"Remove indicator", null, null, "Remove indicator");
			delIndicatorItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent aE) {
	            	Indicator indicator = null;
	            	Component invoker = getInvoker();
	            	if (invoker instanceof OverlayPane) {
	            		indicator = overlayPane.getIndicator(clickPosition);
	            		if (indicator != null) {
	            			overlayPane.removeIndicator(indicator);
	            			overlayPane.rePosIndicatorLabel();
	            			overlayPane.repaint();
	            		}
	            	} else if (invoker instanceof IndicatorPane) {
	            		IndicatorPane indicatorPane = (IndicatorPane)invoker;
	            		indicator = indicatorPane.getIndicator();
	            		if (indicator.getLabelBounds().contains(clickPosition)) {
	            			removeIndicatorPane(indicatorPane);
	            			repaint();
	            		}
	            	}
	            	if (indicator != null) {
	            		PropertyManager.getInstance().removeChartPropSheet(
	            				ChartPanel.this.getChartSchema().toString(), indicator.getPropertySheet());
	            	}
	            }
	        });
			add(delIndicatorItem);
		}

		public void setClickPosition(Point clickPosition) {
			this.clickPosition = clickPosition;
		}				
	}
}
