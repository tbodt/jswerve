/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tbodt.jswerve.examples.name;

import com.tbodt.jswerve.*;
import java.io.PrintWriter;
import java.util.regex.Pattern;

/**
 *
 * @author Theodore Dubois
 */
public class NameDisplayPage extends PatternPage {
    public NameDisplayPage() {
        super(Request.Method.GET, Pattern.compile("/(\\w)+/?"));
    }

    @Override
    public Response service(Request request) {
        String name = matcher(request).group(1);
        Response.Builder builder = Response.builder();
        PrintWriter out = new PrintWriter(builder.getOutputStream());
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Hello, " + name + "!</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("Hello world! Your name is " + name + ".");
        out.println("</body>");
        out.println("</html>");
        return builder.build();
    }
}
