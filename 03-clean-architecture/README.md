# Milestone 3: Clean Code & Clean Architecture

## 🎯 Learning Objectives

By completing this milestone, you will:

- Master SOLID principles in real-world applications
- Implement Hexagonal Architecture (Ports & Adapters)
- Apply Domain-Driven Design (DDD) tactical patterns
- Write maintainable, testable, and scalable code
- Use design patterns appropriately
- Establish code quality gates and automation
- Document architectural decisions
- Refactor legacy code effectively

## 📚 Core Concepts

### 1. SOLID Principles
- **S**ingle Responsibility Principle
- **O**pen/Closed Principle
- **L**iskov Substitution Principle
- **I**nterface Segregation Principle
- **D**ependency Inversion Principle

### 2. Clean Architecture Layers
```
┌─────────────────────────────────────────────┐
│         Presentation Layer (Adapters)       │
│    (Controllers, REST APIs, Web UI)         │
├─────────────────────────────────────────────┤
│         Application Layer (Use Cases)       │
│    (Business Workflows, Orchestration)      │
├─────────────────────────────────────────────┤
│         Domain Layer (Entities)             │
│    (Business Logic, Domain Models)          │
├─────────────────────────────────────────────┤
│      Infrastructure Layer (Adapters)        │
│  (Database, External APIs, File System)     │
└─────────────────────────────────────────────┘
```

### 3. Hexagonal Architecture
- **Primary Ports** (driving) - Input to the application
- **Secondary Ports** (driven) - Output from the application
- **Primary Adapters** - REST controllers, CLI, GUI
- **Secondary Adapters** - Database, message queues, external APIs

### 4. Domain-Driven Design
- **Value Objects** - Immutable, compared by value
- **Entities** - Have identity, mutable
- **Aggregates** - Consistency boundaries
- **Domain Events** - Something that happened
- **Repositories** - Persistence abstraction
- **Domain Services** - Operations that don't belong to entities

### 5. Design Patterns
- Strategy Pattern
- Factory Pattern
- Repository Pattern
- Specification Pattern
- Command Pattern
- Observer Pattern

## 🏗️ Project: Banking System

A production-grade banking application demonstrating clean architecture principles.

### Domain Model

```
Banking Domain
├── Account Aggregate
│   ├── Account (Entity)
│   ├── AccountId (Value Object)
│   ├── Money (Value Object)
│   ├── Transaction (Entity)
│   └── AccountType (Enum)
├── Customer Aggregate
│   ├── Customer (Entity)
│   ├── CustomerId (Value Object)
│   └── Address (Value Object)
└── Domain Events
    ├── AccountCreated
    ├── MoneyDeposited
    ├── MoneyWithdrawn
    └── MoneyTransferred
```

### Project Structure

```
03-clean-architecture/
├── src/main/java/com/antoniopedro/banking/
│   ├── domain/                      # Core business logic
│   │   ├── model/
│   │   │   ├── Account.java
│   │   │   ├── Customer.java
│   │   │   ├── Transaction.java
│   │   │   └── valueobject/
│   │   │       ├── AccountId.java
│   │   │       ├── Money.java
│   │   │       ├── CustomerId.java
│   │   │       └── Address.java
│   │   ├── event/
│   │   │   ├── AccountCreated.java
│   │   │   └── MoneyTransferred.java
│   │   ├── exception/
│   │   │   ├── InsufficientFundsException.java
│   │   │   └── AccountNotFoundException.java
│   │   └── service/
│   │       └── InterestCalculator.java
│   ├── application/                 # Use cases
│   │   ├── port/
│   │   │   ├── input/              # Primary ports
│   │   │   │   ├── CreateAccountUseCase.java
│   │   │   │   ├── DepositMoneyUseCase.java
│   │   │   │   ├── WithdrawMoneyUseCase.java
│   │   │   │   └── TransferMoneyUseCase.java
│   │   │   └── output/             # Secondary ports
│   │   │       ├── AccountRepository.java
│   │   │       ├── CustomerRepository.java
│   │   │       └── EventPublisher.java
│   │   └── service/                # Use case implementations
│   │       ├── AccountService.java
│   │       └── TransferService.java
│   ├── adapter/                     # Adapters
│   │   ├── input/                  # Primary adapters
│   │   │   ├── rest/
│   │   │   │   ├── AccountController.java
│   │   │   │   └── dto/
│   │   │   └── cli/
│   │   │       └── BankingCLI.java
│   │   └── output/                 # Secondary adapters
│   │       ├── persistence/
│   │       │   ├── jpa/
│   │       │   │   ├── AccountJpaAdapter.java
│   │       │   │   ├── AccountEntity.java
│   │       │   │   └── JpaAccountRepository.java
│   │       │   └── inmemory/
│   │       │       └── InMemoryAccountRepository.java
│   │       └── messaging/
│   │           └── EventPublisherAdapter.java
│   └── config/                      # Configuration
│       ├── BeanConfiguration.java
│       └── SecurityConfiguration.java
├── src/test/java/
│   ├── domain/                      # Domain tests (80%)
│   ├── application/                 # Use case tests (15%)
│   └── adapter/                     # Adapter tests (5%)
├── docs/
│   ├── architecture-diagram.png
│   └── domain-model.md
├── adr/                             # Architecture Decision Records
│   ├── 0001-hexagonal-architecture.md
│   ├── 0002-value-objects-for-ids.md
│   └── 0003-event-sourcing-for-audit.md
└── README.md
```

## 🚀 Implementation Details

### Domain Layer Example

```java
// Value Object - Money
@Value
public class Money {
    BigDecimal amount;
    Currency currency;
    
    public Money add(Money other) {
        validateSameCurrency(other);
        return new Money(
            amount.add(other.amount),
            currency
        );
    }
    
    public boolean isGreaterThan(Money other) {
        validateSameCurrency(other);
        return amount.compareTo(other.amount) > 0;
    }
    
    private void validateSameCurrency(Money other) {
        if (!currency.equals(other.currency)) {
            throw new CurrencyMismatchException();
        }
    }
}

// Entity - Account
@Getter
public class Account {
    private final AccountId id;
    private Money balance;
    private final CustomerId ownerId;
    private final List<Transaction> transactions;
    
    public void deposit(Money amount) {
        validateAmount(amount);
        balance = balance.add(amount);
        transactions.add(Transaction.deposit(amount, Instant.now()));
    }
    
    public void withdraw(Money amount) {
        validateAmount(amount);
        if (!balance.isGreaterThan(amount)) {
            throw new InsufficientFundsException(id, balance, amount);
        }
        balance = balance.subtract(amount);
        transactions.add(Transaction.withdrawal(amount, Instant.now()));
    }
    
    private void validateAmount(Money amount) {
        if (amount.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(amount);
        }
    }
}
```

### Application Layer Example

```java
// Use Case Interface (Primary Port)
public interface TransferMoneyUseCase {
    TransferResult transfer(TransferCommand command);
}

// Use Case Implementation
@Service
@Transactional
@RequiredArgsConstructor
public class TransferService implements TransferMoneyUseCase {
    
    private final AccountRepository accountRepository;
    private final EventPublisher eventPublisher;
    
    @Override
    public TransferResult transfer(TransferCommand command) {
        // 1. Load aggregates
        Account sourceAccount = accountRepository.findById(command.sourceAccountId())
            .orElseThrow(() -> new AccountNotFoundException(command.sourceAccountId()));
            
        Account targetAccount = accountRepository.findById(command.targetAccountId())
            .orElseThrow(() -> new AccountNotFoundException(command.targetAccountId()));
        
        // 2. Execute business logic
        Money amount = new Money(command.amount(), Currency.USD);
        sourceAccount.withdraw(amount);
        targetAccount.deposit(amount);
        
        // 3. Persist changes
        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);
        
        // 4. Publish events
        eventPublisher.publish(new MoneyTransferred(
            sourceAccount.getId(),
            targetAccount.getId(),
            amount,
            Instant.now()
        ));
        
        return new TransferResult(true, "Transfer successful");
    }
}
```

### Adapter Layer Example

```java
// REST Controller (Primary Adapter)
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    
    private final CreateAccountUseCase createAccountUseCase;
    private final DepositMoneyUseCase depositMoneyUseCase;
    
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request) {
        
        var command = new CreateAccountCommand(
            request.customerId(),
            request.initialDeposit()
        );
        
        var result = createAccountUseCase.create(command);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(AccountResponse.from(result));
    }
    
    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<DepositResponse> deposit(
            @PathVariable String accountId,
            @Valid @RequestBody DepositRequest request) {
        
        var command = new DepositCommand(
            AccountId.of(accountId),
            request.amount()
        );
        
        var result = depositMoneyUseCase.deposit(command);
        
        return ResponseEntity.ok(DepositResponse.from(result));
    }
}
```

## 🧪 Testing Strategy

### Testing Pyramid

```
        ┌───────────┐
        │   E2E     │  5%
        ├───────────┤
        │Integration│ 15%
        ├───────────┤
        │   Unit    │ 80%
        └───────────┘
```

### Domain Tests (80%)
```java
class AccountTest {
    
    @Test
    void shouldDepositMoney() {
        // Given
        Account account = AccountTestBuilder.anAccount()
            .withBalance(Money.of(100, Currency.USD))
            .build();
        Money depositAmount = Money.of(50, Currency.USD);
        
        // When
        account.deposit(depositAmount);
        
        // Then
        assertThat(account.getBalance())
            .isEqualTo(Money.of(150, Currency.USD));
    }
    
    @Test
    void shouldThrowExceptionWhenInsufficientFunds() {
        // Given
        Account account = AccountTestBuilder.anAccount()
            .withBalance(Money.of(100, Currency.USD))
            .build();
        
        // When & Then
        assertThatThrownBy(() -> 
            account.withdraw(Money.of(150, Currency.USD))
        ).isInstanceOf(InsufficientFundsException.class);
    }
}
```

## 📐 Code Quality Tools

### Checkstyle Configuration
```xml
<!-- checkstyle.xml -->
<module name="Checker">
    <module name="TreeWalker">
        <module name="MethodLength">
            <property name="max" value="20"/>
        </module>
        <module name="ClassFanOutComplexity">
            <property name="max" value="20"/>
        </module>
    </module>
</module>
```

### SonarQube Quality Gates
- Code Coverage > 80%
- Duplicated Lines < 3%
- Maintainability Rating A
- Security Rating A
- Cyclomatic Complexity < 15

### ArchUnit Tests
```java
@AnalyzeClasses(packages = "com.antoniopedro.banking")
class ArchitectureTest {
    
    @ArchTest
    static final ArchRule domainShouldNotDependOnAdapters = 
        noClasses()
            .that().resideInPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInPackage("..adapter..");
    
    @ArchTest
    static final ArchRule adaptersShouldDependOnPorts = 
        classes()
            .that().resideInPackage("..adapter..")
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage("..application.port..", "..domain..", "java..");
}
```

## 📖 Architecture Decision Records

### ADR Template
```markdown
# ADR-0001: Use Hexagonal Architecture

## Status
Accepted

## Context
We need an architecture that promotes testability, maintainability, and 
independence from frameworks and external systems.

## Decision
Implement Hexagonal Architecture (Ports & Adapters pattern).

## Consequences
Positive:
- Domain logic isolated from infrastructure
- Easy to test business logic
- Can swap adapters without affecting core

Negative:
- More initial complexity
- More classes and interfaces
- Learning curve for team
```

## 🚀 Running the Application

```bash
# Build the project
mvn clean package

# Run tests with coverage
mvn clean verify jacoco:report

# Run Checkstyle
mvn checkstyle:check

# Run SpotBugs
mvn spotbugs:check

# Run the application
mvn spring-boot:run
```

## 📖 Study Resources

### Books
- *Clean Architecture* by Robert C. Martin
- *Domain-Driven Design* by Eric Evans
- *Implementing Domain-Driven Design* by Vaughn Vernon
- *Clean Code* by Robert C. Martin
- *Refactoring* by Martin Fowler

### Articles
- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [The Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [DDD Reference](https://www.domainlanguage.com/ddd/reference/)

## 🎯 Success Criteria

You've mastered this milestone when you can:

- [ ] Design systems following SOLID principles
- [ ] Implement hexagonal architecture from scratch
- [ ] Identify and apply appropriate design patterns
- [ ] Write rich domain models with business logic
- [ ] Maintain >80% code coverage
- [ ] Set up automated code quality gates
- [ ] Document architectural decisions with ADRs
- [ ] Refactor code to improve design
- [ ] Explain trade-offs of architectural choices

## 🔍 Key Takeaways

1. **Domain First** - Business logic should be framework-independent
2. **Dependencies Point Inward** - Outer layers depend on inner layers
3. **Ports & Adapters** - Define contracts, implement adapters
4. **Rich Domain Models** - Put logic where it belongs
5. **Value Objects** - Immutability and validation
6. **Automated Quality** - Make quality gates automatic
7. **Architecture Tests** - Test your architecture constraints
8. **Document Decisions** - ADRs capture the "why"

## 🚀 Next Steps

After mastering clean architecture:
- Build maintainable, testable systems
- Apply DDD patterns effectively
- Enforce architectural boundaries
- Write self-documenting code

**Proceed to:** [Milestone 4: Data-Intensive Applications](../04-data-intensive-apps)

---

**Estimated Time:** 3-4 weeks of dedicated study  
**Difficulty:** Advanced  
**Prerequisites:** Strong Java and OOP knowledge, design patterns basics
