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

package com.google.code.profiling.filters;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.math.NumberUtils;

import com.google.code.profiling.timer.UtilTimerStack;

/**
 * <p>
 * Filter that will intercept requests &amp; time how long it takes for them to return. It stores
 * this information in the ProfilingTimerBean.
 * 
 * <p>
 * Install the filter in your web.xml file as follows:
 * 
 * <pre>
 *   &lt;filter&gt;
 *      &lt;filter-name&gt;profiling&lt;/filter-name&gt;
 *      &lt;filter-class&gt;com.google.code.profiling.filters.ProfilingTimerFilter&lt;/filter-class&gt;
 *      &lt;init-param&gt;
 *             &lt;param-name&gt;min.time&lt;/param-name&gt;
 *             &lt;param-value&gt;0&lt;/param-value&gt;
 *         &lt;/init-param&gt;
 *  &lt;/filter&gt;
 *  &lt;filter-mapping&gt;
 *      &lt;filter-name&gt;profiling&lt;/filter-name&gt;
 *      &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 *  &lt;/filter-mapping&gt;
 * </pre>
 */
public class ProfilingTimerFilter implements Filter {

    // This is the parameter you pass to the init parameter
    private static final String MIN_TIME_PARAM = "min.time";

    private UtilTimerStack utilTimerStack;

    /**
     * Check for parameters to turn the filter on or off. If parameters are given, change the
     * current state of the filter. If current state is off then pass to filter chain. If current
     * state is on - record start time, pass to filter chain, and then record total time on the
     * return.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws java.io.IOException, javax.servlet.ServletException {
	String resource = null;
	// if an include file then get the proper resource name.
	if (request.getAttribute("javax.servlet.include.request_uri") != null) {
	    resource = (String) request.getAttribute("javax.servlet.include.request_uri");
	} else {
	    resource = ((HttpServletRequest) request).getRequestURI();
	}

	this.utilTimerStack.push(resource);
	try {
	    // time and perform the request
	    chain.doFilter(request, response);
	} finally {
	    this.utilTimerStack.pop(resource);
	}

    }

    public void init(FilterConfig filterConfig) throws ServletException {
	long minTime = NumberUtils.toLong(filterConfig.getInitParameter(MIN_TIME_PARAM), 0);
	this.utilTimerStack = new UtilTimerStack(minTime);
    }

    public void destroy() {
	this.utilTimerStack = null;
    }
}
