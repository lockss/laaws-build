/*
 * LOCKSS Java Conventions Plugin
 *
 * This plugin replaces the functionality of lockss-parent-pom for Java projects.
 * It configures:
 * - Java compilation settings
 * - Test configuration with JUnit
 * - JAR manifest configuration
 * - Source/Javadoc artifact generation
 * - Common dependency configurations
 */

plugins {
    java
    `java-library`
    `maven-publish`
}

group = "org.lockss"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.isIncremental = true
}

tasks.withType<Javadoc>().configureEach {
    options.encoding = "UTF-8"
    (options as StandardJavadocDocletOptions).apply {
        addBooleanOption("Xdoclint:none", true)
        addStringOption("Xmaxwarns", "1")
    }
    isFailOnError = false
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()

    // Fork configuration similar to Maven
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)

    // Test output configuration
    testLogging {
        events("passed", "skipped", "failed")
        showExceptions = true
        showCauses = true
        showStackTraces = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }

    // System properties for tests
    systemProperty("org.lockss.defaultLogLevel", "info")
    systemProperty("java.io.tmpdir", System.getProperty("java.io.tmpdir"))

    // Keep temp files property
    if (project.hasProperty("keeptempfiles")) {
        systemProperty("org.lockss.keepTempFiles", "true")
    }
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Built-By" to System.getProperty("user.name"),
            "Build-Jdk" to System.getProperty("java.version")
        )
    }
}

// Configure test JAR
val testJar by tasks.registering(Jar::class) {
    archiveClassifier.set("tests")
    from(sourceSets["test"].output)
}

// Create a configuration for test artifacts that other projects can depend on
val testArtifacts by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
    extendsFrom(configurations["testImplementation"])
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, Usage.JAVA_RUNTIME))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class.java, Category.LIBRARY))
        attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements::class.java, LibraryElements.JAR))
    }
}

// Add testJar to testArtifacts configuration
artifacts {
    add("testArtifacts", testJar)
}

// Only publish test jar if explicitly configured
if (project.findProperty("publishTestJar") == true) {
    artifacts {
        add("archives", testJar)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set(project.name)
                description.set(project.description ?: project.name)
                url.set("https://www.lockss.org/")
                inceptionYear.set("2000")

                licenses {
                    license {
                        name.set("BSD-3-Clause")
                        url.set("https://opensource.org/licenses/BSD-3-Clause")
                    }
                }

                organization {
                    name.set("LOCKSS Program")
                    url.set("https://www.lockss.org/")
                }

                developers {
                    developer {
                        id.set("clairegriffin")
                        name.set("Claire Griffin")
                        organization.set("LOCKSS Program")
                    }
                    developer {
                        id.set("thibgc")
                        name.set("Thib Guicherd-Callin")
                        organization.set("LOCKSS Program")
                    }
                    developer {
                        id.set("tlipkis")
                        name.set("Tom Lipkis")
                        organization.set("LOCKSS Program")
                    }
                    developer {
                        id.set("dlvargas")
                        name.set("Daniel Vargas")
                        organization.set("LOCKSS Program")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/lockss/${project.name}.git")
                    developerConnection.set("scm:git:ssh://github.com/lockss/${project.name}.git")
                    url.set("https://github.com/lockss/${project.name}")
                }
            }
        }
    }
}

// Add generated sources directory
sourceSets {
    main {
        java {
            srcDir("src/generated/java")
        }
    }
}

// Clean generated sources
tasks.named("clean") {
    doLast {
        delete("src/generated")
    }
}

// Generate build.properties file for BuildInfo class
// This replicates the Maven antrun buildInfo step
val generateBuildInfo by tasks.registering(WriteProperties::class) {
    group = "build"
    description = "Generate build.properties for BuildInfo"

    val buildInfoDir = project.layout.buildDirectory.dir("resources/main/org/lockss/componentresources")
    destinationFile.set(buildInfoDir.map { it.file("build.properties") })

    // Get git info if available
    val gitBranch = providers.exec {
        commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
        isIgnoreExitValue = true
    }.standardOutput.asText.map { it.trim() }.orElse("unknown")

    val gitCommit = providers.exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
        isIgnoreExitValue = true
    }.standardOutput.asText.map { it.trim() }.orElse("unknown")

    val gitDirty = providers.exec {
        commandLine("git", "status", "--porcelain")
        isIgnoreExitValue = true
    }.standardOutput.asText.map { it.trim().isNotEmpty().toString() }.orElse("unknown")

    val hostname = providers.exec {
        commandLine("hostname")
        isIgnoreExitValue = true
    }.standardOutput.asText.map { it.trim() }.orElse("unknown")

    // Project properties
    property("build.name", project.name)
    property("build.description", project.description ?: project.name)
    property("build.groupId", project.group.toString())
    property("build.artifactId", project.name)
    property("build.version", project.version.toString())
    property("build.packaging", "jar")

    // Parent info (use root project as "parent")
    property("build.parent.groupId", rootProject.group.toString())
    property("build.parent.artifactId", rootProject.name)
    property("build.parent.version", rootProject.version.toString())
    property("build.parent.packaging", "pom")

    // Build environment
    // Use BuildTimestamp helper for formatted timestamp (same format as Maven's antrun step)
    val buildTimeMillis = BuildTimestamp.nowMillis()
    property("build.timestamp", BuildTimestamp.format(buildTimeMillis))
    property("build.rawtimestamp", buildTimeMillis.toString())
    // build.releasename must be just the version (e.g., "2.10.0-SNAPSHOT") for DaemonVersion parsing
    property("build.releasename", project.version.toString())
    property("build.host", hostname.get())
    property("build.user.name", System.getProperty("user.name"))
    property("build.os.name", System.getProperty("os.name"))
    property("build.os.arch", System.getProperty("os.arch"))
    property("build.os.version", System.getProperty("os.version"))

    // Java info
    property("build.java.version", System.getProperty("java.version"))
    property("build.java.class.version", System.getProperty("java.class.version"))
    property("build.java.vendor", System.getProperty("java.vendor"))
    property("build.java.home", System.getProperty("java.home"))
    property("build.java.specification.version", System.getProperty("java.specification.version"))
    property("build.java.specification.vendor", System.getProperty("java.specification.vendor"))
    property("build.java.specification.name", System.getProperty("java.specification.name"))
    property("build.java.vm.version", System.getProperty("java.vm.version"))
    property("build.java.vm.vendor", System.getProperty("java.vm.vendor"))
    property("build.java.vm.name", System.getProperty("java.vm.name"))
    property("build.java.vm.specification.version", System.getProperty("java.vm.specification.version"))
    property("build.java.vm.specification.vendor", System.getProperty("java.vm.specification.vendor"))
    property("build.java.vm.specification.name", System.getProperty("java.vm.specification.name"))

    // Git info
    property("build.git.branch", gitBranch.get())
    property("build.git.commit", gitCommit.get())
    property("build.git.dirty", gitDirty.get())

    comment = "Build information generated by Gradle"
}

// Ensure build.properties is generated before processResources
tasks.named("processResources") {
    dependsOn(generateBuildInfo)
}
