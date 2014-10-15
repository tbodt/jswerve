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

/**
 *
 * @author Theodore Dubois
 */
public class Website {
    private static final File SITES = new File(JSwerve.HOME, "sites");

    private final WebsiteClassLoader classLoader = new WebsiteClassLoader();
    private final FileSystem archive;
    private final Set<AbstractPage> pages = new HashSet<>();

    public Website(String name) {
        this(new File(SITES, name + ".jar"));
    }

    public Website(File file) {
        this(file.toPath());
    }

    public Website(Path site) {
        try {
            this.archive = FileSystems.newFileSystem(site, null);
            Path mappings = archive.getPath("META-INF", "jswerve-pages");
            BufferedReader mappingsReader = Files.newBufferedReader(mappings);

            String pageLine;
            while ((pageLine = mappingsReader.readLine()) != null) {
                String[] components = pageLine.split("\\s+", 3);
                if (components.length != 3)
                    throw new IllegalArgumentException("invalid syntax in index");

                String path = components[0];
                Path file = archive.getPath(components[1]);
                String contentType = components[2];
                pages.add(new StaticPage(path, file, contentType));
            }
            Logging.LOG.log(Level.INFO, "Successfully created website at {0}", site);
        } catch (IOException ex) {
            Logging.LOG.log(Level.SEVERE, "Error creating website at " + site, ex);
            throw new RuntimeException(ex);
        }
    }

    public Response service(Request request) {
        for (AbstractPage page : pages)
            if (page.canService(request))
                return page.service(request);
        return new Response(StatusCode.NOT_FOUND);
    }

    private final class StaticPage extends AbstractPage {
        private final Path file;
        private final String path;
        private final String contentType;

        public StaticPage(String path, Path file, String contentType) {
            super(Request.Method.GET);
            this.file = file;
            if (!path.startsWith("/"))
                path = "/" + path;
            this.path = path;
            this.contentType = contentType;
        }

        @Override
        public boolean canService(Request request) {
            return super.canService(request) && request.getUri().getPath().equals(path);
        }

        @Override
        public Response service(Request request) {
            try {
                return new Response(StatusCode.OK, Files.newInputStream(file), contentType);
            } catch (IOException ex) {
                return new Response(StatusCode.INTERNAL_SERVER_ERROR);
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
