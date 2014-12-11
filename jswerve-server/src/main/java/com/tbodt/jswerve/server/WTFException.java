/* WTFException Copyright (C) 2014 Theodore Dubois.
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

package com.tbodt.jswerve.server;

/**
 * An exception indicating that something completely unexpected and a bug report should be filed.
 * 
 * @author Theodore Dubois
 */
public class WTFException extends RuntimeException {
    /**
     * Constructs an instance of <code>WTFException</code> with the specified detail message.
     * @param msg what the f*** happened
     */
    public WTFException(String msg) {
        super(msg);
    }
}
