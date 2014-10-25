/* ReflectionException Copyright (C) 2014 Theodore Dubois.
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

package com.tbodt.jswerve;

/**
 * Thrown if a reflective operation fails.
 * 
 * @author Theodore Dubois
 */
public class ReflectionException extends Exception {
    /**
     * Constructs an instance of {@codeReflectionException} with the specified exception cause.
     * @param ex the
     */
    public ReflectionException(Exception ex) {
        super(ex);
    }
}
