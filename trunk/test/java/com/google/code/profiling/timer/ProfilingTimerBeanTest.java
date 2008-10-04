package com.google.code.profiling.timer;

import junit.framework.TestCase;

public class ProfilingTimerBeanTest extends TestCase {

	public void testPrintLong() throws InterruptedException {
		ProfilingTimerBean parentBean = new ProfilingTimerBean("parentResource");
		parentBean.setStartTime();

		ProfilingTimerBean childBean = new ProfilingTimerBean("childResource");
		childBean.setStartTime();
		
		// let the time pass
		Thread.sleep(1000);
		
		childBean.setEndTime();

		parentBean.setEndTime();
		parentBean.addChild(childBean);

		parentBean.print(100);
	}
}
