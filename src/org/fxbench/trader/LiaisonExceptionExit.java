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
package org.fxbench.trader;

/**
 * Class LiaisonException.<br>
 * <br>
 * This class represents liaison specific exception.
 * It allows to encapsulate original error info and description message
 * which can be shown to the user.<br>
 * <br>
 *
 * @Creation date (9/3/2003 5:38 PM)
 */
public class LiaisonExceptionExit extends LiaisonException {
    /**
     * Constructor.
     *
     * @param aSource
     * @param asLocalizedKey
     * @param asDefaultMessage
     */
    public LiaisonExceptionExit(Throwable aSource,
                                String asLocalizedKey) {
        super(aSource, asLocalizedKey);
    }
}
