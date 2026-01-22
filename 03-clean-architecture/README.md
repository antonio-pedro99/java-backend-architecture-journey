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
