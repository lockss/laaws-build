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
