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
package com.tbodt.jswerve.util;

import java.io.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Theodore Dubois
 */
public class ExpectableTest {
    @Test
    public void testExpecting() throws IOException {
        Reader expectIn = new StringReader("test line\nanother test line");
        Writer expectOut = new StringWriter();
        Expectable ex = new Expectable(expectIn, expectOut);

        ex.writeln("test stuff");
        assertEquals("test stuff\n", expectOut.toString());
        ex.write("more test stuff");
        assertEquals("test stuff\nmore test stuff", expectOut.toString());

        ex.expect("test line");
        ex.expect("another test");
    }
    
    @Test
    public void testExpectingExact() throws IOException {
        Reader expectIn = new StringReader("test line\nanother test line");
        Writer expectOut = new StringWriter();
        Expectable ex = new Expectable(expectIn, expectOut);

        ex.expectExact("test line");
        ex.expectExact("another test line");
    }

    @Test(expected = UnexpectedDataException.class)
    public void testUnexpecting() throws IOException {
        Reader expectIn = new StringReader("test line\nanother test line");
        Writer expectOut = new StringWriter();
        Expectable ex = new Expectable(expectIn, expectOut);
        
        ex.expect("this is not really there");
    }
    
    @Test(expected = UnexpectedDataException.class)
    public void testUnexpectingEOF() throws IOException {
        Reader expectIn = new StringReader("test line\nanother test line");
        Writer expectOut = new StringWriter();
        Expectable ex = new Expectable(expectIn, expectOut);
        
        ex.expect("test line\nanother test line\nthis doesn't exist");
    }
}
