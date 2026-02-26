package com.antdev99.learning.http;

import java.util.List;

/**
 * A simple representation of an HTTP response.
 */
public record MiniHttpResponse(String version, List<MiniHttpHeader> headers, StatusCode statusCode, String body) implements MiniHttpMessage {

    /**
     * Builder class for constructing HttpResponse instances in a fluent manner.
     */
    public static class Builder {
        private StatusCode statusCode;
        private String body;
        private List<MiniHttpHeader> headers;
        private String version = "HTTP/1.1"; // Default HTTP version

         /**
         * Sets the headers for the HTTP response.
         *
         * @param headers a list of MiniHttpHeader objects representing the response headers
         * @return the Builder instance for chaining
         */
        public Builder withHeaders(List<MiniHttpHeader> headers) {
            this.headers = headers;
            return this;
        }

        /**
         * Sets the HTTP version for the response.
         *
         * @param version the HTTP version (e.g., "HTTP/1.1")
         * @return the Builder instance for chaining
         */
        public Builder withVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Sets the HTTP status code for the response.
         *
         * @param statusCode the HTTP status code (e.g., 200, 404)
         * @return the Builder instance for chaining
         */
        public Builder withStatusCode(StatusCode statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        /**
         * Sets the body of the HTTP response.
         *
         * @param body the response body as a string
         * @return the Builder instance for chaining
         */
        public Builder withBody(String body) {
            this.body = body;
            return this;
        }

        /**
         * Builds and returns the HttpResponse instance.
         *
         * @return a new HttpResponse with the specified status code and body
         */
        public MiniHttpResponse build() {
            return new MiniHttpResponse(version, headers, statusCode, body);
        }
    }

    /**
     * An enumeration of common HTTP status codes and their corresponding reason phrases.
     */
    public enum StatusCode {
        OK(200, "OK"),
        NOT_FOUND(404, "Not Found"),
        INTERNAL_SERVER_ERROR(500, "Internal Server Error");

        private final int code;
        private final String reasonPhrase;

        StatusCode(int code, String reasonPhrase) {
            this.code = code;
            this.reasonPhrase = reasonPhrase;
        }

        public int code() {
            return code;
        }

        public String reasonPhrase() {
            return reasonPhrase;
        }
    }
}
