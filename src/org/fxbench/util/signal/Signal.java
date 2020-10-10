/*
 * Copyright 2006 FXCM LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fxbench.util.signal;


/**
 * AddSignal class
 * This signal is sent when some element in the SignalVector has been added.
 */
public class Signal {
    /**
     * This signal type
     */
    public SignalType type;
    /**
     * Element value
     */
    private Object element;
    private Object newElement;
    private Object oldElement;
    /**
     * Index of added element
     */
    private int index;

    /**
     * Constructor
     *
     * @param aiIndex  index of added element.
     * @param aElement added element value.
     */
    public Signal(SignalType type, int index, Object element) {
    	this.type = type; 
        this.index = index;
        this.element = element;
    }
    
    public Signal(int index, Object newElement, Object oldElement) {
    	type = SignalType.CHANGE;
    	this.index = index;
    	this.element = newElement;
    	this.newElement = newElement;
    	this.oldElement = oldElement;
    }

    /**
     * Gets added element value.
     */
    public Object getElement() {
        return element;
    }

    /**
     * Gets new element value.
     */
    public Object getNewElement() {
        return newElement;
    }

    /**
     * Gets old element value.
     */
    public Object getOldElement() {
        return oldElement;
    }
    
    /**
     * Gets index where element was added.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Gets type of the signal.
     */
    public SignalType getType() {
        return type;
    }
    
    public static Signal newAddSignal(int index, Object element) {
    	return new Signal(SignalType.ADD, index, element);
    }
    
    public static Signal newChangeSignal(int index, Object newElement, Object oldElement) {
    	return new Signal(index, newElement, oldElement);
    }
    
    public static Signal newRemoveSignal(int index, Object element) {
    	return new Signal(SignalType.REMOVE, index, element);
    }
   
    public enum SignalType {
    	ADD, CHANGE, REMOVE;
    	public String getName() {
			return name();
		}
    	@Override
		public String toString() {
			return name();
		}
    }
}
