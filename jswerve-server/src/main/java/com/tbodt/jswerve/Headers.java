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

import java.util.*;

/**
 * The HTTP headers.
 * 
 * @author Theodore Dubois
 */
public final class Headers {
    private final Map<String, String> headersMap;

    private Headers(Map<String, String> headers) {
        this.headersMap = Collections.unmodifiableMap(headers);
    }
    
    public static final class Builder {
        private final Map<String, String> headersMap;
        private boolean built = false;
        
        public Builder() {
            headersMap = new HashMap<String, String>();
        }

        public Builder(Headers headers) {
            headersMap = new HashMap<String, String>(headers.asMap());
        }
        
        public Builder setHeader(String name, String value) {
            headersMap.put(name, value);
            return this;
        }
        
        public Headers build() {
            if (built)
                throw new IllegalStateException();
            built = true;
            return new Headers(headersMap);
        }
    }

    public String get(String key) {
        return headersMap.get(key);
    }
    
    public Map<String, String> asMap() {
        return headersMap;
    }
}
