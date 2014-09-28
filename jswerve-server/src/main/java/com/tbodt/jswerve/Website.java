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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author Theodore Dubois
 */
public class Website {
    private static final File SITES = new File(JSwerve.HOME, "sites");
    
    private final WebsiteClassLoader classLoader = new WebsiteClassLoader();
    private final ZipFile archive;
    private final Multimap<Request.Method, Page> entries = HashMultimap.create();

    public Website(String name) {
        this(new File(SITES, name + ".jar"));
    }
    
    public Website(File site) {
        try {
            this.archive = new ZipFile(site);
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
                String[] typePath = response.split(":", 2);
                ensure(typePath.length == 2);
                String contentType = typePath[0];
                String path = typePath[1];
                entries.put(method, new StaticPage(pattern, path, contentType));
            }
            Logging.LOG.log(Level.INFO, "Successfully created website at {0}", site);
        } catch (IOException ex) {
            Logging.LOG.log(Level.SEVERE, "Error creating website at " + site, ex);
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
            if (path.startsWith("/"))
                path = path.substring(1);
            this.path = path;
            this.contentType = contentType;
        }

        @Override
        public Response serve(Request request) {
            try {
                InputStream is = classLoader.getResourceAsStream(path);
                return new Response(StatusCode.OK, readAll(is), contentType);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        }

        public String getPath() {
            return path;
        }
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    private final class WebsiteClassLoader extends ClassLoader {
        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            try {
                ZipEntry entry = archive.getEntry(name.replace(".", "/") + ".class");
                if (entry == null)
                    throw new ClassNotFoundException(name);
                InputStream in = archive.getInputStream(entry);
                byte[] bytes = readAll(in);
                return defineClass(name, bytes, 0, bytes.length);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        protected URL findResource(String name) {
            try {
                return new URL("jar:" + new File(archive.getName()).toURI().toURL() + "!/" + name);
            } catch (MalformedURLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    private static void ensure(boolean condition) {
        if (!condition)
            throw new IllegalArgumentException("invalid syntax in index");
    }

    private static byte[] readAll(InputStream in) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int n;
        while ((n = in.read(b)) != -1)
            buf.write(b, 0, n);
        return buf.toByteArray();
    }
}
