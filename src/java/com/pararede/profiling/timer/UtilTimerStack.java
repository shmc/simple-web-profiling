/**
 * Atlassian Source Code Template.
 * User: Scott Farquhar
 * Date: Feb 19, 2003
 * Time: 6:56:26 PM
 * CVS Revision: $Revision: 1.3 $
 * Last CVS Commit: $Date: 2003/05/07 06:39:26 $
 * Author of last CVS Commit: $Author: scott $
 *
 * @author <a href="mailto:scott@atlassian.com">Scott Farquhar</a>
 */
package com.pararede.profiling.timer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pararede.profiling.memory.UtilMemoryStack;

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

    private static final Log log = LogFactory.getLog(UtilMemoryStack.class);

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
	if ((currentTimer != null) && (name != null) && name.equals(currentTimer.getResource())) {
	    currentTimer.setEndTime();
	    ProfilingTimerBean parent = currentTimer.getParent();
	    // if we are the root timer, then print out the times
	    if (parent == null) {
		printTimes(currentTimer);
		current.set(null); // for those servers that use thread pooling
	    } else {
		current.set(parent);
	    }
	} else {
	    // if timers are not matched up, then print what we have, and then print warning.
	    if (currentTimer != null) {
		printTimes(currentTimer);
		current.set(null); // prevent printing multiple times
		log.warn("Unmatched Timer.  Was expecting " + currentTimer.getResource() + ", instead got " + name);
	    }
	}
    }

    private void printTimes(ProfilingTimerBean currentTimer) {
	log.info(currentTimer.getPrintable(this.minTime));
    }
}
