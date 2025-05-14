package com.payneteasy.jetty.util;

import org.eclipse.jetty.ee8.servlet.ServletContextHandler;

public interface IJettyContextListener {

    void contextDidCreate(ServletContextHandler aContext);

}
