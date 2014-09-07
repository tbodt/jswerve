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
public class WelcomeWebsite extends Website {
    private static final Map<String, String> contentTypes = new HashMap<String, String>();
    static {
        contentTypes.put("html", "text/html");
        contentTypes.put("png", "image/png");
    }
    
    @Override
    public Response serviceRequest(Request request) throws IOException {
        String path = "/com/tbodt/jswerve/welcome/" + request.getUri().getPath();
        if (path.endsWith("/"))
            path += "index.html";
        String contentType = "text/plain";
        if (path.lastIndexOf('.') != -1) {
            String extension = path.substring(path.lastIndexOf('.') + 1);
            if (contentTypes.containsKey(extension))
                contentType = contentTypes.get(extension);
        }
        System.out.println(contentType);
        System.out.println(path);
        
        InputStream pageIn = WelcomeWebsite.class.getResourceAsStream(path);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int b;
        while ((b = pageIn.read()) != -1)
            buf.write(b);
        return new Response(StatusCode.OK, buf.toByteArray(), contentType);
    }
}
