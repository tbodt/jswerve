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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.io.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author Theodore Dubois
 */
public class Website {
    private static Website currentWebsite = new Website("hello");
    private static final File SITES = new File(JSwerver.HOME, "sites");

    private final String name;
    private final ZipFile archive;
    private final Multimap<Request.Method, Page> entries = HashMultimap.create();

    public Website(String name) {
        this.name = name;
        try {
            this.archive = new ZipFile(new File(SITES, name + ".jar"));
            ZipEntry mappings = archive.getEntry("META-INF/index");
            BufferedReader mappingsReader = new BufferedReader(new InputStreamReader(archive.getInputStream(mappings)));

            String requestPattern;
            while ((requestPattern = mappingsReader.readLine()) != null) {
                Request.Method method;
                Pattern pattern;

                String[] methodPattern = requestPattern.split(" ", 2);
                ensure(methodPattern.length == 2);
                method = Request.Method.forName(methodPattern[0]);
                pattern = Pattern.compile(methodPattern[1]);

                String response = mappingsReader.readLine();
                ensure(response != null); // null means EOF
                String[] typePath = requestPattern.split(":", 2);
                ensure(typePath.length == 2);
                String contentType = typePath[0];
                String path = typePath[1];
                entries.put(method, new StaticPage(pattern, path, contentType));
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public Response service(Request request) {
        String uri = request.getUri().toString();
        Request.Method method = request.getMethod();
        for (Page page : entries.get(method))
            if (page.getPattern().matcher(uri).matches())
                return page.serve(request);
        return new Response(StatusCode.NOT_FOUND);
    }

    private static abstract class Page {
        private final Pattern pattern;

        public Page(Pattern pattern) {
            this.pattern = pattern;
        }

        public Pattern getPattern() {
            return pattern;
        }

        public abstract Response serve(Request request);
    }

    private final class StaticPage extends Page {
        private final String path;
        private final String contentType;

        public StaticPage(Pattern pattern, String path, String contentType) {
            super(pattern);
            this.path = path;
            this.contentType = contentType;
        }

        @Override
        public Response serve(Request request) {
            try {
                ZipEntry entry = archive.getEntry(path);
                InputStream pageIn = archive.getInputStream(entry);
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                int b;
                while ((b = pageIn.read()) != -1)
                    buf.write(b);
                return new Response(StatusCode.OK, buf.toByteArray(), contentType);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        public String getPath() {
            return path;
        }
    }


    public static Website getCurrentWebsite() {
        return currentWebsite;
    }

    public static void setCurrentWebsite(Website website) {
        currentWebsite = website;
    }

    private static void ensure(boolean condition) {
        if (!condition)
            throw new IllegalArgumentException("invalid syntax in index");
    }
}
