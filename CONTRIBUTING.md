# Contributing to LOCKSS

## Git Layout

This Git repository uses the LOCKSS naming and policy conventions for Git Flow:

* The stable and development branches are `master` and `develop` respectively
* The prefixes for feature, release, hotfix, support and bugfix branches are `feature-`, `release-`, `hotfix-`, `support-` and `bugfix-` respectively
* The prefix for release tags is `version-`

Resources:
* https://danielkummer.github.io/git-flow-cheatsheet/
* https://github.com/petervanderdoes/gitflow-avh
* https://github.com/lockss/lockss-gitflow-init

## Development Setup

### Prerequisites

* JDK 17
* Gradle 8.11+ (included via wrapper)

### Building

```bash
# Clone with submodules
git clone --recurse-submodules https://github.com/lockss/laaws-build.git
cd laaws-build

# Build all projects
./gradlew build -x test

# Run tests
./gradlew test
```

### Working with Submodules

Each subdirectory (e.g., `lockss-core`, `laaws-configservice`) is a Git submodule with its own repository. To work on a specific project:

```bash
# Enter the submodule
cd laaws-configservice

# Create a feature branch
git checkout -b feature-my-feature develop

# Make changes and commit
git add .
git commit -m "Add my feature"

# Push to the submodule's remote
git push -u origin feature-my-feature
```

### Code Style

* Java code follows standard Java conventions
* Kotlin DSL for Gradle build scripts (`build.gradle.kts`)
* Use 4-space indentation (no tabs)

### Testing

```bash
# Run all tests
./gradlew test

# Run tests for a specific project
./gradlew :lockss-core:test

# Run a specific test class
./gradlew :lockss-core:test --tests "org.lockss.util.TestStringUtil"
```

### Pull Requests

1. Create a feature branch from `develop`
2. Make your changes with clear commit messages
3. Ensure all tests pass: `./gradlew build`
4. Push your branch and create a pull request against `develop`
