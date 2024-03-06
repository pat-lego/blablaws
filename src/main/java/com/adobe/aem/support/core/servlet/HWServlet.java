package com.adobe.aem.support.core.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = { Servlet.class })
@SlingServletResourceTypes(
    resourceTypes="support/hwservlet", 
    methods= "GET")
public class HWServlet extends SlingSafeMethodsServlet {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException {
        logger.info("In the {} servlet", this.getClass().getName());
        int sleepTime = 2000;
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
           logger.error("Failed sleeping {} ms", sleepTime, e);
        }

        //  This will cause a RR session leak
        //RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher("/content/dam/we-retail/en/activities/biking/enduro-trail-jump.jpg");

        // This will NOT cause a RR session leak
        RequestDispatcher dispatcher = request.getRequestDispatcher("/content/dam/we-retail/en/activities/biking/enduro-trail-jump.jpg/jcr:content/renditions/original");  
        try {
            dispatcher.forward((ServletRequest)request, (ServletResponse)response);
        } catch (ServletException | IOException e) {
            logger.error("Failed to dispatch request");
        }

        logger.info("Completed the {} servlet", this.getClass().getName());
    }
}
