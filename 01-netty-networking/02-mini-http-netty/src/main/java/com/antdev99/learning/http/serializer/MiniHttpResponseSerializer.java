package com.antdev99.learning.http.serializer;

import com.antdev99.learning.http.MiniHttpResponse;

/**
 * A simple implementation of the HttpMessageSerializer interface for MiniHttpResponse.
 * This class is responsible for converting a MiniHttpResponse into its string representation.
 */
public class MiniHttpResponseSerializer implements HttpMessageSerializer<MiniHttpResponse> {

    @Override
    public String serialize(MiniHttpResponse message) {
        return message.version() + " " + message.statusCode().code() + " " + message.statusCode().reasonPhrase() + "\r\n" +
                String.join("\r\n", message.headers().stream()
                        .map(header -> header.name() + ": " + header.value())
                        .toArray(String[]::new)) +
                "\r\n" +
                message.body();
    }
}
