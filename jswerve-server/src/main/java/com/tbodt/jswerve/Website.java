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

import com.tbodt.jswerve.server.JSwerve;
import com.tbodt.jswerve.server.Logging;
import java.io.*;
import java.net.*;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 *
 * @author Theodore Dubois
 */
public class Website {
    private static final File SITES = new File(JSwerve.HOME, "sites");
    
    private final WebsiteClassLoader classLoader = new WebsiteClassLoader();
    private final FileSystem archive;
    private final Set<Page> pages = new HashSet<>();

    public Website(String name) {
        this(new File(SITES, name + ".jar"));
    }
    
    public Website(File file) {
        this(file.toPath());
    }
    
    public Website(Path site) {
        try {
            this.archive = FileSystems.newFileSystem(site, null);
            Path mappings = archive.getPath("META-INF", "index");
            BufferedReader mappingsReader = Files.newBufferedReader(mappings);

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
                pages.add(new StaticPage(pattern, path, contentType));
            }
            Logging.LOG.log(Level.INFO, "Successfully created website at {0}", site);
        } catch (IOException ex) {
            Logging.LOG.log(Level.SEVERE, "Error creating website at " + site, ex);
            throw new RuntimeException(ex);
        }
    }
    private static void ensure(boolean condition) {
        if (!condition)
            throw new IllegalArgumentException("invalid syntax in index");
    }

    public Response service(Request request) {
        URI absoluteUri = URI.create("http://" + request.getHeaders().get("Host")).resolve(request.getUri());
        String uri = absoluteUri.toString();
        for (Page page : pages)
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
            return new Response(StatusCode.OK, classLoader.getResourceAsStream(path), contentType);
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
                Path classFile = archive.getPath(name.replace(".", File.separator) + ".class");
                if (Files.notExists(classFile))
                    throw new ClassNotFoundException(name);
                byte[] bytes = Files.readAllBytes(classFile);
                return defineClass(name, bytes, 0, bytes.length);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        protected URL findResource(String name) {
            try {
                Path resource = archive.getPath(name);
                if (Files.notExists(resource))
                    return null;
                return archive.getPath(name).toUri().toURL();
            } catch (MalformedURLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
