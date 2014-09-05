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

import java.net.URI;

/**
 *
 * @author Theodore Dubois
 */
public class GetRequest extends Request {
    private final URI uri;

    public GetRequest(String requestUri) {
        uri = URI.create(requestUri);
    }

    @Override
    public Response service() {
        String body = "Hello, world!";
        return new Response(StatusCode.OK, body.getBytes(), "text/html");
    }
}
