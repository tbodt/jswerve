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
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

/**
 *
 * @author Theodore Dubois
 */
public class Server implements Runnable {
    private Thread theThread;
    private Website website;
    private final Protocol[] protocols;
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final Selector selector;

    public Server(Website website, Protocol... protocols) throws IOException {
        this.website = website;
        selector = Selector.open();
        this.protocols = protocols;

        for (Protocol protocol : protocols) {
            ServerSocketChannel channel = ServerSocketChannel.open();
            channel.configureBlocking(false);
            channel.socket().bind(new InetSocketAddress((InetAddress) null, protocol.getPort()));
            channel.register(selector, SelectionKey.OP_ACCEPT, protocol);
        }
    }

    public void start() {
        try {
            theThread = new Thread(this, "Server");
            theThread.setContextClassLoader(website.getClassLoader());
            theThread.start();
            Logging.LOG.info("Successfully started server");
        } catch (Exception ex) {
            Logging.LOG.log(Level.SEVERE, "Error starting server", ex);
        }
    }

    public void stop() {
        try {
            if (theThread != null) {
                theThread.interrupt();
                theThread = null;
                pool.shutdown();
            }
            Logging.LOG.info("Successfully stopped server");
        } catch (RuntimeException ex) {
            Logging.LOG.log(Level.SEVERE, "Error stopping server", ex);
        }
    }

    public void deploy(Website website) {
        stop();
        this.website = website;
        start();
        Logging.LOG.info("Successfully deployed something");
    }

    public void join() throws InterruptedException {
        if (theThread != null)
            theThread.join();
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                selector.select();

                Set<SelectionKey> keys = selector.selectedKeys();
                for (SelectionKey key : keys) {
                    if (!key.isValid()) {
                        Logging.LOG.fine("Skipping invalid selector key");
                        continue;
                    }
                    if (key.isAcceptable()) {
                        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                        Protocol protocol = (Protocol) key.attachment();
                        SocketChannel sc = ssc.accept();
                        sc.configureBlocking(false);
                        sc.register(selector, SelectionKey.OP_READ, protocol.newConnection(website, sc));
                    }
                    if (key.isReadable()) {
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        SocketChannel sc = (SocketChannel) key.channel();
                        int count = sc.read(buffer);
                        if (count == -1) {
                            // Connection shut down.
                            sc.close();
                            key.cancel();
                            continue;
                        }
                        buffer.flip();
                        HttpConnection conn = (HttpConnection) key.attachment();
                        conn.handleRead(buffer, key);
                    }
                    if (key.isWritable()) {
                        SocketChannel sc = (SocketChannel) key.channel();
                        HttpConnection conn = (HttpConnection) key.attachment();
                        conn.handleWrite(key);
                    }
                }
                keys.clear();
            }
        } catch (InterruptedIOException ex) {
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    /*
     final Socket socket = channel.accept();
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
     response = website.service(request);
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
     */

}
