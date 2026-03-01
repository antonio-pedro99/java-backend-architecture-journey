package com.antdev99.learning.http.parser;

import com.antdev99.learning.http.MiniHttpRequest;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple HTTP parser
 */
public final class MiniHttpParser {
    private static final Logger logger = LoggerFactory.getLogger(MiniHttpParser.class);

    /**
     * Parses an HTTP request from the given ByteBuf.
     * The method reads the request line, headers, and body (if applicable) to construct an HttpRequest object.
     * @param buf the ByteBuf containing the raw HTTP request data
     * @return an HttpRequest object representing the parsed HTTP request
     */
    public static MiniHttpRequest parseRequest(ByteBuf buf) {
        logger.debug("Parsing HTTP request...");

        HttpRequestFirstLine firstLine = extractRequestFirstLine(buf);
        if (firstLine == null) {
            logger.error("Invalid HTTP request: Missing first line");
            throw new IllegalArgumentException("Invalid HTTP request: Missing first line");
        }

        logger.debug("Parsed request line: {} {} {}", firstLine.method(), firstLine.path(), firstLine.version());

        List<HttpHeaderLine> headers = extractHeaders(buf);
        if (headers.isEmpty()) {
            logger.error("Invalid HTTP request: Missing headers");
            throw new IllegalArgumentException("Invalid HTTP request: Missing headers");
        }

        logger.debug("Parsed {} headers", headers.size());

        HashMap<String, Object> headersMap = headers.stream().collect(
                HashMap::new,
                (map, header) -> map.put(header.name(), header.value()),
                HashMap::putAll
        );

        if (firstLine.method().equals("POST") || firstLine.method().equals("PUT")) {
            String body = readBody(buf);
            if (body.isBlank()) {
                logger.error("Invalid HTTP request: Missing body for {} request", firstLine.method());
                throw new IllegalArgumentException("Invalid HTTP request: Missing body for " + firstLine.method() + " request");
            }

            logger.debug("Parsed request body with {} bytes", body.length());

            return new MiniHttpRequest(firstLine.method(), firstLine.path(),
                    firstLine.version(), headersMap, body.getBytes());
        }

        logger.debug("Request parsing complete");
        return new MiniHttpRequest(firstLine.method(), firstLine.path(),
                firstLine.version(), headersMap, null);
    }

    private static HttpRequestFirstLine extractRequestFirstLine(ByteBuf buf) {
        while (buf.isReadable()) {
            String line = readLine(buf);
            if (line.isEmpty()) {
                break;
            }
            final HttpRequestLine processedLine = processLine(line);
            if (processedLine instanceof HttpRequestFirstLine requestLine) {
                return requestLine;
            }
        }
        return null;
    }

    private static List<HttpHeaderLine> extractHeaders(ByteBuf buf) {
        List<HttpHeaderLine> headers = new ArrayList<>();
        while (buf.isReadable()) {
            String line = readLine(buf);
            if (line.isEmpty()) {
                break;
            }
            final HttpRequestLine processedLine = processLine(line);
            if (processedLine instanceof HttpHeaderLine headerLine) {
                headers.add(headerLine);
            }
        }
        return headers;
    }

    private static HttpRequestLine processLine(String line) {
        if (line.startsWith("GET") || line.startsWith("POST") || line.startsWith("PUT") || line.startsWith("DELETE")) {
            String[] parts = line.split(" ");
            if (parts.length == 3) {
                String method = parts[0];
                String path = parts[1];
                String version = parts[2];
                return new HttpRequestFirstLine(method, path, version);
            }
        } else {
            String[] headerParts = line.split(": ", 2);
            if (headerParts.length == 2) {
                String headerName = headerParts[0];
                String headerValue = headerParts[1];

                return new HttpHeaderLine(headerName, headerValue);
            }
        }
        logger.debug("Invalid line format: {}", line);
        return null; // Invalid line
    }

    private static String readLine(ByteBuf buf) {
        StringBuilder sb = new StringBuilder();
        while (buf.isReadable()) {
            char c = (char) buf.readByte();
            if (c == '\r') {
                if (buf.isReadable() && (char) buf.getByte(buf.readerIndex()) == '\n') {
                    buf.readByte();
                }
                break; // EOL
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private static String readBody(ByteBuf buf) {
        StringBuilder sb = new StringBuilder();
        while (buf.isReadable()) {
            sb.append((char) buf.readByte());
        }
        return sb.toString();
    }
}
