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
import java.net.*;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

/**
 *
 * @author Theodore Dubois
 */
public class Website {
    private static final File SITES = new File(Constants.HOME, "sites");

    private final ClassLoader loader;
    private final Set<Page> pages = new HashSet<Page>();

    public Website(String name) {
        this(new File(SITES, name + ".jar"));
    }

    public Website(File file) {
        this(classLoaderForFile(file));
    }
    
    // this is in a separate method because the constructor call can't go in a try/catch and has to be first
    private static ClassLoader classLoaderForFile(File file) {
        try {
            return new URLClassLoader(new URL[] {file.toURI().toURL()});
        } catch (MalformedURLException ex) {
            // no will happen
            throw new RuntimeException(ex);
        }
    }

    public Website(ClassLoader loader) {
        try {
            this.loader = loader;
            BufferedReader mappingsReader = new BufferedReader(new InputStreamReader(loader.getResourceAsStream("META-INF/jswerve-pages")));

            String pageLine;
            while ((pageLine = mappingsReader.readLine()) != null) {
                String[] components = pageLine.split("\\s+", 3);
                if (components.length == 1) {
                    String className = components[0];
                    try {
                        @SuppressWarnings("unchecked")
                        Class<? extends Page> clazz = (Class<? extends Page>) loader.loadClass(className);
                        Page page = clazz.newInstance();
                        pages.add(page);
                    } catch (ClassNotFoundException ex) {
                        throw new IllegalArgumentException("no index class " + className);
                    } catch (InstantiationException ex) {
                        throw new IllegalArgumentException("class " + className + " has no no-arg constructor");
                    } catch (IllegalAccessException ex) {
                        throw new IllegalArgumentException("class " + className + " has no public no-arg constructor");
                    }
                } else {
                    if (components.length != 3)
                        throw new IllegalArgumentException("invalid syntax in index");

                    String path = components[0];
                    URL file = loader.getResource(components[1]);
                    String contentType = components[2];
                    pages.add(new StaticPage(path, file, contentType));
                }
            }
            Logging.LOG.log(Level.INFO, "Successfully created website");
        } catch (IOException ex) {
            Logging.LOG.log(Level.SEVERE, "Error creating website", ex);
            throw new RuntimeException(ex);
        }
    }

    public Response service(Request request) {
        try {
        for (Page page : pages)
            if (page.canService(request))
                return page.service(request);
        } catch (RuntimeException ex) {
            ex.printStackTrace(System.err);
            throw new StatusCodeException(StatusCode.INTERNAL_SERVER_ERROR);
        }
        return new Response(StatusCode.NOT_FOUND, Headers.EMPTY_HEADERS);
    }

    private final class StaticPage extends AbstractPage {
        private final URL url;
        private final String path;
        private final String contentType;

        public StaticPage(String path, URL file, String contentType) {
            super(Request.Method.GET);
            this.url = file;
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
                return new Response(StatusCode.OK, Headers.EMPTY_HEADERS, url.openStream(), contentType);
            } catch (IOException ex) {
                return new Response(StatusCode.INTERNAL_SERVER_ERROR, Headers.EMPTY_HEADERS);
            }
        }

        public String getPath() {
            return path;
        }
    }

    public ClassLoader getClassLoader() {
        return loader;
    }
}
