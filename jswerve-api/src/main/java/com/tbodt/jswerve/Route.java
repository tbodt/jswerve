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
    private final String[] pattern;
    private final EnumSet<Request.Method> methods;
    private final String controller;
    private final String action;

    Route(String[] pattern, EnumSet<Request.Method> methods, String controller, String action) {
        this.pattern = pattern;
        this.methods = methods;
        this.controller = controller;
        this.action = action;
    }

    public String[] getPattern() {
        return pattern;
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
