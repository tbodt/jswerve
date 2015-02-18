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

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Theodore Dubois
 */
public class HeadersTest {
    @Test
    public void testBuilding() {
        Headers.Builder builder = Headers.builder();
        builder.header("Content-Type", "text/html");
        Headers headers = builder.build();
        assertEquals("text/html", headers.get("Content-Type"));

        builder = headers.getBuilder();
        builder.header("Connection", "close");
        headers = builder.build();
        assertEquals("text/html", headers.get("Content-Type"));
        assertEquals("close", headers.get("Connection"));

        builder = Headers.builder();
        builder.setHeaders(headers);
        builder.header("Content-Length", "more than the atoms in the observable universe");
        headers = builder.build();
        assertEquals("text/html", headers.get("Content-Type"));
        assertEquals("close", headers.get("Connection"));
        assertEquals("more than the atoms in the observable universe", headers.get("Content-Length"));
    }

    @Test(expected = IllegalStateException.class)
    public void testBuildingFailure() {
        Headers.Builder builder = Headers.builder();
        builder.build();
        builder.build();
    }

    @Test
    public void testUsing() {
        Headers.Builder builder = Headers.builder();
        builder.header("Connection", "close");
        builder.header("Content-Type", "text/html");
        Headers headers = builder.build();

        assertTrue(headers.contains("Connection"));
        assertTrue(headers.contains("Content-Type"));

        assertEquals("close", headers.get("Connection"));
        assertEquals("text/html", headers.get("Content-Type"));

        for (Map.Entry<String, String> header : headers)
            if (header.getKey().equalsIgnoreCase("Connection"))
                assertEquals("close", header.getValue());
            else if (header.getKey().equalsIgnoreCase("Content-Type"))
                assertEquals("text/html", header.getValue());
            else
                fail("Header key I didn't put there");
        
        Map<String, String> headerValues = new HashMap<String, String>();
        headerValues.put("Connection", "close");
        headerValues.put("Content-Type", "text/html");
        assertEquals(headerValues, headers.asMap());
    }
}
