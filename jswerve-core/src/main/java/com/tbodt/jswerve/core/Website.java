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
package com.tbodt.jswerve.core;

import com.tbodt.jswerve.Request;
import com.tbodt.jswerve.Response;
import com.tbodt.jswerve.WTFException;
import com.tbodt.jswerve.*;
import com.tbodt.jswerve.controller.Controller;
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
    private ClassLoader loader;
    private RoutingTable routes;

    public Website(File file) throws IOException, InvalidWebsiteException {
        this(file, new URLClassLoader(new URL[] {file.toURI().toURL()}));
    }

    public Website(File archive, ClassLoader loader) throws IOException, InvalidWebsiteException {
        this.loader = loader;
        init(archive);
    }

    public Website(Collection<Class<?>> classes) throws InvalidWebsiteException {
        init(classes);
    }

    private void init(File archive) throws IOException, InvalidWebsiteException {
        Set<Class<?>> classes = new HashSet<Class<?>>();

        if (archive.isDirectory())
            spiderDirectory(archive, "", classes);
        else {
            ZipFile file = new ZipFile(archive);
            for (ZipEntry entry : Collections.list(file.entries()))
                addClass(classes, entry.getName());
        }
        init(classes);
    }

    private void spiderDirectory(File root, String path, Set<Class<?>> classes) {
        for (File file : new File(root, path).listFiles()) {
            String relativePath;
            if (path.equals(""))
                relativePath = file.getName();
            else
                relativePath = path + File.separator + file.getName();
            if (file.isDirectory())
                spiderDirectory(root, relativePath, classes);
            else
                addClass(classes, relativePath);
        }
    }

    private void addClass(Set<Class<?>> classes, String name) {
        try {
            if (name.endsWith(".class"))
                classes.add(loader.loadClass(name.substring(0, name.indexOf(".class")).replace('/', '.')));
        } catch (ClassNotFoundException ex) {
            throw new WTFException("A class that is in the archive was not found in the archive!! File a bug report!!");
        }
    }

    private void init(Collection<Class<?>> classes) throws InvalidWebsiteException {
        this.routes = RoutingTable.extract(classes);
    }

    public Response service(Request request) {
        try {
            Route route = routes.route(request);
            ControllerInfo controllerInfo = ControllerInfo.get(route.getController());
            Controller controller = controllerInfo.instantiate();
            controller.setRequest(request);
            controllerInfo.invoke(controller, route.getAction());
            return controller.getResponse();
        } catch (StatusCodeException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace(System.err);
            throw new StatusCodeException(StatusCode.INTERNAL_SERVER_ERROR, ex);
        }
    }

    public ClassLoader getClassLoader() {
        return loader;
    }
}
