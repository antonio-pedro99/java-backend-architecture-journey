package com.antoniopedro.netty.http;

/**
 * Exception thrown when no route matches the request.
 * 
 * @author Antonio Pedro
 */
public class RouteNotFoundException extends RuntimeException {
    public RouteNotFoundException(String message) {
        super(message);
    }
}
