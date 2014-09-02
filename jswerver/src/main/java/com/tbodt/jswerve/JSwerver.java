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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 *
 * @author Theodore Dubois
 */
public class JSwerver {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        ExecutorService pool = Executors.newWorkStealingPool();
        ServerSocket ss = new ServerSocket(8888);
        while (true) {            
            Socket socket = ss.accept();
            pool.execute(new RequestHandler(socket));
        }
    }
}
