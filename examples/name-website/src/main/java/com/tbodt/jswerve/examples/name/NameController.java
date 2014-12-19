/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tbodt.jswerve.examples.name;

import com.tbodt.jswerve.Content;
import com.tbodt.jswerve.controller.Controller;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @author Theodore Dubois
 */
public class NameController extends Controller {
    public void displayName() {
        PrintWriter out = new PrintWriter(new StringWriter(), true);
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Hello, " + getParam("name") + "!</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("Hello world! Your name is " + getParam("name") + ".");
        out.println("</body>");
        out.println("</html>");
        setResponseData(new Content(out.toString().getBytes(), "text/html"));
    }
}
