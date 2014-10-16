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
import java.nio.ByteBuffer;
import java.util.Map;

/**
 *
 * @author Theodore Dubois
 */
public class Response {
    private final StatusCode status;
    private final Headers headers;
    private InputStream body;

    public static final Headers DEFAULT_HEADERS = new Headers.Builder()
            .setHeader("Connection", "close")
            .build();

    public Response(StatusCode status, Headers headers) {
        this(status, headers, new InputStream() {
            @Override
            public int read() throws IOException {
                return -1;
            }
        }, null);
    }

    public Response(StatusCode status, Headers headers, InputStream body, String contentType) {
        this.status = status;
        Headers.Builder builder = new Headers.Builder(headers);
        builder.setHeaders(headers);
        if (contentType != null)
            builder.setHeader("Content-Type", contentType);
        this.headers = builder.build();
        this.body = body;
    }
        
    public static final class Builder {
        private StatusCode status;
        private final Headers.Builder headers = new Headers.Builder();
        private final ByteArrayOutputStream out = new ByteArrayOutputStream();
        private InputStream body;
        private String contentType;
        
        private boolean built = false;
        
        private Builder() {}
        
        public Builder status(StatusCode status) {
            this.status = status;
            return this;
        }
        
        public Builder header(String key, String value) {
            headers.setHeader(key, value);
            return this;
        }
        
        public Builder setBytes(byte[] bytes, String contentType) {
            body = new ByteArrayInputStream(bytes);
            this.contentType = contentType;
            return this;
        }
        
        public OutputStream getOutputStream() {
            return out;
        }
        
        public Builder setContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }
        
        public Response build() {
            if (built)
                throw new IllegalStateException("already built");
            if (status == null)
                throw new IllegalStateException("no status provided");
            if (contentType == null)
                throw new IllegalStateException("need content type");
            built = true;
            
            if (body == null)
                body = new ByteArrayInputStream(out.toByteArray());
            return new Response(status, headers.build(), body, contentType);
        }
    }
    
    public static Builder builder() {
        return new Builder();
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
    
    public InputStream getInputStream() {
        if (body == null)
            throw new IllegalStateException("getInputStream has already been called");
        InputStream ret = body;
        body = null;
        return ret;
    }
}
