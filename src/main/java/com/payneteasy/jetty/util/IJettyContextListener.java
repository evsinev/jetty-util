package com.payneteasy.jetty.util;

import org.eclipse.jetty.servlet.ServletContextHandler;

public interface IJettyContextListener {


    void contextDidCreate(ServletContextHandler aContext);

}
