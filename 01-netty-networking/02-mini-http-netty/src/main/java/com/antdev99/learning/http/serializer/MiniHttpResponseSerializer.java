package com.antdev99.learning.http.serializer;

import com.antdev99.learning.http.MiniHttpResponse;

/**
 * A simple implementation of the HttpMessageSerializer interface for MiniHttpResponse.
 * This class is responsible for converting a MiniHttpResponse into its string representation.
 */
public class MiniHttpResponseSerializer implements HttpMessageSerializer<MiniHttpResponse> {

    @Override
    public String serialize(MiniHttpResponse message) {
        String headers = message.headers().entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .reduce((h1, h2) -> h1 + "\r\n" + h2)
                .orElse("");

        return message.version() + " " + message.statusCode().code() + " " + message.statusCode().reasonPhrase() + "\r\n"
                + (headers.isEmpty() ? "" : headers + "\r\n")
                + "\r\n"
                + (message.body() != null ? message.body() : "");
    }
}
