# Milestone 1: Networking in Java with Netty

## 🎯 Learning Objectives

By completing this milestone, you will:

- Understand the fundamentals of asynchronous, event-driven networking
- Master Netty's architecture: Bootstrap, Channel, Pipeline, Handler, EventLoop
- Build production-ready network servers with proper error handling
- Implement various protocols: HTTP, WebSocket, custom binary protocols
- Compare performance characteristics of different networking approaches
- Apply best practices for resource management and scalability

## 📚 Core Concepts

### 1. Event-Driven Architecture
- Non-blocking I/O and why it matters
- Reactor pattern implementation in Netty
- EventLoop and EventLoopGroup
- Threading model and performance implications

### 2. Channel Pipeline
- ChannelHandler chain of responsibility
- Inbound vs Outbound handlers
- Codec architecture (encoders/decoders)
- Custom protocol implementation

### 3. Netty Components
- Bootstrap (client) and ServerBootstrap
- Channel types and options
- ByteBuf memory management
- Channel futures and promises

## 🏗️ Project Structure

```
01-netty-networking/
├── src/main/java/com/antoniopedro/netty/
│   ├── http/                    # HTTP server implementation
│   │   ├── HttpServer.java
│   │   ├── HttpServerHandler.java
│   │   ├── Router.java
│   │   └── RouteHandler.java
│   ├── websocket/               # WebSocket server implementation
│   │   ├── WebSocketServer.java
│   │   ├── WebSocketFrameHandler.java
│   │   └── ChatRoom.java
│   ├── protocol/                # Custom protocol implementation
│   │   ├── CustomProtocolServer.java
│   │   ├── MessageEncoder.java
│   │   ├── MessageDecoder.java
│   │   └── Message.java
│   ├── tcp/                     # TCP server examples
│   │   ├── EchoServer.java
│   │   └── EchoServerHandler.java
│   └── udp/                     # UDP server examples
│       ├── UdpServer.java
│       └── UdpServerHandler.java
├── src/test/java/               # Comprehensive tests
├── docs/                        # Architecture diagrams, notes
├── performance/                 # Benchmark results
└── README.md
```

## 🚀 Implementations

### 1. HTTP Server with Routing
**File:** `http/HttpServer.java`

A production-ready HTTP/1.1 server featuring:
- Route-based request handling (GET, POST, PUT, DELETE)
- JSON request/response support
- Query parameter parsing
- Request body handling
- Proper HTTP status codes
- Connection keep-alive support

**Key Learning Points:**
- HTTP protocol implementation details
- Efficient routing mechanisms
- Content negotiation
- Error handling in network applications

### 2. WebSocket Real-Time Chat
**File:** `websocket/WebSocketServer.java`

A real-time chat application demonstrating:
- WebSocket handshake and upgrade
- Bidirectional communication
- Broadcasting to multiple clients
- Connection lifecycle management
- Heartbeat/ping-pong mechanism

**Key Learning Points:**
- WebSocket protocol specifics
- Managing stateful connections
- Handling connection drops gracefully
- Scalability considerations for real-time apps

### 3. Custom Binary Protocol
**File:** `protocol/CustomProtocolServer.java`

A custom protocol implementation showcasing:
- Protocol design (message framing, headers)
- Efficient serialization/deserialization
- Message type handling
- Protocol versioning
- Error recovery

**Key Learning Points:**
- Designing wire protocols
- ByteBuf manipulation
- Codec development
- Protocol evolution strategies

### 4. TCP Echo Server
**File:** `tcp/EchoServer.java`

A simple but instructive TCP server:
- Basic channel setup
- Reading and writing data
- Exception handling
- Resource cleanup

### 5. UDP Server
**File:** `udp/UdpServer.java`

Connectionless protocol handling:
- DatagramPacket handling
- Differences from TCP
- Use cases for UDP

## 📊 Performance Benchmarks

### Comparison Matrix

| Implementation | Requests/sec | Latency (p95) | Memory Usage | Connections |
|---------------|--------------|---------------|--------------|-------------|
| Netty HTTP    | TBD          | TBD           | TBD          | TBD         |
| Java NIO      | TBD          | TBD           | TBD          | TBD         |
| Traditional IO| TBD          | TBD           | TBD          | TBD         |

**Tools Used:**
- JMH (Java Microbenchmark Harness)
- Apache Bench (ab)
- wrk2 for HTTP benchmarking
- VisualVM for profiling

## 🧪 Testing Strategy

### Unit Tests
- Individual handler logic
- Codec encoding/decoding
- Message parsing
- Route matching

### Integration Tests
- Full server lifecycle
- Client-server communication
- Protocol conformance
- Error scenarios

### Load Tests
- Concurrent connections handling
- Throughput under load
- Memory leak detection
- Connection pooling

## 🛠️ Running the Examples

### Start HTTP Server
```bash
mvn compile exec:java -Dexec.mainClass="com.antoniopedro.netty.http.HttpServer"
```

Test with curl:
```bash
# GET request
curl http://localhost:8080/api/users

# POST request
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John","email":"john@example.com"}'
```

### Start WebSocket Server
```bash
mvn compile exec:java -Dexec.mainClass="com.antoniopedro.netty.websocket.WebSocketServer"
```

Test with websocat:
```bash
websocat ws://localhost:8081/chat
```

### Run Benchmarks
```bash
cd performance
./run-benchmarks.sh
```

## 📖 Study Resources

### Books
- **Netty in Action** by Norman Maurer - The definitive guide
- **Unix Network Programming** by W. Richard Stevens - Classic networking fundamentals

### Articles & Papers
- [Netty Best Practices](https://netty.io/wiki/user-guide-for-4.x.html)
- [The C10K Problem](http://www.kegel.com/c10k.html)
- [Reactor Pattern](https://en.wikipedia.org/wiki/Reactor_pattern)

### Videos
- [Netty: A Different Kind of Web Server](https://www.youtube.com/results?search_query=netty+tutorial)
- [Understanding Network I/O](https://www.youtube.com/results?search_query=network+io+blocking)

## 🎯 Success Criteria

You've mastered this milestone when you can:

- [ ] Explain the difference between blocking and non-blocking I/O
- [ ] Implement a custom protocol from scratch
- [ ] Debug network issues using tools like Wireshark
- [ ] Optimize a Netty application for throughput or latency
- [ ] Handle edge cases: slow clients, connection storms, malformed data
- [ ] Explain EventLoop threading model and when to use different configurations
- [ ] Write production-grade error handling and logging
- [ ] Benchmark and profile network applications

## 🔍 Common Pitfalls to Avoid

1. **Blocking the EventLoop** - Never perform blocking operations in handlers
2. **ByteBuf Leaks** - Always release ByteBufs or use try-with-resources
3. **Unbounded Buffers** - Implement backpressure mechanisms
4. **Shared State** - Be careful with concurrent access in handlers
5. **Thread Pool Misuse** - Understand when to use EventExecutor
6. **Connection Leaks** - Properly close channels and release resources

## 🚀 Next Steps

After completing this milestone, you'll have:
- Deep understanding of network programming
- Portfolio-worthy networking projects
- Foundation for understanding Spring's reactive stack
- Performance optimization skills

**Proceed to:** [Milestone 2: Spring Framework & Spring Boot](../02-spring-framework)

---

**Estimated Time:** 2-3 weeks of dedicated study and implementation  
**Difficulty:** Intermediate to Advanced  
**Prerequisites:** Basic Java knowledge, understanding of network concepts
