package com.antdev99.learning.http;

/**
 * An enumeration representing the different types of HTTP requests (e.g., GET, POST, PUT, DELETE).
 * This can be used to identify the type of HTTP request being processed in the application.
 */
public enum HttpRequestType {
    GET,
    POST,
    PUT,
    DELETE,
    HEAD,
    OPTIONS,
    PATCH,
    TRACE,
    CONNECT
}
