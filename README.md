# LOCKSS 2.0 Build Environment

This project brings together Git repositories and build infrastructure to build the LOCKSS 2.x system. The project supports both Gradle (recommended) and Maven build systems.

## Quick Start (Gradle)

```bash
# Clone the repository and submodules
git clone --recurse-submodules https://github.com/lockss/laaws-build.git
cd laaws-build

# Build all projects (skip tests for faster build)
./gradlew build -x test

# Build a specific Spring Boot service
./gradlew :laaws-configservice:bootJar

# Run tests
./gradlew test
```

## Prerequisites

*   Java Development Kit (JDK) 17
*   **For Gradle builds**: Gradle 8.11+ (included via wrapper - no installation needed)
*   **For Maven builds**: [Apache Maven](https://maven.apache.org/) 3.9
*   [yq](https://github.com/mikefarah/yq) version 4 (for helper scripts)
    *   Fedora Linux 42/41/Rawhide, OpenSUSE Leap 15.6/Tumbleweed: the `yq` OS package
    *   Arch Linux: the `go-yq` OS package
    *   AlmaLinux OS/CentOS Stream/RHEL/Rocky Linux 9/10: the `yq` [EPEL](https://docs.fedoraproject.org/en-US/epel/) package
    *   Oracle Linux 10/9: the `yq` [OL EPEL](https://yum.oracle.com/) package
    *   Other Linux distributions: with a [binary download](https://github.com/mikefarah/yq/?tab=readme-ov-file#download-the-latest-binary), with [Wget](https://github.com/mikefarah/yq/?tab=readme-ov-file#wget), with [Homebrew](https://github.com/mikefarah/yq/?tab=readme-ov-file#macos--linux-via-homebrew), with [Snap](https://github.com/mikefarah/yq/?tab=readme-ov-file#linux-via-snap)
    *   MacOS: `yq` with [Homebrew](https://github.com/mikefarah/yq/?tab=readme-ov-file#macos--linux-via-homebrew) or [MacPorts](https://github.com/mikefarah/yq/?tab=readme-ov-file#macports)
    *   [All installation instructions](https://github.com/mikefarah/yq/?tab=readme-ov-file#install)
*   Optional prerequisites
    *   `etags` from Emacs
    *   `dot` from Graphviz
    *   `ps2pdf` from Ghostscript

In LOCKSS-related Git repositories, ongoing development occurs in the `develop` branch (the `master` branch is for stable releases only).

## Project Structure

```
laaws-build/
├── buildSrc/                    # Gradle convention plugins
├── gradle/libs.versions.toml    # Centralized dependency versions
├── lockss-util/                 # Utility libraries
│   ├── lockss-util-core/
│   ├── lockss-util-entities/
│   └── lockss-util-rest/
├── lockss-core/                 # Core LOCKSS library
├── lockss-plugin-compat/        # Plugin compatibility layer
├── lockss-spring-bundle/        # Spring Boot common components
├── lockss-pom-bundles/          # Dependency BOMs
├── lockss-tdb-tools/            # TDB processing tools
│   ├── lockss-tdb-processor/
│   └── lockss-tdbxml-gradle-plugin/
├── laaws-configservice/         # Configuration service (Spring Boot)
├── laaws-crawler-service/       # Crawler service (Spring Boot)
├── laaws-metadataservice/       # Metadata service (Spring Boot)
├── laaws-metadataextractor-common/
├── laaws-poller/                # Poller service (Spring Boot)
├── laaws-repository-service/    # Repository service (Spring Boot)
├── laaws-repository-tools/
├── laaws-soap-service/          # SOAP service (Spring Boot)
└── bin/                         # Helper scripts
```

## Usage (Gradle - Recommended)

### Building

```bash
# Build all projects
./gradlew build

# Build without tests (faster)
./gradlew build -x test

# Build a specific project
./gradlew :lockss-core:build
./gradlew :laaws-configservice:bootJar

# Clean build
./gradlew clean build
```

### Common Tasks

```bash
# List all available tasks
./gradlew tasks

# Generate OpenAPI code for a service
./gradlew :laaws-configservice:openApiGenerate

# Run tests for a specific project
./gradlew :lockss-core:test

# Generate Javadoc
./gradlew javadoc

# Check dependencies
./gradlew dependencies
```

### Spring Boot Services

Each Spring Boot service produces an executable JAR:

```bash
# Build all service JARs
./gradlew bootJar

# Run a service locally
java -jar laaws-configservice/build/libs/laaws-configservice-*.jar
```

## Usage (Maven - Legacy)

### One-Time Bootstrapping

1.  `bin/clone develop`
2.  `( cd lockss-parent-pom && mvn install )`

### Running the Build

1. `bin/foreach git pull`
2. `mvn clean`
3. `mvn install -DskipTests=true`
4. `mvn test`
