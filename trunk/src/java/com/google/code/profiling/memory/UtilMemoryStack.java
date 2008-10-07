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

package com.google.code.profiling.memory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A memory stack.
 * <p>
 * Usage:
 * 
 * <pre>
 * String logMessage = &quot;Log message&quot;;
 * UtilMemoryStack.push(logMessage);
 * try {
 *     //do some code
 * } finally {
 *     UtilMemoryStack.pop(logMessage); //this needs to be the same text as above
 * }
 * </pre>
 */
public class UtilMemoryStack {

    private static final Log log = LogFactory.getLog(UtilMemoryStack.class);

    // A reference to the current ProfilingMemoryBean
    private static final ThreadLocal current = new ThreadLocal();

    private long minMemory;

    public UtilMemoryStack(long minMemory) {
	this.minMemory = minMemory;
    }

    public void push(String name) {
	// create a new timer and start it
	ProfilingMemoryBean newMemory = new ProfilingMemoryBean(name);
	newMemory.setStartMemory();

	// if there is a current memory - add the new timer as a child of it
	ProfilingMemoryBean currentMemory = (ProfilingMemoryBean) current.get();
	if (currentMemory != null) {
	    currentMemory.addChild(newMemory);
	}

	// set the new memory to be the current memory
	current.set(currentMemory);
    }

    public void pop(String name) {
	ProfilingMemoryBean currentMemory = (ProfilingMemoryBean) current.get();

	// if the memories are matched up with each other (ie push("a"); pop("a"));
	if (currentMemory != null && name != null && name.equals(currentMemory.getResource())) {
	    currentMemory.setEndMemory();
	    ProfilingMemoryBean parent = currentMemory.getParent();
	    // if we are the root memory, then print out the memories
	    if (parent == null) {
		printMemory(currentMemory);
		current.remove(); // for those servers that use thread pooling
	    } else {
		current.set(parent);
	    }
	} else {
	    // if memories are not matched up, then print what we have, and then print warning.
	    if (currentMemory != null) {
		printMemory(currentMemory);
		current.remove(); // prevent printing multiple times
		log.warn("Unmatched Memory.  Was expecting " + currentMemory.getResource() + ", instead got " + name);
	    }
	}
    }

    private void printMemory(ProfilingMemoryBean currentMemory) {
	currentMemory.print(this.minMemory);
    }
}
