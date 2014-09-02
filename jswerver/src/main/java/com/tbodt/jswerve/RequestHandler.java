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
import java.net.Socket;

/**
 *
 * @author Theodore Dubois
 */
public class RequestHandler implements Runnable {
    private final Socket socket;

    public RequestHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            StatusCode status = StatusCode.OK;
            Request request = null;
            try {
                request = Request.readRequest(socket.getInputStream());
            } catch (StatusCodeException ex) {
                status = ex.getStatusCode();
            } catch (RuntimeException ex) {
                status = StatusCode.BAD_REQUEST;
            }
            Response response = new Response(status, request.getHttpVersion());
            response.writeResponse(socket.getOutputStream());
            socket.close();
        } catch (IOException ex) {
            // we can't really do anything about that
        }
    }
}
