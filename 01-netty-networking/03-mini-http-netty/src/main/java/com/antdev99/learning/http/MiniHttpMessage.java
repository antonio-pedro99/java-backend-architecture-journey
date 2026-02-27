package com.antdev99.learning.http;

/**
 * A representation of an HTTP message, which can be either an HTTP request or an HTTP response.
 * This is a marker interface that serves as a common supertype for both MiniHttpRequest and MiniHttpResponse.
 */
public sealed interface MiniHttpMessage permits MiniHttpRequest, MiniHttpResponse {
}
