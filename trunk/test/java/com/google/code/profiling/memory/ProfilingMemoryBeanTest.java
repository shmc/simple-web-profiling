package com.google.code.profiling.memory;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class ProfilingMemoryBeanTest extends TestCase {

	public void testPrintLong() {
		ProfilingMemoryBean parentBean = new ProfilingMemoryBean("parentResource");
		parentBean.setStartMemory();

		ProfilingMemoryBean childBean = new ProfilingMemoryBean("childResource");
		childBean.setStartMemory();

		// ocupy memory
		List list = new ArrayList();
		for (int i = 0; i < 1000000; i++) {
			list.add(new Integer(0));
		}
		
		childBean.setEndMemory();

		parentBean.setEndMemory();
		parentBean.addChild(childBean);

		parentBean.print(-Integer.MIN_VALUE);
	}

}
