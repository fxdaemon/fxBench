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
package org.fxbench.chart.ta;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fxbench.chart.IndicatorPane;
import org.fxbench.chart.OverlayPane;
import org.fxbench.chart.Range;
import org.fxbench.trader.local.DBAccess;
import org.fxbench.ui.auxi.UIManager;
import org.fxbench.util.SerialVersion;
import org.fxbench.util.properties.PropertySheet;

import com.tictactec.ta.lib.MAType;

/**
 *
 * @author viorel.gheba
 */
public abstract class Indicator
{
	private final static Log logger = LogFactory.getLog(DBAccess.class);
	
    private static final long serialVersionUID = SerialVersion.APPVERSION;
    public static final int TA_CALCULATE_SIZE = 480;
    
    protected OverlayPane overlayPane;
    protected IndicatorPane indicatorPane;
    protected PropertySheet propSheet;
    protected Rectangle labelBounds;
    
    public Indicator() {
    	labelBounds = new Rectangle();
    }
    
    public void setPane(OverlayPane overlayPane) {
    	this.overlayPane = overlayPane;
    	this.labelBounds.setLocation(overlayPane.getIndicatorLabelOrginPos());
        this.indicatorPane = null;
    }
    
    public void setPane(IndicatorPane indicatorPane) {
    	this.overlayPane = null;
        this.indicatorPane = indicatorPane;
        this.labelBounds.setLocation(indicatorPane.getIndicatorLabelOrginPos());
    }
    
    private void initLabelSize() {
    	FontMetrics fm = UIManager.getInst().createLabel().getFontMetrics(getLabelFont());
    	labelBounds.height = fm.getHeight();
    	labelBounds.width = fm.stringWidth(getLabel());
    }
    
    public PropertySheet getPropertySheet() {
    	return propSheet;
    }
    
    public void setPropertySheet(PropertySheet propertySheet) {
    	this.propSheet = propertySheet;
    	initLabelSize();
    }
    
    public Rectangle getLabelBounds() {
    	return labelBounds;
    }
    
    public void update() {
    	calculate();
		shift();
    }
    
    public void repaint() {
    	if (overlayPane != null) {
    		overlayPane.rePosIndicatorLabel();
    		overlayPane.repaint();
    	} else if (indicatorPane != null) {
    		indicatorPane.repaint();
    	}
    }
    
    protected Font getLabelFont() {
    	return propSheet.getFontVal("font_lable");
    }
    
    protected int getPeriod() {
		return propSheet.getIntVal("period");		
	}
    
    protected int getPeriodK() {
		return propSheet.getIntVal("period_k");		
	}
	
	protected int getPeriodD() {
		return propSheet.getIntVal("period_d");		
	}
	
	protected int getPeriodSlowD() {
		return propSheet.getIntVal("period_slow_d");		
	}
	
	protected int getPeriodFast() {
		return propSheet.getIntVal("period_fast");		
	}
	
	protected int getPeriodSlow() {
		return propSheet.getIntVal("period_slow");		
	}
	
	protected int getPeriodSignal() {
		return propSheet.getIntVal("period_signal");		
	}
	
	protected String getSlowKType() {
		return propSheet.getStrVal("type_slow_k");		
	}

	protected String getSlowDType() {
		return propSheet.getStrVal("type_slow_d");		
	}
	
	protected Color getColor() {
		return propSheet.getColorVal("color");
	}
	
	protected Color getColorD() { 
		return propSheet.getColorVal("color_d");
	}
	
	protected Color getColorK() {
		return propSheet.getColorVal("color_k");
	}
	
	protected Color getColorMacd() {
		return propSheet.getColorVal("color_macd");
	}
	
	protected Color getColorSignal() {
		return propSheet.getColorVal("color_signal");
	}
	
	protected Color getColorHist() {
		return propSheet.getColorVal("color_hist");
	}
	
	protected String getPirceType() {
		return propSheet.getStrVal("pirce_type");
	}
	
	protected double getLevelSell() {
		return propSheet.getDblVal("level_sell");
	}
	
	protected double getLevelBuy() {
		return propSheet.getDblVal("level_buy");
	}
	
	protected Color getColorLevel() {
		return propSheet.getColorVal("color_level");
	}
	
	protected Color getColorBand() {
		return propSheet.getColorVal("color_band");
	}
	
	protected Color getColorAve() {
		return propSheet.getColorVal("color_ave");
	}
	
	protected double getNbDev() {
		return propSheet.getDblVal("nbdev");
	}
	
	protected double getStep() {
		return propSheet.getDblVal("step");
	}
	
	protected double getMax() {
		return propSheet.getDblVal("max");
	}
	
	protected String getDevType() {
		return propSheet.getStrVal("type_dev");		
	}
	
	protected String getSlowType() {
		return propSheet.getStrVal("type_slow");		
	}
	
	protected MAType getMAType(String type) {
		if (type == "SMA") {
			return MAType.Sma;
		} else if (type == "EMA") {
			return MAType.Ema;
		} else if (type == "WMA") {
			return MAType.Wma;
		} else {
			return MAType.Sma;
		}
	}
	
	public static Indicator valueOf(PropertySheet propertySheet) {
    	String clsPath = "org.fxbench.chart.ta";
    	Indicator indicator = null;
    	try {
    		Class<?> cls = Class.forName(clsPath + ".ta" + propertySheet.getTitle());
    		indicator = (Indicator)cls.newInstance();
    		indicator.setPropertySheet(propertySheet);
        } catch (Throwable e) {
        	logger.error(e.getMessage());
        }
    	return indicator;
    }
	
    public abstract String getName();
    public abstract String getLabel();
    public abstract String getValueFormat();
    public abstract Double getZoomFactor();
    public abstract Range getRangeY();
    public abstract void draw(Graphics2D g);
    public abstract boolean isCalculated();
    public abstract void calculate();
    public abstract void shift();
}
