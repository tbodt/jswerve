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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author Theodore Dubois
 */
public final class Index {
    public Index(File site) throws IOException {
        ZipFile siteJar = new ZipFile(site);
        ZipEntry mappings = siteJar.getEntry("META-INF/index");
        BufferedReader mappingsReader = new BufferedReader(new InputStreamReader(siteJar.getInputStream(mappings)));

        String requestPattern;
        while ((requestPattern = mappingsReader.readLine()) != null) {
            String method;
            String pattern;

            String[] methodPattern = requestPattern.split(" ", 2);
            ensure(methodPattern.length == 2);
            method = methodPattern[0];
            pattern = methodPattern[1];
            
            String response = mappingsReader.readLine();
            ensure(response != null);
            
        }
    }
    private static void ensure(boolean condition) {
        if (!condition)
            throw new IllegalArgumentException("invalid syntax in index");
    }

    public static class IndexEntry {

    }
}
