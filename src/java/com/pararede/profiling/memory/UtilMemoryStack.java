package com.pararede.profiling.memory;

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
	if ((currentMemory != null) && (name != null) && name.equals(currentMemory.getResource())) {
	    currentMemory.setEndMemory();
	    ProfilingMemoryBean parent = currentMemory.getParent();
	    // if we are the root memory, then print out the memories
	    if (parent == null) {
		printMemory(currentMemory);
		current.set(null); // for those servers that use thread pooling
	    } else {
		current.set(parent);
	    }
	} else {
	    // if memories are not matched up, then print what we have, and then print warning.
	    if (currentMemory != null) {
		printMemory(currentMemory);
		current.set(null); // prevent printing multiple times
		log.warn("Unmatched Memory.  Was expecting " + currentMemory.getResource() + ", instead got " + name);
	    }
	}
    }

    private void printMemory(ProfilingMemoryBean currentMemory) {
	log.info(currentMemory.getPrintable(this.minMemory));
    }
}
