/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tbodt.jswerve.examples.name;

import com.tbodt.jswerve.controller.Controller;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @author Theodore Dubois
 */
public class NameController extends Controller {
    public void index() {
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw, true);
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>The Name Website</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<form method=\"POST\" action=\"/\">");
        out.println("<label for=\"name-field\">Enter your name:</label>");
        out.println("<input type=\"text\" name=\"name\" id=\"name-field\" /><br />");
        out.println("<input type=\"submit\" />");
        out.println("</form>");
        out.println("</body>");
        out.println("</html>");
        renderText(sw.toString(), "text/html");
    }

    public void submit() {
        
    }

    public void hello() {
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw, true);
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Hello, " + getParam("name") + "!</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("Hello world! Your name is " + getParam("name") + ".");
        out.println("</body>");
        out.println("</html>");
        renderText(sw.toString(), "text/html");
    }
}
