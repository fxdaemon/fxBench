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
package org.fxbench.chart;

import java.util.List;

import org.fxbench.util.SerialVersion;

/**
 *
 * @author viorel.gheba
 */
public strictfp class Range 
{
    private static final long serialVersionUID = SerialVersion.APPVERSION;

    private double lower;
    private double upper;

    public Range() {
        this.lower = Double.MAX_VALUE;
        this.upper = Double.MIN_VALUE;
    }

    public Range(double lower, double upper) {
        this.lower = Math.min(lower, upper);
        this.upper = Math.max(lower, upper);
    }

    public double getLowerBound() {
        return this.lower;
    }

    public double getUpperBound() {
        return this.upper;
    }

    public double getLength() {
    	return upper > lower ? Math.abs(upper - lower) : 0;
    }

    public boolean contains(double value) {
        return (value >= this.lower && value <= this.upper);
    }

    public boolean intersects(double b0, double b1) {
        if (b0 <= this.lower) {
            return (b1 > this.lower);
        } else {
            return (b0 <= this.upper && b1 >= b0);
        }
    }

    public boolean intersects(Range range) {
        return this.intersects(range.getLowerBound(), range.getUpperBound());
    }

    public double constrain(double value) {
        double result = value;
        if (!contains(value)) {
            if (value > this.upper) {
                result = this.upper;
            } else if (value < this.lower) {
                result = this.lower;
            }
        }
        return result;
    }

    public static Range combine(Range r1, Range r2) {
        if (r1 == null) {
            return r2;
        } else {
            if (r2 == null) {
                return r1;
            } else {
                double l = Math.min(r1.getLowerBound(), r2.getLowerBound());
                double u = Math.max(r1.getUpperBound(), r2.getUpperBound());
                return new Range(l, u);
            }
        }
    }

	public static Range combineNotZero(Range r1, Range r2)
	{
		if (r1 == null) {
			return r2;
		} else {
			if (r2 == null) {
				return r1;
			} else {
				if (r2.getLowerBound() > 0) {
					double l = Math.min(r1.getLowerBound(), r2.getLowerBound());
					double u = Math.max(r1.getUpperBound(), r2.getUpperBound());
					return new Range(
						Math.min(l, u),
						Math.max(l, u));
				} else {
					double l = r1.getLowerBound();
					double u = Math.max(r1.getUpperBound(), r2.getUpperBound());
					return new Range(
						Math.min(l, u),
						Math.max(l, u));
				}
			}
		}
	}

    public static Range expandToInclude(Range range, double value) {
        if (range == null) {
            return new Range(value, value);
        }
        if (value < range.getLowerBound()) {
            return new Range(value, range.getUpperBound());
        } else if (value > range.getUpperBound()) {
            return new Range(range.getLowerBound(), value);
        } else {
            return range;
        }
    }

    public static Range expand(Range range, double lowerMargin, double upperMargin) {
        if (range == null) {
            throw new IllegalArgumentException("Null 'range' argument.");
        }
        double length = range.getLength();
        double lower = range.getLowerBound() - length * lowerMargin;
        double upper = range.getUpperBound() + length * upperMargin;
        if (lower > upper) {
            lower = lower / 2.0 + upper / 2.0;
            upper = lower;
        }
        return new Range(lower, upper);
    }

    public static Range shift(Range base, double delta) {
        return shift(base, delta, false);
    }

    public static Range shift(Range base, double delta, boolean allowZeroCrossing) {
        if (base == null) {
            throw new IllegalArgumentException("Null 'base' argument.");
        }
        if (allowZeroCrossing) {
            return new Range(base.getLowerBound() + delta, base.getUpperBound() + delta);
        } else {
            return new Range(shiftWithNoZeroCrossing(base.getLowerBound(), delta), shiftWithNoZeroCrossing(base.getUpperBound(), delta));
        }
    }

    private static double shiftWithNoZeroCrossing(double value, double delta) {
        if (value > 0.0) {
            return Math.max(value + delta, 0.0);
        } else if (value < 0.0) {
            return Math.min(value + delta, 0.0);
        } else {
            return value + delta;
        }
    }

    public static Range scale(Range base, double factor) {
        if (base == null) {
            throw new IllegalArgumentException("Null 'base' argument.");
        }
        if (factor < 0) {
            throw new IllegalArgumentException("Negative 'factor' argument.");
        }
        return new Range(base.getLowerBound() * factor, base.getUpperBound() * factor);
    }

    public static Range valueOf(double[] data) {
    	double low = Double.MAX_VALUE;
		double high = 0;
		
		for (int i = 0; i < data.length; i++) {
			if (data[i] < low) {
				low = data[i];
			}
			if (data[i] > high) {
				high = data[i];
			}
		}    	

    	return new Range(low, high);
    }
    
    public static Range valueOf(Dataset dataset) {
    	double low = Double.MAX_VALUE;
		double high = 0;
		
		List<DataItem> dataItems = dataset.getDataItems();
		for (DataItem item : dataItems) {
			if (item.getLow() < low) {
				low = item.getLow();
			}
			if (item.getHigh() > high) {
				high = item.getHigh();
			}
		}
		
		return new Range(low, high);
    }
    
    public boolean equals(Object obj) {
        if (!(obj instanceof Range)) {
            return false;
        }
        Range range = (Range) obj;
        if (!(this.lower == range.lower)) {
            return false;
        }
        if (!(this.upper == range.upper)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(this.lower);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.upper);
        result = 29 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public String toString() {
        return ("Range[" + this.lower + "," + this.upper + "]");
    }

}
