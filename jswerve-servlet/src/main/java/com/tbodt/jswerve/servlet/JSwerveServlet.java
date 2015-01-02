/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tbodt.jswerve.servlet;

import com.tbodt.jswerve.*;
import com.tbodt.jswerve.core.*;
import com.tbodt.jswerve.util.UrlUtils;
import java.io.IOException;
import java.net.*;
import java.util.*;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.apache.commons.io.IOUtils;

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
        spiderWar(ctx, "/WEB-INF/classes", classes);
        try {
            website = new Website(classes);
        } catch (InvalidWebsiteException ex) {
            throw new ServletException(ex);
        }
    }

    private void spiderWar(ServletContext ctx, String start, Set<Class<?>> classes) {
        for (String path : ctx.getResourcePaths(start))
            if (ctx.getResourcePaths(path) != null && !ctx.getResourcePaths(path).isEmpty())
                spiderWar(ctx, path, classes);
            else if (path.endsWith(".class"))
                try {
                    classes.add(ctx.getClassLoader().loadClass(
                            path.substring("/WEB-INF/classes/".length(), path.indexOf(".class"))
                            .replace('/', '.')));
                } catch (ClassNotFoundException ex) {
                    throw new WTFException("class" + ex.getMessage() + " not found! but I saw it!");
                }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpMethod method = HttpMethod.valueOf(req.getMethod());
        URI uri = extractUri(req);
        Headers headers = translateHeaders(req);
        Content content = new Content(IOUtils.toByteArray(req.getInputStream()), req.getContentType());
        Request request = new Request(method, uri, headers, content);
        try {
            Response response = website.service(request);
            resp.getWriter().println(response.getContent().toString());
        } catch (StatusCodeException ex) {
            resp.setStatus(ex.getStatusCode().getCode());
            ex.printStackTrace(resp.getWriter());
        }
    }

    private URI extractUri(HttpServletRequest req) throws ServletException {
        try {
            return new URI(req.getScheme(),
                    UrlUtils.decode(req.getServerName()),
                    UrlUtils.decode(req.getRequestURI()),
                    UrlUtils.decode(req.getQueryString()), null);
        } catch (URISyntaxException ex) {
            throw new ServletException("Damn! URI syntax!", ex);
        }
    }

    private Headers translateHeaders(HttpServletRequest req) {
        Headers.Builder builder = new Headers.Builder();
        for (String headerName : Collections.list(req.getHeaderNames()))
            builder.setHeader(headerName, req.getHeader(headerName));
        return builder.build();
    }
}
