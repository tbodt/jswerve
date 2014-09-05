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
            StatusCode status = StatusCode.INTERNAL_SERVER_ERROR;
            Request request = null;
            String httpVersion;
            try {
                request = Request.readRequest(socket.getInputStream());
                httpVersion = request.getHttpVersion();
            } catch (StatusCodeException ex) {
                status = ex.getStatusCode();
                if (ex instanceof BadRequestException)
                    httpVersion = ((BadRequestException) ex).getHttpVersion();
                else
                    httpVersion = "HTTP/1.1";
            }
            Response response;
            if (request == null)
                response = new Response(status, httpVersion);
            else
                response = funResponse();
            response.writeResponse(socket.getOutputStream());
            socket.close();
        } catch (IOException ex) {
            // we can't really do anything about that
        }
    }

    private static Response funResponse() {
        String body = "Hello, world!";
        return new Response(StatusCode.OK, "HTTP/1.1", body.getBytes(), "text/html");
    }
}
