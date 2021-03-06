/*
 * Copyright 2008 the original author or authors.
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

package com.google.code.profiling.timer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A timer stack.
 * <p>
 * Usage:
 * 
 * <pre>
 * String logMessage = &quot;Log message&quot;;
 * UtilTimerStack.push(logMessage);
 * try {
 *     //do some code
 * } finally {
 *     UtilTimerStack.pop(logMessage); //this needs to be the same text as above
 * }
 * </pre>
 */
public class UtilTimerStack {

    private static final Log log = LogFactory.getLog(UtilTimerStack.class);

    // A reference to the current ProfilingTimerBean
    private static final ThreadLocal current = new ThreadLocal();

    private long minTime;

    public UtilTimerStack(long minTime) {
	this.minTime = minTime;
    }

    public void push(String name) {
	// create a new timer and start it
	ProfilingTimerBean newTimer = new ProfilingTimerBean(name);
	newTimer.setStartTime();

	// if there is a current timer - add the new timer as a child of it
	ProfilingTimerBean currentTimer = (ProfilingTimerBean) current.get();
	if (currentTimer != null) {
	    currentTimer.addChild(newTimer);
	}

	// set the new timer to be the current timer
	current.set(newTimer);
    }

    public void pop(String name) {
	ProfilingTimerBean currentTimer = (ProfilingTimerBean) current.get();

	// if the timers are matched up with each other (ie push("a"); pop("a"));
	if (currentTimer != null && name != null && name.equals(currentTimer.getResource())) {
	    currentTimer.setEndTime();
	    ProfilingTimerBean parent = currentTimer.getParent();
	    // if we are the root timer, then print out the times
	    if (parent == null) {
		print(currentTimer);
		current.remove(); // for those servers that use thread pooling
	    } else {
		current.set(parent);
	    }
	} else {
	    // if timers are not matched up, then print what we have, and then
	    // print warning.
	    if (currentTimer != null) {
		print(currentTimer);
		current.remove(); // prevent printing multiple times
		log.warn("Unmatched Timer.  Was expecting " + currentTimer.getResource() + ", instead got " + name);
	    }
	}
    }

    private void print(ProfilingTimerBean currentTimer) {
	currentTimer.print(this.minTime);
    }
}
