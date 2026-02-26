package com.antdev99.learning.http;

/**
 * A simple representation of an HTTP header, which consists of a header name and a header value.
 */
public record MiniHttpHeader(String name, String value) {
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
}
