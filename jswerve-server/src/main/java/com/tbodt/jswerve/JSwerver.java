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
        HOME = new File(args[0]);
        RemoteControl.activate();

        deploy("hello-website");

        // now read commands from the console
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = in.readLine()) != null) {
            if (line.equals("start"))
                RequestAccepter.start();
            else if (line.equals("stop"))
                RequestAccepter.stop();
            else if (line.equals("deploy"))
                deploy(in.readLine());
            else
                System.out.print("no such command ");
            System.out.println(line);
        }
    }
}
