# Introduction #

The are the steps needed to configure the profiling filters for in a JEE web application.

# Details #

The configuration is done solely in the filter section of your web.xml file.

Using Servlet 2.4 it is possible (and recommended) to configure the filters to the different dispatcher methods to allow the analysis of all kinds of requests.

Since the two different kinds of analysis are configured separately it is possible to turn on timer profiling and not memory profiling and vice-versa.

## Timer ##

```
   <filter>
      <filter-name>profiling</filter-name>
      <filter-class>com.google.code.profiling.filters.ProfilingTimerFilter</filter-class>
         <init-param>
             <param-name>min.time</param-name>
             <param-value>0</param-value>
         </init-param>
  </filter>
  <filter-mapping>
      <filter-name>profiling</filter-name>
      <url-pattern>/*</url-pattern>
  </filter-mapping>
```


The min.memory (in ms) parameter is a threshold to configure filter to show only the requests that consumed more than that amount.

## Memory ##

```
   <filter>
      <filter-name>profiling</filter-name>
      <filter-class>com.google.code.profiling.filters.ProfilingMemoryFilter</filter-class>
         <init-param>
             <param-name>min.memory</param-name>
             <param-value>0</param-value>
         </init-param>
  </filter>
  <filter-mapping>
      <filter-name>profiling</filter-name>
      <url-pattern>/*</url-pattern>
  </filter-mapping>
```

The min.memory (in KB) parameter is a threshold to configure filter to show only the requests that consumed more than that amount.