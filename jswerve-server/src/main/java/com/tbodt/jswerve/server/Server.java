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

import com.tbodt.jswerve.Website;
import com.tbodt.jswerve.util.Logging;
import java.io.IOException;
import java.net.*;
import java.nio.channels.*;
import java.util.Set;
import java.util.concurrent.*;
import java.util.logging.Level;

/**
 *
 * @author Theodore Dubois
 */
public class Server implements Runnable {
    private Thread theThread;
    private Website website;
    private final ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 2, new ThreadFactory() {
        public Thread newThread(Runnable r) {
            return new Thread(r, "Server slave");
        }
    });
    private final Selector selector;

    public Server(Website website, Protocol... protocols) throws IOException {
        this.website = website;
        selector = Selector.open();

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
                for (final SelectionKey key : keys)
                    try {
                        if (!key.isValid()) {
                            Logging.LOG.fine("Skipping invalid selector key");
                            continue;
                        }
                        if (key.isAcceptable()) {
                            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                            Protocol protocol = (Protocol) key.attachment();
                            SocketChannel sc;
                            while ((sc = ssc.accept()) != null) {
                                sc.configureBlocking(false);
                                Connection connection = protocol.newConnection(website, sc);
                                sc.register(selector, connection.getInterest().getOps(), connection);
                            }
                        }
                        if (key.isReadable()) {
                            key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
                            final Connection conn = (Connection) key.attachment();
                            pool.submit(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        conn.handleRead(key);
                                    } catch (IOException ex) {
                                        handleException(key, ex);
                                    }
                                }
                            });
                        }
                        if (key.isWritable()) {
                            key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                            final Connection conn = (Connection) key.attachment();
                            pool.submit(new Runnable() {
                                public void run() {
                                    try {
                                        conn.handleWrite(key);
                                    } catch (IOException ex) {
                                        handleException(key, ex);
                                    }
                                }
                            });
                        }
                    } catch (ClosedByInterruptException ex) {
                        // We were interrupted. Close everything and stop the thread.
                        selector.close();
                        return;
                    }
                keys.clear();
            }
        } catch (IOException ex) {
            // Select failed. Print an error message, but nothing else can be done.
            ex.printStackTrace(System.err);
        }
    }

    /**
     * Handle exception thrown in a runnable.
     *
     * @param key the selection key
     * @param ex the exception
     */
    private void handleException(SelectionKey key, IOException ex) {
        // Not much can be done. Just close the socket and hope for the best.
        ex.printStackTrace(System.err);
        try {
            key.channel().close();
        } catch (IOException ex1) {
            // Close failed. There is no hope.
            ex1.printStackTrace(System.err);
            
        }
    }
}
