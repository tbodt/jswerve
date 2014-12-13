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

import java.util.EnumSet;

/**
 *
 * @author Theodore Dubois
 */
public final class Route {
    private final String path;
    private final EnumSet<Request.Method> methods;
    private final String controller;
    private final String action;

    public static final class Builder {
        private final String path;
        private EnumSet<Request.Method> methods;
        private String controller;
        private String action;
        private boolean built = false;

        public Builder(String path) {
            this.path = path;
        }

        public Builder to(String controller, String action) {
            if (controller != null || action != null)
                throw new IllegalStateException("to has already been specified");
            this.controller = controller;
            this.action = action;
            return this;
        }

        public Builder via(Request.Method first, Request.Method... rest) {
            if (methods != null)
                throw new IllegalStateException("Via has already been specified");
            methods = EnumSet.of(first, rest);
            return this;
        }

        public Route build() {
            if (methods == null)
                throw new IllegalStateException("Must specify via");
            if (controller == null || action == null)
                throw new IllegalStateException("Must specify to");
            if (built)
                throw new IllegalStateException("Already been built");
            built = true;
            return new Route(path, methods, controller, action);
        }
    }

    private Route(String path, EnumSet<Request.Method> methods, String controller, String action) {
        this.path = path;
        this.methods = methods;
        this.controller = controller;
        this.action = action;
    }

    public String getPath() {
        return path;
    }

    public EnumSet<Request.Method> getMethods() {
        return methods;
    }

    public String getController() {
        return controller;
    }

    public String getAction() {
        return action;
    }
}
