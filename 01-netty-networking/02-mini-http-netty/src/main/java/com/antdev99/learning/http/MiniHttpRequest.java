package com.antdev99.learning.http;

import java.util.HashMap;

/**
 * A simple representation of an HTTP request.
 */
public record MiniHttpRequest(String method, String path, String version,
       HashMap<String, Object> headers, byte[] body) implements MiniHttpMessage {
}
