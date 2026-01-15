# Milestone 2: Spring Framework & Spring Boot

## 🎯 Learning Objectives

By completing this milestone, you will:

- Master Spring's Dependency Injection and Inversion of Control
- Build production-ready RESTful APIs with Spring Boot
- Implement comprehensive security with Spring Security
- Work with Spring Data JPA for complex database operations
- Apply reactive programming patterns with Spring WebFlux
- Write effective tests with Spring Test framework
- Understand Spring's transaction management
- Implement event-driven architecture within Spring applications

## 📚 Core Concepts

### 1. Spring Framework Fundamentals
- Inversion of Control (IoC) Container
- Dependency Injection patterns (constructor, setter, field)
- Bean lifecycle and scopes
- ApplicationContext and BeanFactory
- Aspect-Oriented Programming (AOP)

### 2. Spring Boot
- Auto-configuration magic
- Starter dependencies
- Application properties and profiles
- Actuator for monitoring
- DevTools for development productivity

### 3. Spring Data JPA
- Repository pattern
- Query methods and custom queries
- Specifications for dynamic queries
- Pagination and sorting
- Entity relationships and cascading
- Transaction management

### 4. Spring Security
- Authentication vs Authorization
- JWT token-based authentication
- OAuth2 and OpenID Connect
- Method-level security
- CORS configuration
- Security best practices

### 5. Testing in Spring
- Unit testing with JUnit 5 and Mockito
- Integration testing with @SpringBootTest
- Web layer testing with MockMvc
- Repository testing with @DataJpaTest
- Test containers for database testing

## 🏗️ Project Structure

```
02-spring-framework/
├── src/main/java/com/antoniopedro/spring/
│   ├── EcommerceApplication.java
│   ├── config/                  # Configuration classes
│   │   ├── SecurityConfig.java
│   │   ├── JpaConfig.java
│   │   └── WebConfig.java
│   ├── domain/                  # Domain models
│   │   ├── User.java
│   │   ├── Product.java
│   │   ├── Order.java
│   │   └── OrderItem.java
│   ├── repository/              # Data access layer
│   │   ├── UserRepository.java
│   │   ├── ProductRepository.java
│   │   └── OrderRepository.java
│   ├── service/                 # Business logic
│   │   ├── UserService.java
│   │   ├── ProductService.java
│   │   ├── OrderService.java
│   │   └── AuthenticationService.java
│   ├── controller/              # REST endpoints
│   │   ├── UserController.java
│   │   ├── ProductController.java
│   │   ├── OrderController.java
│   │   └── AuthController.java
│   ├── dto/                     # Data Transfer Objects
│   │   ├── request/
│   │   └── response/
│   ├── security/                # Security components
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── UserPrincipal.java
│   ├── exception/               # Exception handling
│   │   ├── GlobalExceptionHandler.java
│   │   └── custom exceptions
│   └── event/                   # Event-driven components
│       ├── OrderCreatedEvent.java
│       └── OrderEventListener.java
├── src/main/resources/
│   ├── application.yml
│   ├── application-dev.yml
│   ├── application-prod.yml
│   └── db/migration/            # Flyway migrations
├── src/test/java/               # Comprehensive tests
└── docker-compose.yml           # PostgreSQL, Redis
```

## 🚀 Implementation: E-Commerce Backend API

### Core Features

#### 1. User Management
- User registration with validation
- Email verification
- Password hashing with BCrypt
- User profile management
- Role-based access control (USER, ADMIN)

#### 2. Product Catalog
- CRUD operations for products
- Product search and filtering
- Category management
- Inventory tracking
- Product reviews and ratings

#### 3. Order Processing
- Shopping cart management
- Order creation and validation
- Order status tracking
- Order history
- Payment integration (mock)

#### 4. Authentication & Authorization
- JWT-based authentication
- Refresh token mechanism
- Role-based access control
- API endpoint security
- Rate limiting

### API Endpoints

```
Authentication:
POST   /api/auth/register        - User registration
POST   /api/auth/login           - User login
POST   /api/auth/refresh         - Refresh access token
POST   /api/auth/logout          - Logout

Users:
GET    /api/users                - List users (ADMIN)
GET    /api/users/{id}           - Get user details
PUT    /api/users/{id}           - Update user
DELETE /api/users/{id}           - Delete user (ADMIN)

Products:
GET    /api/products             - List products (paginated, filterable)
GET    /api/products/{id}        - Get product details
POST   /api/products             - Create product (ADMIN)
PUT    /api/products/{id}        - Update product (ADMIN)
DELETE /api/products/{id}        - Delete product (ADMIN)
GET    /api/products/search      - Search products

Orders:
GET    /api/orders               - List user's orders
GET    /api/orders/{id}          - Get order details
POST   /api/orders               - Create order
PUT    /api/orders/{id}/cancel   - Cancel order
GET    /api/orders/admin         - All orders (ADMIN)
```

## 🛠️ Technical Implementation

### Database Schema

```sql
-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    enabled BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Products table
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INTEGER NOT NULL,
    category VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Orders table
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    status VARCHAR(50) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Order items table
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders(id),
    product_id BIGINT REFERENCES products(id),
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL
);
```

### Security Configuration

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/products/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(jwtAuthenticationFilter(), 
                UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

## 🧪 Testing Strategy

### Unit Tests (70% coverage)
```java
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private ProductService productService;
    
    @Test
    void shouldCreateProduct() {
        // Test implementation
    }
}
```

### Integration Tests (20% coverage)
```java
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void shouldReturnProductList() throws Exception {
        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk());
    }
}
```

### Repository Tests (10% coverage)
```java
@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    private ProductRepository repository;
    
    @Test
    void shouldFindProductsByCategory() {
        // Test implementation
    }
}
```

## 📊 Performance & Monitoring

### Spring Boot Actuator Endpoints
- `/actuator/health` - Health check
- `/actuator/metrics` - Application metrics
- `/actuator/info` - Application info
- `/actuator/prometheus` - Metrics for Prometheus

### Caching Strategy
```java
@Cacheable(value = "products", key = "#id")
public Product findById(Long id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));
}
```

## 🚀 Running the Application

### Start with Docker Compose
```bash
# Start PostgreSQL and Redis
docker-compose up -d

# Run the application
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Build and Package
```bash
# Build JAR
mvn clean package

# Run JAR
java -jar target/spring-ecommerce-api.jar

# Build Docker image
docker build -t spring-ecommerce-api .
```

## 📖 Study Resources

### Official Documentation
- [Spring Framework Reference](https://docs.spring.io/spring-framework/reference/)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/)

### Books
- *Spring in Action* by Craig Walls
- *Pro Spring 5* by Iuliana Cosmina et al.
- *Spring Boot: Up and Running* by Mark Heckler

### Tutorials
- [Baeldung Spring Tutorials](https://www.baeldung.com/spring-tutorial)
- [Spring Guides](https://spring.io/guides)

## 🎯 Success Criteria

You've mastered this milestone when you can:

- [ ] Explain Spring's IoC container and dependency injection
- [ ] Build RESTful APIs following best practices
- [ ] Implement secure authentication and authorization
- [ ] Write comprehensive tests achieving 80%+ coverage
- [ ] Optimize database queries with Spring Data JPA
- [ ] Handle exceptions gracefully with proper error responses
- [ ] Configure and use Spring profiles for different environments
- [ ] Implement caching to improve performance
- [ ] Monitor application health with Actuator
- [ ] Deploy a Spring Boot application to production

## 🔍 Common Pitfalls to Avoid

1. **N+1 Query Problem** - Use fetch joins or @EntityGraph
2. **Circular Dependencies** - Refactor code structure
3. **Overusing @Autowired** - Prefer constructor injection
4. **Ignoring Transactions** - Use @Transactional appropriately
5. **Poor Exception Handling** - Implement global exception handlers
6. **Hardcoded Secrets** - Use environment variables or secret managers
7. **Missing Validation** - Use Bean Validation (@Valid)
8. **Inefficient Queries** - Monitor and optimize SQL queries

## 🚀 Next Steps

After completing this milestone:
- Understanding of enterprise application patterns
- Production-ready REST API skills
- Security implementation expertise
- Testing proficiency

**Proceed to:** [Milestone 3: Clean Code & Clean Architecture](../03-clean-architecture)

---

**Estimated Time:** 3-4 weeks of dedicated study and implementation  
**Difficulty:** Intermediate to Advanced  
**Prerequisites:** Java fundamentals, basic SQL knowledge, REST API concepts
