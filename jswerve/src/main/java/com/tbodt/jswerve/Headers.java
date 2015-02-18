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
import org.apache.commons.lang3.text.WordUtils;

/**
 * HTTP headers.
 *
 * This class does not include any code for parsing or formatting headers. It is only a convenient representation.
 *
 * @author Theodore Dubois
 */
public final class Headers implements Iterable<Map.Entry<String, String>> {
    /**
     * Empty headers.
     */
    public static final Headers EMPTY_HEADERS = new Headers(Collections.<String, String>emptyMap());

    private final Map<String, String> headersMap;

    private Headers(Map<String, String> headers) {
        this.headersMap = Collections.unmodifiableMap(headers);
    }

    /**
     * A builder for {@link Headers}. Use it like this:
     * <pre>{@code
     * Headers.Builder builder = Headers.builder();
     * builder.header("Content-Type", "matter-transport/sentient-life-form"); // see RFC 1437
     * Headers headers = builder.build();
     * }</pre>
     *
     * You can use invocation chaining to shorten that:
     * <pre>{@code
     * Headers headers = Headers.builder();
     *         .header("Content-Type", "matter-transport/sentient-life-form");
     *         .build();
     * }</pre>
     *
     * If you have an existing {@link Headers} instance, you can use {@link Headers#getBuilder()} to build upon it.
     *
     * @see Headers#getBuilder()
     */
    public static final class Builder {
        private final Map<String, String> headersMap;
        private boolean built = false;

        private Builder() {
            headersMap = new HashMap<String, String>();
        }

        private Builder(Headers headers) {
            headersMap = new HashMap<String, String>(headers.asMap());
        }

        /**
         * Add a header to the builder with the given name and value.
         *
         * @param name the name
         * @param value the value
         * @return {@code this} for invocation chaining
         */
        public Builder header(String name, String value) {
            headersMap.put(capitalizeHeader(name), value);
            return this;
        }

        /**
         * Add all the headers in {@code headers} to this builder. Equivalent to:
         * <pre>{@code
         * for (Map.Entry<String, String> header : headers)
         *     this.header(header.getKey(), header.getValue());
         * }</pre> But it's faster.
         *
         * @param headers
         * @return
         */
        public Builder setHeaders(Headers headers) {
            headersMap.putAll(headers.headersMap);
            return this;
        }

        /**
         * Build the final headers. If the method is called a second time, it throws {@code IllegalStateException}.
         *
         * @return the final headers!
         * @throws IllegalStateException if the method is called a second time
         */
        public Headers build() {
            if (built)
                throw new IllegalStateException();
            built = true;
            return new Headers(headersMap);
        }
    }

    /**
     * Create a new {@link Builder}. This is necessary because all the constructors are private.
     *
     * @return a new {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Create a new {@link Builder} with these headers as a starting point.
     *
     * @return a new {@link Builder} with these headers as a starting point
     */
    public Builder getBuilder() {
        return new Builder(this);
    }

    /**
     * Return the header value for the given name.
     *
     * @param name the header name
     * @return the header value for the given name
     */
    public String get(String name) {
        return headersMap.get(capitalizeHeader(name));
    }

    /**
     * Whether the header by the given name is defined.
     *
     * @param name the name
     * @return whether the header by the given name is defined
     */
    public boolean contains(String name) {
        return headersMap.containsKey(capitalizeHeader(name));
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return headersMap.entrySet().iterator();
    }

    /**
     * Return a map representing these headers. The result is unmodifiable.
     *
     * @return a map representing these headers
     */
    public Map<String, String> asMap() {
        return headersMap;
    }

    private static String capitalizeHeader(String header) {
        return WordUtils.capitalize(header, '-');
    }
}
