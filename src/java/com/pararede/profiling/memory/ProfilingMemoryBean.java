package com.pararede.profiling.memory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProfilingMemoryBean implements Serializable {

    private static final long serialVersionUID = 3467875415909108916L;

    private List children = new ArrayList();
    private ProfilingMemoryBean parent = null;

    private String resource;

    private long startMemory;
    private long totalMemory;

    public ProfilingMemoryBean(String resource) {
	this.resource = resource;
    }

    protected void addParent(ProfilingMemoryBean parent) {
	this.parent = parent;
    }

    public ProfilingMemoryBean getParent() {
	return this.parent;
    }

    public void addChild(ProfilingMemoryBean child) {
	this.children.add(child);
	child.addParent(this);
    }

    public void setStartMemory() {
	this.startMemory = Runtime.getRuntime().freeMemory();
    }

    public void setEndMemory() {
	this.totalMemory = Runtime.getRuntime().freeMemory() - this.startMemory;
    }

    public String getResource() {
	return this.resource;
    }

    /**
     * Get a formatted string representing all the methods that took longer than a specified time.
     */
    public String getPrintable(long minMemory) {
	return getPrintable("", minMemory);
    }

    protected String getPrintable(String indent, long minMemory) {
	// only print the value if we are larger or equal to the min time.
	if (this.totalMemory >= minMemory) {
	    StringBuffer buffer = new StringBuffer();
	    buffer.append(indent);
	    buffer.append("[" + this.totalMemory + "KB] - " + this.resource);
	    buffer.append("\n");

	    Iterator childrenIt = this.children.iterator();
	    while (childrenIt.hasNext()) {
		buffer.append(((ProfilingMemoryBean) childrenIt.next()).getPrintable(indent + "  ", minMemory));
	    }

	    return buffer.toString();
	} else {
	    return "";
	}
    }
}
