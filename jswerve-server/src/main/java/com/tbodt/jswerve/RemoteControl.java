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
import java.net.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Theodore Dubois
 */
public final class RemoteControl {
    private static boolean activated;

    public static void activate() {
        if (!activated)
            new Thread(new Runnable() {
                public void run() {
                    RemoteControl.run();
                }
            }, "Remote Control").start();
        activated = true;
    }

    private static DatagramSocket socket;
    private static final byte START_CODE = 0;
    private static final byte STOP_CODE = 1;

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
                if (!packet.equals(InetAddress.getLocalHost()))
                    continue; // ignore requests from everyone else

                switch (buf[0]) {
                    case STOP_CODE:
                        respond(packet.getAddress(), packet.getPort(), STOP_CODE, true);
                        System.exit(0);
                        break;
                }
            } catch (IOException ex) {
                // continue
            }
    }

    private static void respond(InetAddress addr, int port, byte code, boolean success) throws IOException {
        // High bit indicates whether it's a request or response.
        // Next bit is whether it was successful. 1 if successful, 0 otherwise.
        code |= 1 << 7;
        if (!success)
            code |= 1 << 6;
        byte[] responseBytes = new byte[32];
        Arrays.fill(responseBytes, code);
        DatagramPacket response = new DatagramPacket(responseBytes, responseBytes.length, addr, port);
        socket.send(response);
    }

    public static void respondStart(boolean success) {
        try {
            respond(InetAddress.getLocalHost(), 8470, START_CODE, success);
        } catch (IOException ex) {
            System.exit(1); // by now it's hopeless
        }
    }
}
