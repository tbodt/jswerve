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

import com.tbodt.jswerve.util.UrlUtils;
import java.nio.charset.Charset;
import java.util.*;

/**
 * A MIME message. Named {@code Content} because that's shorter.
 *
 * @author Theodore Dubois
 */
public final class Content {
    private final byte[] data;
    private final String mimeType;
    private final Map<String, String> mimeParameters;

    /**
     * Empty content, 0 bytes long and with no MIME type.
     */
    public static Content EMPTY = new Content(new byte[0], null);

    /**
     * Construct a {@code Content} with the given data and MIME type. The mime type is parsed for MIME parameters.
     *
     * @param data the data
     * @param mimeType the MIME type
     */
    public Content(byte[] data, String mimeType) {
        this.data = data;
        if (mimeType != null && mimeType.contains(";")) {
            this.mimeType = mimeType.substring(0, mimeType.indexOf(';')).toLowerCase();
            mimeParameters = decodeMimeParameters(mimeType.substring(mimeType.indexOf(';') + 1));
        } else {
            this.mimeType = mimeType;
            mimeParameters = Collections.emptyMap();
        }
    }

    private static Map<String, String> decodeMimeParameters(String parameters) {
        Map<String, String> map = new HashMap<String, String>();
        StringTokenizer tok = new StringTokenizer(parameters, ";");
        while (tok.hasMoreTokens()) {
            String parameter = tok.nextToken().trim();
            int equalIndex = parameter.indexOf('=');
            if (equalIndex == -1)
                continue;
            String key = UrlUtils.decode(parameter.substring(0, equalIndex)).trim().toLowerCase();
            String value = UrlUtils.decode(parameter.substring(equalIndex + 1));
            if (key.length() == 0)
                continue;
            if (value.startsWith("\"") && value.endsWith("\""))
                value = value.substring(1, value.length() - 1);
            map.put(key, value);
        }
        return Collections.unmodifiableMap(map);

    }

    /**
     * If the data is HTML form parameters, parse it as such and return the result. If it is not, return an empty map.
     * 
     * @return the data, parsed as HTML form parameters
     */
    public Map<String, String> parseFormParameters() {
        if (mimeType != null && mimeType.equals("application/x-www-form-urlencoded"))
            return UrlEncodedFormParser.parse(new String(getData(), Charset.forName("US-ASCII")));
        else
            return Collections.emptyMap();
    }

    /**
     * Return the data. The resulting array can be modified without fear of the apocalypse.
     *
     * @return the data
     */
    public byte[] getData() {
        return data.clone();
    }

    /**
     * Return the MIME type, with the parameters removed.
     *
     * @return the MIME type, with the parameters removed
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Return the MIME parameters.
     *
     * @return the MIME parameters
     */
    public Map<String, String> getMimeParameters() {
        return Collections.unmodifiableMap(mimeParameters);
    }
}
