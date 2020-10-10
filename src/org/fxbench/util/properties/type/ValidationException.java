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
package org.fxbench.util.properties.type;

import org.fxbench.util.ResourceManager;

public class ValidationException extends Exception
{
    private String mResourcePair;

    private ValidationException() {
    }

    public ValidationException(String aResourcePair) {
        super(aResourcePair);
        mResourcePair = aResourcePair;
    }

    public String getLocalizedMessage(ResourceManager resourceManager) {
        try {
        	return resourceManager.getString(mResourcePair);
        } catch (Exception e) {
            e.printStackTrace();
            return getMessage() + " (Localization is not available)";
        }
    }
}