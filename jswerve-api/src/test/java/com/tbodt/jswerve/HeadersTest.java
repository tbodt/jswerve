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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Theodore Dubois
 */
public class HeadersTest {
    @Test
    public void testBuilding() {
        Headers.Builder builder = new Headers.Builder();
        builder.setHeader("Content-Type", "text/html");
        Headers headers = builder.build();
        assertEquals("text/html", headers.get("Content-Type"));
        
        builder = new Headers.Builder(headers);
        builder.setHeader("Connection", "close");
        headers = builder.build();
        assertEquals("text/html", headers.get("Content-Type"));
        assertEquals("close", headers.get("Connection"));
        
        builder = new Headers.Builder();
        builder.setHeaders(headers);
        builder.setHeader("Content-Length", "more than the atoms in the observable universe");
        headers = builder.build();
        assertEquals("text/html", headers.get("Content-Type"));
        assertEquals("close", headers.get("Connection"));
        assertEquals("more than the atoms in the observable universe", headers.get("Content-Length"));
    }    
}
