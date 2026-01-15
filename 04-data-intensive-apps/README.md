# Milestone 4: Designing Data-Intensive Applications

## 🎯 Learning Objectives

By completing this milestone, you will:

- Design and implement event-driven architectures
- Apply CQRS (Command Query Responsibility Segregation) pattern
- Implement Event Sourcing for audit and temporal queries
- Build multi-tier caching strategies for performance
- Handle distributed system challenges (consistency, availability, partition tolerance)
- Implement circuit breakers and resilience patterns
- Set up distributed tracing and observability
- Optimize database performance with indexing and sharding strategies
- Work with message queues (Kafka, RabbitMQ)

## 📚 Core Concepts

### 1. Event-Driven Architecture
- Event-driven vs Request-driven systems
- Event sourcing and event stores
- Event processing patterns
- Eventually consistent systems
- Saga pattern for distributed transactions

### 2. CQRS Pattern
- Separating reads from writes
- Different models for queries and commands
- Projection building
- Eventual consistency handling
- Read model synchronization

### 3. Caching Strategies
- Cache-aside (lazy loading)
- Write-through cache
- Write-behind cache
- Refresh-ahead cache
- Cache invalidation strategies
- Multi-level caching (L1, L2, distributed)

### 4. Message-Driven Systems
- Message queues vs Event streams
- Apache Kafka fundamentals
- RabbitMQ patterns
- Consumer groups and partitioning
- Exactly-once delivery semantics
- Dead letter queues

### 5. Resilience Patterns
- Circuit Breaker pattern
- Retry with exponential backoff
- Bulkhead pattern
- Rate limiting
- Timeout handling
- Graceful degradation

### 6. Observability
- Distributed tracing (OpenTelemetry)
- Metrics collection (Prometheus)
- Log aggregation
- Health checks and readiness probes
- Application Performance Monitoring (APM)

## 🏗️ Project: Distributed Order Processing System

A scalable e-commerce order processing system demonstrating data-intensive application patterns.

### System Architecture

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Order API  │────▶│  Kafka/RMQ   │────▶│  Inventory   │
│  (Command)   │     │   Event Bus  │     │   Service    │
└──────────────┘     └──────────────┘     └──────────────┘
       │                     │                     │
       │                     │                     │
       ▼                     ▼                     ▼
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│  PostgreSQL  │     │    Redis     │     │   Payment    │
│  (Write DB)  │     │   (Cache)    │     │   Service    │
└──────────────┘     └──────────────┘     └──────────────┘
       │                     │                     │
       │                     │                     │
       ▼                     ▼                     ▼
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│  MongoDB     │     │   Zipkin     │     │ Notification │
│  (Read DB)   │     │  (Tracing)   │     │   Service    │
└──────────────┘     └──────────────┘     └──────────────┘
```

### Project Structure

```
04-data-intensive-apps/
├── order-service/               # Main service
│   ├── src/main/java/com/antoniopedro/dataapp/
│   │   ├── command/            # Write side (CQRS)
│   │   │   ├── api/
│   │   │   ├── domain/
│   │   │   └── infrastructure/
│   │   ├── query/              # Read side (CQRS)
│   │   │   ├── api/
│   │   │   ├── projection/
│   │   │   └── repository/
│   │   ├── event/              # Event definitions
│   │   │   ├── OrderCreatedEvent.java
│   │   │   ├── OrderConfirmedEvent.java
│   │   │   ├── OrderShippedEvent.java
│   │   │   └── OrderCancelledEvent.java
│   │   ├── saga/               # Saga orchestration
│   │   │   └── OrderSaga.java
│   │   ├── cache/              # Caching layer
│   │   │   ├── CacheConfig.java
│   │   │   └── CacheWarmer.java
│   │   └── resilience/         # Circuit breakers
│   │       ├── CircuitBreakerConfig.java
│   │       └── FallbackHandlers.java
├── inventory-service/           # Inventory management
├── payment-service/             # Payment processing
├── notification-service/        # Notifications
├── shared/                      # Shared libraries
│   ├── events/
│   ├── messaging/
│   └── observability/
├── docker-compose.yml           # Full stack setup
└── k8s/                        # Kubernetes manifests
```

## 🚀 Implementation Details

### Event Sourcing Implementation

```java
// Event Store
@Service
public class EventStore {
    private final EventRepository eventRepository;
    private final EventPublisher eventPublisher;
    
    public void append(String aggregateId, DomainEvent event) {
        EventEntity eventEntity = EventEntity.builder()
            .aggregateId(aggregateId)
            .eventType(event.getClass().getSimpleName())
            .eventData(serializeEvent(event))
            .version(getNextVersion(aggregateId))
            .timestamp(Instant.now())
            .build();
            
        eventRepository.save(eventEntity);
        eventPublisher.publish(event);
    }
    
    public List<DomainEvent> getEvents(String aggregateId) {
        return eventRepository.findByAggregateIdOrderByVersionAsc(aggregateId)
            .stream()
            .map(this::deserializeEvent)
            .collect(Collectors.toList());
    }
}

// Aggregate reconstruction from events
@Getter
public class Order {
    private OrderId id;
    private OrderStatus status;
    private List<OrderItem> items;
    private Money totalAmount;
    
    public static Order reconstruct(List<DomainEvent> events) {
        Order order = new Order();
        events.forEach(order::apply);
        return order;
    }
    
    private void apply(DomainEvent event) {
        if (event instanceof OrderCreatedEvent e) {
            this.id = e.getOrderId();
            this.items = e.getItems();
            this.totalAmount = e.getTotalAmount();
            this.status = OrderStatus.CREATED;
        } else if (event instanceof OrderConfirmedEvent e) {
            this.status = OrderStatus.CONFIRMED;
        } else if (event instanceof OrderShippedEvent e) {
            this.status = OrderStatus.SHIPPED;
        }
    }
}
```

### CQRS Implementation

```java
// Command Side
@Service
@RequiredArgsConstructor
public class OrderCommandService {
    private final EventStore eventStore;
    
    @Transactional
    public OrderId createOrder(CreateOrderCommand command) {
        // Validate command
        validateCommand(command);
        
        // Create aggregate
        OrderId orderId = OrderId.generate();
        Order order = Order.create(orderId, command.getItems());
        
        // Store events
        order.getUncommittedEvents().forEach(event -> 
            eventStore.append(orderId.getValue(), event)
        );
        
        return orderId;
    }
}

// Query Side - Read Model
@Document(collection = "order_views")
@Data
public class OrderView {
    @Id
    private String orderId;
    private String customerId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private List<OrderItemView> items;
    private Instant createdAt;
    private Instant updatedAt;
}

// Projection Builder
@Service
@RequiredArgsConstructor
public class OrderProjection {
    private final OrderViewRepository orderViewRepository;
    
    @EventListener
    public void on(OrderCreatedEvent event) {
        OrderView view = OrderView.builder()
            .orderId(event.getOrderId().getValue())
            .customerId(event.getCustomerId().getValue())
            .status(OrderStatus.CREATED)
            .totalAmount(event.getTotalAmount().getAmount())
            .items(toItemViews(event.getItems()))
            .createdAt(event.getOccurredAt())
            .build();
            
        orderViewRepository.save(view);
    }
    
    @EventListener
    public void on(OrderConfirmedEvent event) {
        orderViewRepository.findById(event.getOrderId().getValue())
            .ifPresent(view -> {
                view.setStatus(OrderStatus.CONFIRMED);
                view.setUpdatedAt(event.getOccurredAt());
                orderViewRepository.save(view);
            });
    }
}
```

### Multi-Level Caching

```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        return new CompositeCacheManager(
            caffeineCacheManager(),      // L1: In-memory
            redisCacheManager()          // L2: Distributed
        );
    }
    
    @Bean
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(Duration.ofMinutes(5))
            .recordStats());
        return cacheManager;
    }
    
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(1))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()));
                
        return RedisCacheManager.builder(factory)
            .cacheDefaults(config)
            .build();
    }
}

// Service with caching
@Service
@RequiredArgsConstructor
public class ProductQueryService {
    private final ProductViewRepository repository;
    
    @Cacheable(value = "products", key = "#id", unless = "#result == null")
    public Optional<ProductView> findById(String id) {
        return repository.findById(id);
    }
    
    @CachePut(value = "products", key = "#product.id")
    public ProductView save(ProductView product) {
        return repository.save(product);
    }
    
    @CacheEvict(value = "products", key = "#id")
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
```

### Circuit Breaker with Resilience4j

```java
@Configuration
public class CircuitBreakerConfig {
    
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .failureRateThreshold(50)
            .waitDurationInOpenState(Duration.ofSeconds(30))
            .slidingWindowSize(10)
            .minimumNumberOfCalls(5)
            .build();
            
        return CircuitBreakerRegistry.of(config);
    }
}

@Service
@RequiredArgsConstructor
public class PaymentServiceClient {
    private final CircuitBreaker circuitBreaker;
    private final RestTemplate restTemplate;
    
    public PaymentResult processPayment(PaymentRequest request) {
        return circuitBreaker.executeSupplier(() -> {
            try {
                return restTemplate.postForObject(
                    "/api/payments",
                    request,
                    PaymentResult.class
                );
            } catch (Exception e) {
                log.error("Payment service call failed", e);
                throw new PaymentServiceException(e);
            }
        });
    }
}
```

### Distributed Tracing

```java
@Configuration
public class TracingConfig {
    
    @Bean
    public Tracer tracer() {
        return OpenTelemetry.getGlobalTracer("order-service");
    }
}

@Service
@RequiredArgsConstructor
public class OrderService {
    private final Tracer tracer;
    
    public Order createOrder(CreateOrderCommand command) {
        Span span = tracer.spanBuilder("createOrder")
            .startSpan();
            
        try (Scope scope = span.makeCurrent()) {
            span.setAttribute("order.customerId", command.getCustomerId());
            span.setAttribute("order.itemCount", command.getItems().size());
            
            // Business logic
            Order order = processOrder(command);
            
            span.setStatus(StatusCode.OK);
            return order;
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }
}
```

## 🧪 Testing Strategy

### Event Sourcing Tests
```java
@Test
void shouldReconstructOrderFromEvents() {
    // Given
    List<DomainEvent> events = List.of(
        new OrderCreatedEvent(orderId, items, totalAmount),
        new OrderConfirmedEvent(orderId),
        new OrderShippedEvent(orderId, trackingNumber)
    );
    
    // When
    Order order = Order.reconstruct(events);
    
    // Then
    assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
}
```

### Integration Tests with TestContainers
```java
@SpringBootTest
@Testcontainers
class OrderServiceIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");
    
    @Container
    static KafkaContainer kafka = new KafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.5.0")
    );
    
    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
        .withExposedPorts(6379);
    
    @Test
    void shouldProcessOrderEndToEnd() {
        // Full integration test
    }
}
```

## 📊 Monitoring & Observability

### Metrics with Micrometer
```java
@Service
@RequiredArgsConstructor
public class MetricsService {
    private final MeterRegistry meterRegistry;
    
    public void recordOrderCreated(Order order) {
        Counter.builder("orders.created")
            .tag("status", order.getStatus().name())
            .register(meterRegistry)
            .increment();
            
        Timer.builder("orders.processing.time")
            .register(meterRegistry)
            .record(() -> processOrder(order));
    }
}
```

## 🚀 Running the System

### With Docker Compose
```bash
# Start all services
docker-compose up -d

# Check service health
docker-compose ps

# View logs
docker-compose logs -f order-service

# Stop all services
docker-compose down
```

### Accessing Services
- Order API: http://localhost:8080
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000
- Zipkin: http://localhost:9411
- Kafka UI: http://localhost:8081

## 📖 Study Resources

### Books
- **Designing Data-Intensive Applications** by Martin Kleppmann (Essential!)
- *Building Microservices* by Sam Newman
- *Release It!* by Michael Nygard
- *Enterprise Integration Patterns* by Gregor Hohpe

### Articles & Papers
- [CQRS Pattern](https://martinfowler.com/bliki/CQRS.html)
- [Event Sourcing](https://martinfowler.com/eaaDev/EventSourcing.html)
- [CAP Theorem](https://en.wikipedia.org/wiki/CAP_theorem)
- [Saga Pattern](https://microservices.io/patterns/data/saga.html)

### Online Resources
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Redis Documentation](https://redis.io/documentation)
- [Resilience4j Guide](https://resilience4j.readme.io/)

## 🎯 Success Criteria

You've mastered this milestone when you can:

- [ ] Design event-driven systems with proper event schemas
- [ ] Implement CQRS with separate read and write models
- [ ] Build event sourcing systems with proper snapshots
- [ ] Design multi-tier caching strategies
- [ ] Handle eventual consistency in distributed systems
- [ ] Implement circuit breakers and resilience patterns
- [ ] Set up comprehensive observability (logs, metrics, traces)
- [ ] Work with Kafka for event streaming
- [ ] Optimize database queries and implement sharding
- [ ] Handle distributed transactions with Saga pattern

## 🔍 Key Challenges & Solutions

### Challenge 1: Eventual Consistency
**Solution:** Implement compensating transactions, use saga pattern, provide clear user feedback

### Challenge 2: Message Ordering
**Solution:** Use partition keys in Kafka, implement idempotency, use sequence numbers

### Challenge 3: Cache Invalidation
**Solution:** Event-driven cache invalidation, TTL strategies, cache-aside pattern

### Challenge 4: Distributed Tracing Overhead
**Solution:** Sampling strategies, async logging, separate tracing infrastructure

## 🚀 Next Steps

Congratulations! You've completed all four milestones:

✅ Milestone 1: Netty Networking  
✅ Milestone 2: Spring Framework  
✅ Milestone 3: Clean Architecture  
✅ Milestone 4: Data-Intensive Applications

### Continue Your Journey
- Build your own projects combining all concepts
- Contribute to open-source projects
- Write technical blog posts about your learnings
- Mentor others on their backend journey
- Explore advanced topics: Kubernetes, Service Mesh, gRPC

---

**Estimated Time:** 4-5 weeks of dedicated study  
**Difficulty:** Advanced to Expert  
**Prerequisites:** All previous milestones, distributed systems basics
