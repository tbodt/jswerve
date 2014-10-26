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
package com.tbodt.jswerve.server;

import com.tbodt.jswerve.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author Theodore Dubois
 */
public class Website {
    private static final File SITES = new File(Constants.HOME, "sites");

    private ClassLoader loader;
    private final Container container = new Container();
    private final Set<Page> pages = new HashSet<Page>();

    public Website(String name) throws IOException {
        this(new File(SITES, name + ".jar"));
    }

    public Website(File file) throws IOException {
        try {
            init(file, new URLClassLoader(new URL[] {file.toURI().toURL()}));
        } catch (MalformedURLException ex) {
            // no will happen
            throw new RuntimeException(ex);
        }
    }

    public Website(File archive, ClassLoader loader) throws IOException {
        init(archive, loader);
    }

    private void init(File archive, ClassLoader loader) throws IOException {
        Set<String> classes = new HashSet<String>();
        if (archive.isDirectory())
            spiderDirectory(archive, classes);
        else {
            ZipFile file = new ZipFile(archive);
            for (ZipEntry entry : Collections.list(file.entries()))
                addClass(classes, entry.getName());
        }
        init(loader, classes.toArray(new String[classes.size()]));
    }

    private void spiderDirectory(File archive, Set<String> classes) {
        for (File file : archive.listFiles())
            if (file.isDirectory())
                spiderDirectory(archive, classes);
            else
                addClass(classes, file.getName());
    }

    private void addClass(Set<String> classes, String name) {
        if (name.endsWith(".class"))
            classes.add(name.substring(0, name.indexOf(".class")).replace('/', '.'));
    }

    private void init(ClassLoader loader, String[] interestingClasses) {
        this.loader = loader;
        for (String className : interestingClasses)
            try {
                Class<?> clazz = loader.loadClass(className);
                @SuppressWarnings("unchecked")
                Page page = (Page) container.get(clazz, null);
                pages.add(page);
            } catch (ClassNotFoundException ex) {
                throw new IllegalArgumentException("intersting class " + className + " doesn't exist");
            } catch (ReflectionException ex) {
                throw new IllegalArgumentException("error creating/introspecting " + className, ex);
            } catch (ClassCastException ex) {
                // this class doesn't implement Page. ignore, and try the next one.
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
