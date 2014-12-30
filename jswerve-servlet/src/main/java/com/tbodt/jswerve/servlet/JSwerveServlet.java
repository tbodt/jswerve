/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tbodt.jswerve.servlet;

import com.tbodt.jswerve.WTFException;
import com.tbodt.jswerve.server.InvalidWebsiteException;
import com.tbodt.jswerve.server.Website;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;

/**
 *
 * @author Theodore Dubois
 */
public class JSwerveServlet extends HttpServlet {
    private Website website;

    @Override
    public void init() throws ServletException {
        ServletContext ctx = getServletContext();
        Set<Class<?>> classes = new HashSet<Class<?>>();
        spiderWar(ctx, "/", classes);
        try {
            website = new Website(classes);
        } catch (InvalidWebsiteException ex) {
            throw new ServletException(ex);
        }
    }

    private void spiderWar(ServletContext ctx, String start, Set<Class<?>> classes) {
        for (String path : ctx.getResourcePaths(start))
            if (ctx.getResourcePaths(path) != null)
                spiderWar(ctx, path, classes);
            else if (path.endsWith(".class"))
                try {
                    classes.add(ctx.getClassLoader().loadClass(path.substring(path.indexOf(".class")).replace('/', '.')));
                } catch (ClassNotFoundException ex) {
                    throw new WTFException("class" + ex.getMessage() + " not found! but I saw it!");
                }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println("Working!");
    }
}
