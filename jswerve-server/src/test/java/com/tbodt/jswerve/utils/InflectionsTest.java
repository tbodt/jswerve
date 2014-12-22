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
package com.tbodt.jswerve.utils;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Theodore Dubois
 */
public class InflectionsTest {
    @Test
    public void testCamelize() {
        assertEquals("Camel", Inflections.camelize("camel"));
        assertEquals("Camel", Inflections.camelize("Camel"));
        assertEquals("Camel", Inflections.camelize("cAMEl"));
        assertEquals("CamelCase", Inflections.camelize("camel-case"));
        assertEquals("CamelCase", Inflections.camelize("caMel-casE"));
        assertEquals("CamelCase", Inflections.camelize("camel_case"));
        assertEquals("CamelCase", Inflections.camelize("caMel_casE"));
    }
}
