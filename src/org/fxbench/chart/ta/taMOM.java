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
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;

import java.awt.Graphics2D;

import org.fxbench.chart.DataItem.PriceType;
import org.fxbench.chart.ta.Indicator;
import org.fxbench.chart.Dataset;
import org.fxbench.chart.DefaultPainter;
import org.fxbench.chart.Range;

/**
 *
 * @author joshua.taylor
 */
public class taMOM extends Indicator
{
    private static String NAME = "MOM";

    //variables for TA-Lib utilization
    private double[] real;

    @Override
    public String getName() {
    	return NAME;
    }

    @Override
    public String getLabel() {
    	return getName() + "(" + getPirceType()
    			+ ", " + getPeriod() + ")";
    }

    @Override
	public String getValueFormat() {
		return "0.#####";
	}
    
    @Override
	public Double getZoomFactor() {
		return 0.01D;
	}
    
    @Override
	public Range getRangeY() {
		return isCalculated() ? Range.valueOf(real) : null;
	}
    
    @Override
    public void draw(Graphics2D g) {
    	if (!isCalculated() || indicatorPane.getRangeY() == null) {
    		return;
    	}
    	
    	//Draw a label
    	g.setFont(getLabelFont());
    	g.setColor(getColor()); 
    	g.drawString(getLabel(), labelBounds.x, labelBounds.y + labelBounds.height);
    	
    	//Draw a curve
   		DefaultPainter.line(
				g, real, indicatorPane.getOriginPoint(), indicatorPane.getRangeY(),
				indicatorPane.getAxisXScale(), indicatorPane.getAxisYScale(), getColor());
    }
    
    @Override
    public boolean isCalculated() {
    	if (real == null || real.length == 0) {
    		return false;
    	} else {
    		return true;
    	}
    }

    @Override
    public void calculate() {
        Dataset dataset = indicatorPane.getChartPanel().getDataset();
        if (dataset == null || dataset.getSize() == 0) {
        	real = null;
        	return;
        }
        
        int count = dataset.getSize();
        real = new double[count];
        
        /**********************************************************************/
        //This entire method is basically a copy/paste action into your own
        //code. The only thing you have to change is the next few lines of code.
        //Choose the 'lookback' method and appropriate 'calculation function'
        //from TA-Lib for your needs. Everything else should stay basically the
        //same

        //prepare ta-lib variables
        MInteger outBegIdx = new MInteger();
        MInteger outNbElement = new MInteger();
        int period = getPeriod();
        PriceType priceType = PriceType.valueOf(getPirceType());
        double[] vals = dataset.getPrices(priceType);
        
        //now do the calculation over the entire dataset
        //[First, perform the lookback call if one exists]
        //[Second, do the calculation call from TA-lib]
        Core core = TaLib.getCore();//needs to be here for serialization issues
        int lookback = core.momLookback(period);
        core.mom(0, count - 1, vals, period, outBegIdx, outNbElement, real);

        //fix the output array's structure. TA-Lib does NOT match
        //indicator index and dataset index automatically. That's what
        //this function does for us.
        real = TaLib.fixOutputArray(real, lookback);
    }
    
    @Override
    public void shift() {
    	if (isCalculated()) {
    		real = indicatorPane.getVisibleReal(real);
    	}
    }
}

