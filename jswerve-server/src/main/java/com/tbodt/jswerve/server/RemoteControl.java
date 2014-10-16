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

import com.tbodt.jswerve.Logging;
import com.tbodt.jswerve.Website;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.logging.*;

/**
 *
 * @author Theodore Dubois
 */
public final class RemoteControl {
    private static boolean activated;

    public static void activate() {
        if (!activated)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    RemoteControl.run();
                }
            }, "Remote Control").start();
        activated = true;
    }

    private static Server server;
    private static DatagramSocket socket;
    private static final byte START_CODE = 0;
    private static final byte STOP_CODE = 1;
    private static final byte DEPLOY_CODE = 2;

    private static void run() {
        try {
            socket = new DatagramSocket(9999);
        } catch (SocketException ex) {
            throw new RuntimeException(ex);
        }
        while (true)
            try {
                byte[] buf = new byte[32];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                if (!packet.getAddress().equals(InetAddress.getLocalHost()))
                    continue; // ignore requests from everyone else

                Logging.LOG.addHandler(new DatagramHandler(packet));
                switch (buf[0]) {
                    case START_CODE:
                        if (server == null)
                            server = new Server(new Website("hello-website"), null);
                        server.start();
                        break;
                    case DEPLOY_CODE:
                        server.deploy(new Website("hello-website"));
                        break;
                    case STOP_CODE:
                        server.stop();
                        System.exit(0);
                        break;
                }
            } catch (IOException ex) {
                // continue
            }
    }

    private static class DatagramHandler extends Handler {
        private final InetAddress addr;
        private final int port;
        private boolean closed;

        private DatagramHandler(DatagramPacket packet) {
            addr = packet.getAddress();
            port = packet.getPort();
        }

        @Override
        public void publish(LogRecord record) {
            if (closed)
                throw new IllegalStateException("handler is closed");
            if (record.getMessage().contains("Successfully") || record.getMessage().contains("Error"))
                Logging.LOG.removeHandler(this);

            String recordString;
            if (getFormatter() == null) {
                recordString = "[" + record.getLevel() + "] " + record.getMessage();
                if (record.getParameters() != null)
                    recordString += Arrays.toString(record.getParameters());
                if (record.getThrown() != null) {
                    StringWriter stackTraceWriter = new StringWriter();
                    record.getThrown().printStackTrace(new PrintWriter(stackTraceWriter));
                    recordString += stackTraceWriter.toString();
                }
            } else
                recordString = getFormatter().format(record);

            try {
                respond(addr, port, recordString);
            } catch (IOException ex) {
                getErrorManager().error(null, ex, ErrorManager.GENERIC_FAILURE);
            }
        }

        private static void respond(InetAddress addr, int port, String message) throws IOException {
            byte[] responseBytes = message.getBytes();
            DatagramPacket response = new DatagramPacket(responseBytes, responseBytes.length, addr, port);
            socket.send(response);
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws SecurityException {
            closed = true;
        }
    }

}
