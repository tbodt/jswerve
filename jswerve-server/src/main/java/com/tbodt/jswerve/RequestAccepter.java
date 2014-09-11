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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Theodore Dubois
 */
public class RequestAccepter implements Runnable {
    private static Thread theThread;
    private static ExecutorService pool;
    private static ServerSocket serverSocket;
    static {
        try {
            serverSocket = new ServerSocket(JSwerver.PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void start() {
        pool = Executors.newCachedThreadPool();
        theThread = new Thread(new RequestAccepter());
        theThread.start();
    }
    
    public static void stop() {
        if (theThread != null) {
            theThread.interrupt();
            theThread = null;
            pool.shutdown();
            pool = null;
        }
    }
    
    @Override
    public void run() {
        try {
            while (!Thread.interrupted())
                pool.execute(new RequestHandler(serverSocket.accept()));
        } catch (InterruptedIOException ex) {
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
