/*
 * Copyright (C) 2015 Theodore Dubois
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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 *
 * @author Theodore Dubois
 */
class UrlEncodedFormParser {
    public static Map<String, String> parse(String encoded) {
        try {
            Map<String, String> map = new HashMap<String, String>();
            StringTokenizer tok = new StringTokenizer(encoded, "&");
            while (tok.hasMoreTokens()) {
                String parameter = tok.nextToken();
                int equalIndex = parameter.indexOf('=');
                if (equalIndex == -1)
                    continue;
                String key = URLDecoder.decode(parameter.substring(0, equalIndex), "UTF-8");
                String value = URLDecoder.decode(parameter.substring(equalIndex + 1), "UTF-8");
                if (key.length() == 0)
                    continue;
                map.put(key, value);
            }
            return Collections.unmodifiableMap(map);
        } catch (UnsupportedEncodingException ex) {
            throw new WTFException("unsupported encoding name came from a real charset object!", ex);
        }
    }

    private UrlEncodedFormParser() {
    }
}
