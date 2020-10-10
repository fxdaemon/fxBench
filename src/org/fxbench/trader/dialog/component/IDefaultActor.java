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
 * Interface is used by dialog control to change enabling status of parent dialog.
 * <br>
 *
 * @Creation date (10/3/2003 11:17 AM)
 */
public interface IDefaultActor {
    /* Enables/disbles parent dialog. */
    void setEnabled(boolean abEnabled);
}
