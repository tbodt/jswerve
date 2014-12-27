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
package com.tbodt.jswerve.server;

import com.tbodt.jswerve.util.Expectable;
import java.io.*;
import java.net.*;
import org.apache.commons.lang3.SystemUtils;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Theodore Dubois
 */
public class ServerTest {
    private static Server server;

    @Test
    public void testBasics() throws IOException {
        Expectable ex = new Expectable(new Socket("localhost", 8888));
        ex.writeln("GET /404 HTTP/1.1\n"
                + "Host: localhost\n");
        ex.expect("HTTP/1.1 404");
        ex.close();
    }

    @Test
    public void testSuccess() throws IOException {
        Expectable ex = new Expectable(new Socket("localhost", 8888));
        ex.writeln("GET / HTTP/1.1\n"
                + "Host: localhost\n");
        ex.expect("HTTP/1.1 200");
        ex.expect("");
        ex.expectExact("");
        ex.expectExact("Hello World!");
        ex.close();
    }

    @Test
    public void testAbruptClose() throws IOException {
        Expectable ex = new Expectable(new Socket("localhost", 8888));
        ex.writeln("GET /404 HTTP/1.1");
        ex.close();
    }

    @Test
    public void testBadRequests() throws IOException {
        Expectable ex = new Expectable(new Socket("localhost", 8888));
        ex.writeln("GET /404");
        ex.writeln("Host: localhost");
        ex.writeln();
        ex.expect("HTTP/1.1 400");
        ex.close();

        ex = new Expectable(new Socket("localhost", 8888));
        ex.writeln("GET /404 HTTP/1.1");
        ex.writeln("Host is localhost");
        ex.writeln();
        ex.expect("HTTP/1.1 400");
        ex.close();
    }

    @Test
    public void testDiscontinuousRequest() throws IOException {
        Expectable ex = new Expectable(new Socket("localhost", 8888));
        ex.writeln("GET /404 HTTP/1.1");
        ex.writeln("Host: localhost");
        ex.writeln();
        ex.expect("HTTP/1.1 404");
        ex.close();
    }

    @Test
    public void testWithHttpUrlConnection() throws IOException {
        URL url = new URL("http://localhost:8888/404");
        try {
            url.openStream();
            fail("URL was not a 404");
        } catch (FileNotFoundException e) {
        }
    }

    @BeforeClass
    public static void startServer() throws IOException {
        server = new Server(new Website(new File(
                SystemUtils.getUserDir().getParentFile(), // the root directory for Maven
                "examples/test-website/target/classes/"
        )), new HttpProtocol());
        server.start();
    }

    @AfterClass
    public static void stopServer() {
        if (server != null)
            server.stop();
    }
}
