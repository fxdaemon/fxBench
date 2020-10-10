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

/**
 *
 * @author jtaylor
 */
public class TaLib {
    //needs to be initialized as early as possible to ensure serialization
    //concerns for objects in other modules that use this library and not
    //follow the pattern of delayed singleton initialization
    //that would normally be part of the "getCore()" method.
    //See "Effective Java 2nd edition," pgs. 17,18.
    private static final Core CORE = new Core();

    public static Core getCore(){return CORE;}
    private Object readResolve(){return CORE;}
    
    public static double[] fixOutputArray(double[] in, int lookback){
    	if (lookback >= in.length) {
    		return in;
    	} else {
	        double out[] = new double[in.length];
	        for (int i = 0; i < lookback; i++) {
	        	out[i] = 0D;
	        }
	        System.arraycopy(in, 0, out, lookback, in.length - lookback);
	
	        return out;
        }
    }

    public static double[] fixOutputArray(int[] outArray, int lookback){
        double tempOutput[] = new double[outArray.length];
        int j = 0;
        for (int i = 0; i < tempOutput.length; i++) {
            if(i<lookback)
                tempOutput[i] = 0.0;
            if(i>=lookback)
                tempOutput[i] = (double)outArray[j++];
        }
        
        return tempOutput;
    }

    public static void showOutputArray(double[] outputArray){
        System.out.println("The output array is as follows:");
        for (int i = 0; i < outputArray.length; i++) {
            System.out.println("outputArray[" + i + "]= " + outputArray[i]);
        }
    }

    public static void showLastElementsOfOutputArray(double[] outputArray, int numberOfElements){
        System.out.println("The last " + numberOfElements + " elements of the output array are as follows:");
        for (int i = 0; i < outputArray.length; i++) {
            if(i > outputArray.length - 1 - numberOfElements)
                System.out.println("outputArray[" + i + "]= " + outputArray[i]);
        }
    }

    public static void showArraysTogether(double[] input, double[] output){
    	System.out.println("\nHere's what the input and output arrays look like together:");
    	System.out.println("Input \tIndicator (output)");
    	for (int i = 0; i < output.length; i++) {
            System.out.println(input[i] + ",\t " + output[i]);
        }
    }

    public static void showLastElementsOfArraysTogether(double[] input, double[] output, int numberOfElements){
    	System.out.println("The last " + numberOfElements + " elements of both arrays are as follows:");
    	System.out.println("Input \tIndicator (output)");
        for (int i = 0; i < output.length; i++) {
            if(i > output.length - 1 - numberOfElements)
            	System.out.println(input[i] + ",\t " + output[i]);
        }
    }
}
