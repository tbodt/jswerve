/*
 * Copyright (C) 2014 Theodore Dubois
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.tbodt.jswerve;

import java.io.*;
import java.util.logging.*;

/**
 * A class with static methods that control the server. It's also the main class.
 *
 * @author Theodore Dubois
 */
public class JSwerver {
    /**
     * The home directory for the server.
     */
    public static File HOME;
    /**
     * The default port for the server.
     */
    public static final int PORT = 8888;
    /**
     * If no HTTP version is specified by the client, this is used.
     */
    public static final String DEFAULT_HTTP_VERSION = "HTTP/1.1";

    /**
     * Deploys a different website into the server.
     *
     * @param name the name of the website
     */
    public static void deploy(String name) {
        RequestAccepter.stop();
        Website.setCurrentWebsite(new Website(name));
        RequestAccepter.start();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        System.setProperty("line.separator", "\r\n"); // that's how HTTP does it
        if (System.getProperty("jswerve.home") == null)
            System.setProperty("jswerve.home", args[0]);
        HOME = new File(System.getProperty("jswerve.home"));
        destroyIO(); // Don't use System.out, err, or in. We have loggers.
        initLogging();
        RemoteControl.activate();

        deploy("hello-website");
    }

    private static void initLogging() throws IOException {
        Logger appLogger = Logger.getLogger("com.tbodt.jswerve");
        appLogger.setUseParentHandlers(false); // no globally inherited console handler
        appLogger.setLevel(Level.ALL);
        
        Handler logHandler = new FileHandler(HOME.getAbsolutePath() + "jswerve.log");
        logHandler.setLevel(Level.ALL);
        appLogger.addHandler(logHandler);
        
        Handler outHandler = new FileHandler(HOME.getAbsolutePath() + "jswerve.out");
        logHandler.setLevel(Level.INFO);
        appLogger.addHandler(logHandler);
    }

    private static void destroyIO() throws IOException {
        PrintStream out = new PrintStream(new FileOutputStream(new File(HOME, "jswerve.out")));
        InputStream in = new InputStream() {
            @Override
            public int read() throws IOException {
                return -1;
            }
        };
        System.setOut(out);
        System.setErr(out);
        System.setIn(in);
    }
}
