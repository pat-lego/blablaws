# Leaking Resource Resolve Example

In this example I provide a way to understand how a RR session leak can be created.

## Use Case 1 (Leaking RR)

When you execute the following code [1] the Thread for the class goes to [2] and this is where the issue lies in how the RR is clone and created. The ServletContext in this scenario is handled by [3]

[1]

```
request.getServletContext().getRequestDispatcher(String.class)
```

[2]

```
UriUtils.isEmpty(String) (Unknown Source:358)
UriUtils.removeDotSegments(String) (Unknown Source:199)
SharedServletContextImpl.getRequestDispatcher(String) (Unknown Source:351)
PerBundleServletContextImpl.getRequestDispatcher(String) (Unknown Source:216)
HWServlet.doGet(SlingHttpServletRequest,SlingHttpServletResponse) (/Users/patriquelegault/Desktop/blablaws/src/main/java/com/adobe/aem/support/core/servlet/HWServlet.java:47)
SlingSafeMethodsServlet.mayService(SlingHttpServletRequest,SlingHttpServletResponse) (/uber-jar-6.5.19.jar/org.apache.sling.api.servlets/SlingSafeMethodsServlet.class:81)
SlingSafeMethodsServlet.service(SlingHttpServletRequest,SlingHttpServletResponse) (/uber-jar-6.5.19.jar/org.apache.sling.api.servlets/SlingSafeMethodsServlet.class:105)
SlingSafeMethodsServlet.service(ServletRequest,ServletResponse) (/uber-jar-6.5.19.jar/org.apache.sling.api.servlets/SlingSafeMethodsServlet.class:114)
RequestData.service(SlingHttpServletRequest,SlingHttpServletResponse) (/org.apache.sling.engine-2.0.6.jar/org.apache.sling.engine.impl.request/RequestData.class:579)
SlingComponentFilterChain.render(SlingHttpServletRequest,SlingHttpServletResponse) (/org.apache.sling.engine-2.0.6.jar/org.apache.sling.engine.impl.filter/SlingComponentFilterChain.class:45)
AbstractSlingFilterChain.doFilter(ServletRequest,ServletResponse) (/org.apache.sling.engine-2.0.6.jar/org.apache.sling.engine.impl.filter/AbstractSlingFilterChain.class:88)
WCMDebugFilter.doFilter(ServletRequest,ServletResponse,FilterChain) (Unknown Source:156)
AbstractSlingFilterChain.doFilter(ServletRequest,ServletResponse) (/org.apache.sling.engine-2.0.6.jar/org.apache.sling.engine.impl.filter/AbstractSlingFilterChain.class:78)
WCMComponentFilter.filterRootInclude(SlingHttpServletRequest,SlingHttpServletResponse,FilterChain,Resource,Page,WCMMode,ComponentManager) (Unknown Source:375)
WCMComponentFilter.doFilter(ServletRequest,ServletResponse,FilterChain) (Unknown Source:190)
AbstractSlingFilterChain.doFilter(ServletRequest,ServletResponse) (/org.apache.sling.engine-2.0.6.jar/org.apache.sling.engine.impl.filter/AbstractSlingFilterChain.class:78)
PageLockFilter.doFilter(ServletRequest,ServletResponse,FilterChain) (Unknown Source:91)
AbstractSlingFilterChain.doFilter(ServletRequest,ServletResponse) (/org.apache.sling.engine-2.0.6.jar/org.apache.sling.engine.impl.filter/AbstractSlingFilterChain.class:78)
TargetComponentFilter.doFilter(ServletRequest,ServletResponse,FilterChain) (Unknown Source:94)
AbstractSlingFilterChain.doFilter(ServletRequest,ServletResponse) (/org.apache.sling.engine-2.0.6.jar/org.apache.sling.engine.impl.filter/AbstractSlingFilterChain.class:78)
```

[3]
https://github.com/apache/felix-dev/blob/master/http/base/src/main/java/org/apache/felix/http/base/internal/whiteboard/PerBundleServletContextImpl.java#L214


## Use Case 2 (Fixed RR)

When you execute the following code [1] the Thread for the class goes to [2] and the state of the RR is remembered allowing for it to be closed. It seems that when we do not execute the call to get the ServletContext we avoid "loosing" the RR, the thread handling class is [3] in this scenario.

[1]

```
request.getRequestDispatcher(String.class)
```

[2]

```
ServletRequestWrapper.getAttribute(String) (/uber-jar-6.5.19.jar/javax.servlet/ServletRequestWrapper.class:38)
WCMComponentFilter$ForwardRequestDispatcher.forward(ServletRequest,ServletResponse) (Unknown Source:500)
HWServlet.doGet(SlingHttpServletRequest,SlingHttpServletResponse) (/Users/patriquelegault/Desktop/blablaws/src/main/java/com/adobe/aem/support/core/servlet/HWServlet.java:50)
SlingSafeMethodsServlet.mayService(SlingHttpServletRequest,SlingHttpServletResponse) (/uber-jar-6.5.19.jar/org.apache.sling.api.servlets/SlingSafeMethodsServlet.class:81)
SlingSafeMethodsServlet.service(SlingHttpServletRequest,SlingHttpServletResponse) (/uber-jar-6.5.19.jar/org.apache.sling.api.servlets/SlingSafeMethodsServlet.class:105)
SlingSafeMethodsServlet.service(ServletRequest,ServletResponse) (/uber-jar-6.5.19.jar/org.apache.sling.api.servlets/SlingSafeMethodsServlet.class:114)
RequestData.service(SlingHttpServletRequest,SlingHttpServletResponse) (/org.apache.sling.engine-2.0.6.jar/org.apache.sling.engine.impl.request/RequestData.class:579)
SlingComponentFilterChain.render(SlingHttpServletRequest,SlingHttpServletResponse) (/org.apache.sling.engine-2.0.6.jar/org.apache.sling.engine.impl.filter/SlingComponentFilterChain.class:45)
AbstractSlingFilterChain.doFilter(ServletRequest,ServletResponse) (/org.apache.sling.engine-2.0.6.jar/org.apache.sling.engine.impl.filter/AbstractSlingFilterChain.class:88)
WCMDebugFilter.doFilter(ServletRequest,ServletResponse,FilterChain) (Unknown Source:156)
AbstractSlingFilterChain.doFilter(ServletRequest,ServletResponse) (/org.apache.sling.engine-2.0.6.jar/org.apache.sling.engine.impl.filter/AbstractSlingFilterChain.class:78)
WCMComponentFilter.filterRootInclude(SlingHttpServletRequest,SlingHttpServletResponse,FilterChain,Resource,Page,WCMMode,ComponentManager) (Unknown Source:375)
WCMComponentFilter.doFilter(ServletRequest,ServletResponse,FilterChain) (Unknown Source:190)
AbstractSlingFilterChain.doFilter(ServletRequest,ServletResponse) (/org.apache.sling.engine-2.0.6.jar/org.apache.sling.engine.impl.filter/AbstractSlingFilterChain.class:78)
PageLockFilter.doFilter(ServletRequest,ServletResponse,FilterChain) (Unknown Source:91)
AbstractSlingFilterChain.doFilter(ServletRequest,ServletResponse) (/org.apache.sling.engine-2.0.6.jar/org.apache.sling.engine.impl.filter/AbstractSlingFilterChain.class:78)
TargetComponentFilter.doFilter(ServletRequest,ServletResponse,FilterChain) (Unknown Source:94)
AbstractSlingFilterChain.doFilter(ServletRequest,ServletResponse) (/org.apache.sling.engine-2.0.6.jar/org.apache.sling.engine.impl.filter/AbstractSlingFilterChain.class:78)
SlingRequestProcessorImpl.processComponent(SlingHttpServletRequest,SlingHttpServletResponse,ServletFilterManager$FilterChainType) (Unknown Source:283)
RequestSlingFilterChain.render(SlingHttpServletRequest,SlingHttpServletResponse) (/org.apache.sling.engine-2.0.6.jar/org.apache.sling.engine.impl.filter/RequestSlingFilterChain.class:49)
```

[3]
cq-wcm-core/src/main/java/com/day/cq/wcm/core/impl/WCMComponentFilter.java