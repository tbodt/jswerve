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

/**
 * A class with static methods that control the server. It's also the main class.
 *
 * @author Theodore Dubois
 */
public class JSwerve {
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
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        System.setProperty("line.separator", "\r\n"); // that's how HTTP does it
        if (System.getProperty("jswerve.home") == null)
            System.setProperty("jswerve.home", args[0]);
        HOME = new File(System.getProperty("jswerve.home"));
        if (!HOME.exists()) {
            System.err.println("That home doesn't exist");
            System.exit(1);
        }
        Logging.initialize();
        Server server = new Server(new Website("hello-website"));
        server.start();
    }
}
