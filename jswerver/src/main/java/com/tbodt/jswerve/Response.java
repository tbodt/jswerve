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

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Map;

/**
 *
 * @author Theodore Dubois
 */
public class Response {
    private final StatusCode status;
    private final String httpVersion;
    private final Map<String, String> headers;

    public Response(StatusCode status, String httpVersion) {
        this.status = status;
        this.httpVersion = httpVersion;
        this.headers = Collections.emptyMap();
    }
    
    public Response(StatusCode status, String httpVersion, Map<String, String> headers) {
        this.status = status;
        this.httpVersion = httpVersion;
        this.headers = headers;
    }
    
    public void writeResponse(OutputStream out) {
        PrintWriter writer = new PrintWriter(out);
        
        writer.write(httpVersion + " " + status);
        for (Map.Entry<String, String> entry : headers.entrySet())
            writer.write(entry.getKey() + ": " + entry.getValue());
    }
}
