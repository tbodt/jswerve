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

import java.util.regex.Pattern;

/**
 *
 * @author Theodore Dubois
 */
public abstract class AbstractPage implements Page {
    private final Request.Method method;
    private final Pattern pattern;

    protected AbstractPage(Request.Method method, Pattern pattern) {
        this.method = method;
        this.pattern = pattern;
    }

    @Override
    public boolean canService(Request request) {
        return request.getMethod() == method && pattern.matcher(request.getUri().toString()).matches();
    }
}
