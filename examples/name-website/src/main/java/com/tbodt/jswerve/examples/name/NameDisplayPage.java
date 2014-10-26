/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tbodt.jswerve.examples.name;

import com.tbodt.jswerve.*;
import com.tbodt.jswerve.annotation.Path;
import com.tbodt.jswerve.annotation.PathParam;
import java.io.PrintWriter;

/**
 *
 * @author Theodore Dubois
 */
public class NameDisplayPage {
    @Path("/{name}")
    public Response displayName(@PathParam("name") String name) {
        Response.Builder builder = Response.builder().status(StatusCode.OK);
        PrintWriter out = new PrintWriter(builder.getOutputStream(), true);
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Hello, " + name + "!</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("Hello world! Your name is " + name + ".");
        out.println("</body>");
        out.println("</html>");
        builder.setContentType("text/html");
        return builder.build();
    }
}
