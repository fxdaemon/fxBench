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
package org.fxbench.trader.dialog.component;

/**
 * An exception that is raised by Amount Combo box when user enter value being lower of long value negative limit.<br>
 * <br>
 * .<br>
 * <br>
 *
 * @Creation date (11/10/2003 1:04 PM)
 */
public class LowerThenMinLongException extends OutOfLongException {
    /**
     * Default constructor.
     */
    public LowerThenMinLongException() {
    }

    /**
     * Constructor.
     *
     * @param aMsg error message
     */
    public LowerThenMinLongException(String aMsg) {
        super(aMsg);
    }

    /**
     * Always returns false
     */
    public boolean isBigger() {
        return false;
    }

    /**
     * Allways returns true
     */
    public boolean isLower() {
        return true;
    }
}
