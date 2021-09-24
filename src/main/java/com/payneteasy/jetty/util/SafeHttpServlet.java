package com.payneteasy.jetty.util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class SafeHttpServlet extends HttpServlet {

    @Override
    protected final  void doPost(HttpServletRequest aRequest, HttpServletResponse aResponse) throws ServletException, IOException {
        doSafePost(new SafeServletRequest(aRequest), new SafeServletResponse(aResponse));
    }

    @Override
    protected final  void doGet(HttpServletRequest aRequest, HttpServletResponse aResponse) throws ServletException, IOException {
        doSafeGet(new SafeServletRequest(aRequest), new SafeServletResponse(aResponse));
    }

    protected void doSafePost(SafeServletRequest aRequest, SafeServletResponse aResponse) throws ServletException, IOException {
        throw new IllegalStateException("doSafePost() no implemented()");
    }

    protected void doSafeGet(SafeServletRequest aRequest, SafeServletResponse aResponse) throws ServletException, IOException {
        throw new IllegalStateException("doSafeGet() no implemented()");
    }


}
