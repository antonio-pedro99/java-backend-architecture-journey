package com.antdev99.learning.http.parser;

public record HttpHeaderLine(String name, Object value)
        implements HttpRequestLine {
}
