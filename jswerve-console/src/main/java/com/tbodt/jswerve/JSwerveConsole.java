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

/**
 *
 * @author Theodore Dubois
 */
public class JSwerveConsole {
    private enum Command {
        START(0),
        STOP(1);
        
        private final int code;

        private Command(int code) {
            this.code = code;
        }
        
        public byte getCode() {
            return (byte) code;
        }
        
        public static boolean isSuccess(byte code) {
            return (code & (1 << 6)) == 0;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 1)
            error("one argument required");
        Command cmd = Command.valueOf(args[0].toUpperCase());
        DatagramSocket socket = new DatagramSocket();
        byte[] buf = new byte[32];
        Arrays.fill(buf, cmd.getCode());
        socket.send(new DatagramPacket(buf, buf.length, InetAddress.getLocalHost(), 9999));
        DatagramPacket result = new DatagramPacket(buf, buf.length);
        socket.receive(result);
        if (Command.isSuccess(buf[0]))
            System.out.println("Success!");
        else
            System.out.println("FAILURE");
    }
    
    private static void error(String msg) {
        System.out.println(msg);
        System.out.println("usage: jswerve command");
        System.exit(1);
    }
}
