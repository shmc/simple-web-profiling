package com.google.code.profiling.timer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Bean to contain information about the pages profiled
 */
public class ProfilingTimerBean implements Serializable {

    private static final long serialVersionUID = -678451227367616598L;

    private static final Log log = LogFactory.getLog(ProfilingTimerBean.class);

    private List children = new ArrayList();
    private ProfilingTimerBean parent = null;

    private String resource;

    private long startTime;
    private long totalTime;

    public ProfilingTimerBean(String resource) {
	this.resource = resource;
    }

    protected void addParent(ProfilingTimerBean parent) {
	this.parent = parent;
    }

    public ProfilingTimerBean getParent() {
	return this.parent;
    }

    public void addChild(ProfilingTimerBean child) {
	this.children.add(child);
	child.addParent(this);
    }

    public void setStartTime() {
	this.startTime = System.currentTimeMillis();
    }

    public void setEndTime() {
	this.totalTime = System.currentTimeMillis() - this.startTime;
    }

    public String getResource() {
	return this.resource;
    }

    /**
     * Get a formatted string representing all the methods that took longer than a specified time.
     */
    public void print(long minTime) {
	print("", minTime);
    }

    protected void print(String indent, long minTime) {
	// only print the value if we are larger or equal to the min time.
	if (this.totalTime >= minTime) {
	    StringBuffer buffer = new StringBuffer();
	    buffer.append(indent);
	    buffer.append("[" + this.totalTime + " ms] - " + this.resource);
	    log.info(buffer.toString());

	    Iterator childrenIt = this.children.iterator();
	    while (childrenIt.hasNext()) {
		((ProfilingTimerBean) childrenIt.next()).print(indent + "  ", minTime);
	    }
	}
    }
}
