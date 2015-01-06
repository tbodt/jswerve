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

import com.tbodt.jswerve.*;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 *
 * @author Theodore Dubois
 */
public class Response {
    private final StatusCode status;
    private final Headers headers;
    private final Content body;

    public static final Headers DEFAULT_HEADERS = Headers.builder()
            .header("Connection", "close")
            .build();

    public Response(StatusCode status, Headers headers) {
        this(status, headers, Content.EMPTY);
    }

    public Response(StatusCode status, Headers headers, Content body) {
        this.status = status;
        Headers.Builder builder = headers.getBuilder();
        builder.setHeaders(headers);
        if (body.getContentType() != null)
            builder.header("Content-Type", body.getContentType());
        this.headers = builder.build();
        this.body = body;
    }
    
    public Headers getHeaders() {
        return headers;
    }

    public ByteBuffer toBytes(String httpVersion) {
        StringBuilder sb = new StringBuilder();
        sb.append(httpVersion).append(" ").append(status).append("\n");
        for (Map.Entry<String, String> header : headers)
            sb.append(header.getKey()).append(": ").append(header.getValue()).append("\n");
        sb.append("\n");
        return ByteBuffer.wrap(sb.toString().getBytes());
    }
    
    public Content getContent() {
        return body;
    }
}
