/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tbodt.jswerve.examples.name;

import com.tbodt.jswerve.Controller;
import static com.tbodt.jswerve.RouteBuilders.*;
import com.tbodt.jswerve.RouteBuilders.RouteInfo;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @author Theodore Dubois
 */
public class NameController extends Controller {
    public static final RouteInfo[] ROUTES = {
        get("/").to(NameController.class, "index"),
        post("/").to(NameController.class, "submit"),
        get("/:name").to(NameController.class, "hello")
    };

    public void index() {
        renderAction("index");
    }

    public void submit() {
        redirectTo("/" + getParam("name"));
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
