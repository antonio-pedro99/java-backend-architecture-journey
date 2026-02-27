package com.antdev99.learning.http.parser;

/**
 * A simple representation of an HTTP request line, which consists of the HTTP method, the requested path, and the HTTP version.
 * @param method the HTTP method (e.g., GET, POST, PUT, DELETE)
 * @param path the requested path (e.g., /index.html)
 * @param version the HTTP version (e.g., HTTP/1.1)
 */
public record HttpRequestLine(String method, String path, String version)
        implements HttpTopLine {
}
