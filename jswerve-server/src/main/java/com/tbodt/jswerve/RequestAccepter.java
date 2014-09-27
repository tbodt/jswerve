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

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

/**
 *
 * @author Theodore Dubois
 */
public class RequestAccepter implements Runnable {
    private static Thread theThread;
    private static Website website = Website.getCurrentWebsite();
    private static ExecutorService pool;
    private static ServerSocket serverSocket;

    public static void start() {
        try {
            if (serverSocket == null)
                serverSocket = new ServerSocket(JSwerve.PORT);
            website = Website.getCurrentWebsite();
            pool = Executors.newCachedThreadPool();
            theThread = new Thread(new RequestAccepter(), "Request Accepter");
            theThread.setContextClassLoader(website.getClassLoader());
            theThread.start();
            Logging.LOG.info("Successfully started server");
        } catch (Exception ex) {
            Logging.LOG.log(Level.SEVERE, "Error starting server", ex);
        }
    }

    public static void stop() {
        try {
            if (theThread != null) {
                theThread.interrupt();
                theThread = null;
                pool.shutdown();
                pool = null;
            }
            Logging.LOG.info("Successfully stopped server");
        } catch (RuntimeException ex) {
            Logging.LOG.log(Level.SEVERE, "Error stopping server", ex);
        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                final Socket socket = serverSocket.accept();
                pool.execute(new Runnable() {
                    public void run() {
                        try {
                            StatusCode status;
                            Request request;
                            Response response;
                            String httpVersion;
                            try {
                                request = new Request(socket.getInputStream());
                                httpVersion = request.getHttpVersion();
                                response = Website.getCurrentWebsite().service(request);
                            } catch (StatusCodeException ex) {
                                status = ex.getStatusCode();
                                if (ex instanceof BadRequestException)
                                    httpVersion = ((BadRequestException) ex).getHttpVersion();
                                else
                                    httpVersion = "HTTP/1.1";
                                if (ex.getCause() != null)
                                    ex.getCause().printStackTrace(System.err);
                                response = new Response(status);
                            }
                            response.writeResponse(socket.getOutputStream(), httpVersion);
                            socket.close();
                        } catch (IOException ex) {
                            // we can't really do anything about that
                        }
                    }
                });
            }
        } catch (InterruptedIOException ex) {
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void join() throws InterruptedException {
        theThread.join();
    }
}
