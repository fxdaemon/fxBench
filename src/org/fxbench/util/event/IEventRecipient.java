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
package org.fxbench.util.event;

/**
 * IEventRecipient interface
 * It's interface of the event recipient.
 */
public interface IEventRecipient {
    /**
     * This method is called when the event for this recipient has been post.
     * The method is called in context of main dispatch thread.
     */
    public void onEvent(Event aEvent);
}