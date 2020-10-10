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

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import java.awt.Graphics2D;

import org.fxbench.chart.DataItem.PriceType;
import org.fxbench.chart.ta.Indicator;
import org.fxbench.chart.Dataset;
import org.fxbench.chart.DefaultPainter;
import org.fxbench.chart.Range;
import org.fxbench.util.SerialVersion;

/**
 *
 * @author joshua.taylor
 */
public class taMACD extends Indicator
{
    private static final long serialVersionUID = SerialVersion.APPVERSION;
    private static String NAME = "MACD";

    //variables for TA-Lib utilization
    private double[] outputMacd;
    private double[] outputSignal;
    private double[] outputHist;

    @Override
    public String getName() {
    	return NAME;
    }

    @Override
    public String getLabel() {
    	return getName() + "(" + getPirceType()
			+ ", " + getPeriodFast()
			+ ", " + getPeriodSlow()
			+ ", " + getPeriodSignal() + ")"; 
//			+ (isCalculated() ? 
//				"  MACD: " + outputMacd[outputMacd.length - 1] +
//				"  SIGNAL: " + outputSignal[outputSignal.length - 1] +
//				"  HISTOGRAM: " + outputHist[outputHist.length - 1]
//			: "");
    }

    @Override
	public String getValueFormat() {
    	return indicatorPane.getChartPanel().getOverlayPane().getAxisYValFormat();
	}
    
    @Override
	public Double getZoomFactor() {
		return indicatorPane.getChartPanel().getOverlayPane().getZoomYFactor();
	}
    
    @Override
	public Range getRangeY() {
    	return isCalculated() ?
    			Range.combine(
    					Range.valueOf(outputMacd),
    					Range.combine(Range.valueOf(outputSignal), Range.valueOf(outputHist)))
    			: null;
	}
    
    @Override
    public void draw(Graphics2D g) {
    	if (!isCalculated() || indicatorPane.getRangeY() == null) {
    		return;
    	}

    	g.setFont(getLabelFont());
    	g.setColor(getColorMacd()); 
    	g.drawString(getLabel(), labelBounds.x, labelBounds.y + labelBounds.height);
    	
    	DefaultPainter.histogram(
   				g, outputHist, indicatorPane.getOriginPoint(), indicatorPane.getRangeY(),
   				indicatorPane.getAxisXScale(), indicatorPane.getAxisYScale(), getColorHist(), getColorHist());
    	DefaultPainter.line(
   				g, outputMacd, indicatorPane.getOriginPoint(), indicatorPane.getRangeY(), 
   				indicatorPane.getAxisXScale(), indicatorPane.getAxisYScale(), getColorMacd());
   		DefaultPainter.line(
   				g, outputSignal, indicatorPane.getOriginPoint(), indicatorPane.getRangeY(),
   				indicatorPane.getAxisXScale(), indicatorPane.getAxisYScale(), getColorSignal());
    }

    @Override
    public boolean isCalculated() {
    	if (outputMacd == null || outputMacd.length == 0 ||
    		outputSignal == null || outputSignal.length == 0 ||
    		outputHist == null || outputHist.length == 0) {
    		return false;
    	} else {
    		return true;
    	}
    }

    @Override
    public void calculate() {
        Dataset dataset = indicatorPane.getChartPanel().getDataset();
        if (dataset == null || dataset.getSize() == 0) {
        	outputMacd = null;
        	outputSignal = null;
        	outputHist = null;
        	return;
        }
        
        int count = dataset.getSize();
        outputMacd = new double[count];
        outputSignal = new double[count];
        outputHist = new double[count];
        
        /**********************************************************************/
        //This entire method is basically a copy/paste action into your own
        //code. The only thing you have to change is the next few lines of code.
        //Choose the 'lookback' method and appropriate 'calculation function'
        //from TA-Lib for your needs. Everything else should stay basically the
        //same

        //prepare ta-lib variables
        MInteger outBegIdx = new MInteger();
        MInteger outNbElement = new MInteger();
        int periodFast = getPeriodFast();
        int periodSlow = getPeriodSlow();
        int periodSignal = getPeriodSignal();        
        PriceType priceType = PriceType.valueOf(getPirceType());

        double[] vals = dataset.getPrices(priceType);

        //now do the calculation over the entire dataset
        //[First, perform the lookback call if one exists]
        //[Second, do the calculation call from TA-lib]
        Core core = TaLib.getCore();//needs to be here for serialization issues
        int lookback = core.macdLookback(periodFast, periodSlow, periodSignal);
        core.macd(0, count-1, vals, periodFast, periodSlow, periodSignal, outBegIdx, outNbElement, outputMacd, outputSignal, outputHist);

        //fix the output array's structure. TA-Lib does NOT match
        //indicator index and dataset index automatically. That's what
        //this function does for us.
        outputMacd = TaLib.fixOutputArray(outputMacd, lookback);
        outputSignal = TaLib.fixOutputArray(outputSignal, lookback);
        outputHist = TaLib.fixOutputArray(outputHist, lookback);
    }
    
    @Override
    public void shift() {
    	if (isCalculated()) {
    		outputMacd = indicatorPane.getVisibleReal(outputMacd);
        	outputSignal = indicatorPane.getVisibleReal(outputSignal);
        	outputHist = indicatorPane.getVisibleReal(outputHist);
    	}
    }
}

