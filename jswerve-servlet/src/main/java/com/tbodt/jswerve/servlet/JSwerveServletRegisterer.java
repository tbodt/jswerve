/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tbodt.jswerve.servlet;

import java.util.Set;
import javax.servlet.*;

/**
 * Automates registration of the JSwerve Servlet, so you don't have to put it in the web.xml.
 *
 * @author Theodore Dubois
 */
public final class JSwerveServletRegisterer implements ServletContainerInitializer {
    public void onStartup(Set<Class<?>> classes, ServletContext ctx) throws ServletException {
        ctx.addServlet("jswerve-servlet", JSwerveServlet.class).addMapping("/*");
    }
}
