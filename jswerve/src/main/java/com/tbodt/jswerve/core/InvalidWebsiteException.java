/* InvalidWebsiteException Copyright (C) 2014 Theodore Dubois.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; see the file COPYING.  If not, write to
 * the Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package com.tbodt.jswerve.core;

/**
 * Thrown if the website has some sort of problem that prevents it from being used.
 * 
 * @author Theodore Dubois
 */
public class InvalidWebsiteException extends Exception {

    /**
     * Creates a new instance of <code>InvalidWebsiteException</code> without detail message.
     */
    public InvalidWebsiteException() {
    }


    /**
     * Constructs an instance of <code>InvalidWebsiteException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public InvalidWebsiteException(String msg) {
        super(msg);
    }
}
