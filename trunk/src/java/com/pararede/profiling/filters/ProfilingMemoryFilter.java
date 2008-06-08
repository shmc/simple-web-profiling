package com.pararede.profiling.filters;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.math.NumberUtils;

import com.pararede.profiling.memory.UtilMemoryStack;

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
 *      &lt;filter-class&gt;com.pararede.profiling.filters.ProfilingMemoryFilter&lt;/filter-class&gt;
 *         &lt;init-param&gt;
 *             &lt;param-name&gt;min.memory&lt;/param-name&gt;
 *             &lt;param-value&gt;0&lt;/param-value&gt;
 *         &lt;/init-param&gt;
 *  &lt;/filter&gt;
 *  &lt;filter-mapping&gt;
 *      &lt;filter-name&gt;profiling&lt;/filter-name&gt;
 *      &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 *  &lt;/filter-mapping&gt;
 * </pre>
 * 
 * <p>
 * With the above settings you can turn the filter on by accessing any URL with the parameter
 * <code>profilingfilter=on</code>.eg:
 * 
 * <pre>
 *     http://mywebsite.com/a.jsp?&lt;b&gt;&lt;i&gt;profilingfilter=on&lt;/i&gt;&lt;/b&gt;
 * </pre>
 * 
 * <p>
 * The above settings also sets the filter to not start automatically upon startup. This may be
 * useful for production, but you will most likely want to set this true in development.
 * 
 * @author <a href="mailto:mike@atlassian.com">Mike Cannon-Brookes</a>
 * @author <a href="mailto:scott@atlassian.com">Scott Farquhar</a>
 */
public class ProfilingMemoryFilter implements Filter {

    // This is the parameter you pass to the init parameter
    private static final String MIN_MEMORY_PARAM = "min.memory";

    private UtilMemoryStack utilMemoryStack;

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

	this.utilMemoryStack.push(resource);
	try {
	    // memory and perform the request
	    chain.doFilter(request, response);
	} finally {
	    this.utilMemoryStack.pop(resource);
	}

    }

    public void setFilterConfig(FilterConfig filterConfig) {
    }

    public void init(FilterConfig filterConfig) throws ServletException {
	long minMemory = NumberUtils.toLong(filterConfig.getInitParameter(MIN_MEMORY_PARAM), 0);
	this.utilMemoryStack = new UtilMemoryStack(minMemory);
    }

    public void destroy() {
	this.utilMemoryStack = null;
    }
}
