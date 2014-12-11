/*
 * Copyright (C) 2014 duboist
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

import com.tbodt.jswerve.Match;
import com.tbodt.jswerve.Request;
import java.lang.annotation.Annotation;

/**
 *
 * @author duboist
 */
public final class Route {
    private final String path;
    private final Request.Method method;
    public Route(Annotation annotation) {
        if (annotation instanceof Match) {
            Match match = (Match) annotation;
            path = match.path();
            method = match.method();
        }
    }
}
