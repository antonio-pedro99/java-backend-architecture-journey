package com.antoniopedro.netty.http;

import io.netty.handler.codec.http.FullHttpRequest;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Simple router for HTTP requests.
 * Maps HTTP methods and paths to handlers.
 * 
 * @author Antonio Pedro
 */
public class Router {
    
    @Getter
    private final List<Route> routes = new ArrayList<>();
    
    public void get(String path, Function<FullHttpRequest, String> handler) {
        routes.add(new Route("GET", path, handler));
    }
    
    public void post(String path, Function<FullHttpRequest, String> handler) {
        routes.add(new Route("POST", path, handler));
    }
    
    public void put(String path, Function<FullHttpRequest, String> handler) {
        routes.add(new Route("PUT", path, handler));
    }
    
    public void delete(String path, Function<FullHttpRequest, String> handler) {
        routes.add(new Route("DELETE", path, handler));
    }
    
    public String handle(String method, String path, FullHttpRequest request) {
        return routes.stream()
                .filter(route -> route.matches(method, path))
                .findFirst()
                .map(route -> route.getHandler().apply(request))
                .orElseThrow(() -> new RouteNotFoundException("No route found for " + method + " " + path));
    }
    
    @Getter
    public static class Route {
        private final String method;
        private final String path;
        private final Function<FullHttpRequest, String> handler;
        
        public Route(String method, String path, Function<FullHttpRequest, String> handler) {
            this.method = method;
            this.path = path;
            this.handler = handler;
        }
        
        public boolean matches(String method, String path) {
            return this.method.equals(method) && this.path.equals(path);
        }
    }
}
