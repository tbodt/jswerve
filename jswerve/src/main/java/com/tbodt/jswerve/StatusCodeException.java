/* HttpResponseException Copyright (C) 2014 Theodore Dubois.
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
 * An exception that requests a specific status code be sent in the response.
 *
 * @author Theodore Dubois
 */
public class StatusCodeException extends RuntimeException {
    private final StatusCode code;

    /**
     * Creates a {@code StatusCodeException} with the given status code.
     *
     * @param code the status code
     */
    public StatusCodeException(StatusCode code) {
        super(code.toString());
        this.code = code;
    }

    /**
     * Creates a {@code StatusCodeException} with the given status code and cause.
     *
     * @param code the status code
     * @param cause the cause
     */
    public StatusCodeException(StatusCode code, Throwable cause) {
        super(code.toString(), cause);
        this.code = code;
    }

    /**
     * Return the status code associated with this exception.
     * @return the status code associated with this exception
     */
    public StatusCode getStatusCode() {
        return code;
    }
}
