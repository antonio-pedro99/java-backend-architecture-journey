# Contributing to Java Backend Architecture Journey

Thank you for your interest in contributing to this learning journey! This document provides guidelines and information for contributors.

## 🎯 Project Purpose

This repository serves as a portfolio-grade learning journey for intermediate to senior Java backend developers. The goal is to demonstrate production-quality implementations, not quick tutorials or toy examples.

## 🤝 How to Contribute

### Types of Contributions

1. **Bug Fixes** - Fix errors in code or documentation
2. **Improvements** - Enhance existing implementations
3. **Examples** - Add new examples within existing milestones
4. **Documentation** - Improve explanations and guides
5. **Tests** - Add or improve test coverage

### What We're NOT Looking For

- Complete rewrites without discussion
- New milestones (the four milestones are fixed)
- Overly simplified examples
- Dependency updates without clear benefits
- Style changes without architectural value

## 📋 Contribution Process

### 1. Open an Issue First

Before starting work, please open an issue to discuss:
- What you want to contribute
- Why it adds value
- How it aligns with the learning objectives

### 2. Fork and Create a Branch

```bash
# Fork the repository on GitHub
git clone https://github.com/YOUR_USERNAME/java-backend-architecture-journey.git
cd java-backend-architecture-journey

# Create a feature branch
git checkout -b feature/your-feature-name
```

### 3. Make Your Changes

Follow these guidelines:

#### Code Quality
- Follow existing code style and conventions
- Write clean, self-documenting code
- Add appropriate comments for complex logic
- Ensure all tests pass
- Add tests for new functionality
- Maintain or improve code coverage

#### Documentation
- Update relevant README files
- Add JavaDoc for public APIs
- Include examples where appropriate
- Update architecture diagrams if needed

#### Commits
- Write clear, descriptive commit messages
- Use conventional commits format:
  ```
  feat: add WebSocket authentication example
  fix: correct Netty memory leak in HTTP handler
  docs: improve CQRS pattern explanation
  test: add integration tests for order service
  ```

### 4. Test Your Changes

```bash
# Run all tests
mvn clean test

# Run specific module tests
cd 01-netty-networking
mvn test

# Check code quality
mvn checkstyle:check
mvn spotbugs:check

# Verify build
mvn clean install
```

### 5. Submit a Pull Request

1. Push your branch to your fork
2. Open a Pull Request against `main` branch
3. Fill out the PR template completely
4. Link related issues
5. Wait for review

#### Pull Request Template

```markdown
## Description
Brief description of changes

## Milestone
Which milestone does this affect?
- [ ] Milestone 1: Netty Networking
- [ ] Milestone 2: Spring Framework
- [ ] Milestone 3: Clean Architecture
- [ ] Milestone 4: Data-Intensive Apps
- [ ] General/Documentation

## Type of Change
- [ ] Bug fix
- [ ] New feature/example
- [ ] Documentation update
- [ ] Code improvement
- [ ] Test addition

## Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex code
- [ ] Documentation updated
- [ ] Tests added/updated
- [ ] All tests pass
- [ ] No new warnings introduced

## Testing
Describe how you tested your changes

## Screenshots (if applicable)
Add screenshots for UI changes
```

## 💻 Development Setup

### Prerequisites
- JDK 17 or higher
- Maven 3.9+
- Docker and Docker Compose
- Git
- IDE (IntelliJ IDEA recommended)

### Local Setup

```bash
# Clone the repository
git clone https://github.com/antonio-pedro99/java-backend-architecture-journey.git
cd java-backend-architecture-journey

# Build all modules
mvn clean install

# Start infrastructure
docker-compose up -d

# Run specific milestone
cd 01-netty-networking
mvn exec:java
```

### IDE Configuration

#### IntelliJ IDEA
1. Import as Maven project
2. Enable annotation processing (for Lombok)
3. Set JDK 17 as project SDK
4. Install recommended plugins:
   - Lombok
   - SonarLint
   - CheckStyle-IDEA

#### VS Code
1. Install Java Extension Pack
2. Install Spring Boot Extension Pack
3. Configure Java SDK path
4. Enable format on save

## 📐 Code Style Guidelines

### Java Style
- Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Maximum line length: 120 characters
- Use meaningful variable names
- Prefer composition over inheritance
- Keep methods small and focused

### Architecture Principles
- Follow SOLID principles
- Maintain clear separation of concerns
- Dependencies point inward (Clean Architecture)
- Business logic in domain layer
- Infrastructure in adapters

### Testing
- Write tests before or with code (TDD encouraged)
- Use meaningful test names: `shouldDoSomethingWhenCondition()`
- Follow AAA pattern: Arrange, Act, Assert
- Mock external dependencies
- Aim for 80%+ coverage on new code

### Documentation
- Public APIs must have JavaDoc
- Complex algorithms need explanatory comments
- README files use clear structure
- Include code examples in documentation

## 🔍 Review Process

### What Reviewers Look For

1. **Code Quality**
   - Follows style guidelines
   - Well-tested
   - No code smells
   - Appropriate design patterns

2. **Documentation**
   - Clear and accurate
   - Helpful examples
   - Updated diagrams

3. **Learning Value**
   - Demonstrates best practices
   - Production-quality code
   - Educational value

### Review Timeline
- Initial review: Within 5 business days
- Follow-up: Within 2 business days
- Merge: After approval and CI passes

## 🙏 Recognition

Contributors will be:
- Listed in the repository contributors
- Mentioned in release notes
- Acknowledged in documentation

## 📬 Questions?

- Open an issue for questions
- Use discussions for general topics
- Email for private matters

## 📄 License

By contributing, you agree that your contributions will be licensed under the MIT License.

---

**Thank you for helping make this learning journey better for everyone!**
