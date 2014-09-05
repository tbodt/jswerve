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

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Theodore Dubois
 */
public class Response {
    private final StatusCode status;
    private final Map<String, String> headers;
    private final byte[] body;

    public static final Map<String, String> DEFAULT_HEADERS = new HashMap<String, String>();

    static {
        DEFAULT_HEADERS.put("Connection", "close");
    }

    public Response(StatusCode status) {
        this(status, null, null);
    }

    public Response(StatusCode status, byte[] body, String contentType) {
        this.status = status;
        this.headers = new HashMap<String, String>(DEFAULT_HEADERS);
        headers.put("Content-Type", contentType);
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void writeResponse(OutputStream out, String httpVersion) throws IOException {
        PrintWriter writer = new PrintWriter(out);

        writer.println(httpVersion + " " + status);
        for (Map.Entry<String, String> entry : headers.entrySet())
            writer.println(entry.getKey() + ": " + entry.getValue());
        writer.println();
        writer.flush();

        if (body != null) {
            out.write(body);
            out.flush();
        }
    }
}
