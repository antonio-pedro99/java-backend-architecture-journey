package com.antdev99.learning.http.parser;

/**
 * A record representing the first line of an HTTP request, which consists of the HTTP method, the request path, and the HTTP version.
 * For example, in the HTTP request line "GET /index.html HTTP/1.1", the method is "GET", the path is "/index.html", and the version is "HTTP/1.1".
 */
public record HttpRequestFirstLine(String method, String path, String version) implements HttpRequestLine {
}
