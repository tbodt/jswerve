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
    private RoutingTable routes;

    public Website(String name) throws IOException {
        this(new File(SITES, name + ".jar"));
    }

    public Website(File file) throws IOException {
        this(file, new URLClassLoader(new URL[] {file.toURI().toURL()}));
    }

    public Website(File archive, ClassLoader loader) throws IOException {
        this.loader = loader;
        init(archive);
    }

    private void init(File archive) throws IOException {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        
        if (archive.isDirectory())
            spiderDirectory(archive, classes);
        else {
            ZipFile file = new ZipFile(archive);
            for (ZipEntry entry : Collections.list(file.entries()))
                addClass(classes, entry.getName());
        }
        
        this.routes = RoutingTable.extract(classes.toArray(new Class<?>[classes.size()]));
    }

    private void spiderDirectory(File archive, Set<Class<?>> classes) {
        for (File file : archive.listFiles())
            if (file.isDirectory())
                spiderDirectory(archive, classes);
            else
                addClass(classes, file.getName());
    }

    private void addClass(Set<Class<?>> classes, String name) {
        try {
            if (name.endsWith(".class"))
                classes.add(loader.loadClass(name.substring(0, name.indexOf(".class")).replace('/', '.')));
        } catch (ClassNotFoundException ex) {
            throw new WTFException("A class that is in the archive was not found in the archive!! File a bug report!!");
        }
    }

    public Response service(Request request) {
        try {
            return routes.route(request);
        } catch (StatusCodeException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace(System.err);
            throw new StatusCodeException(StatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    public ClassLoader getClassLoader() {
        return loader;
    }
}
