/*
 * LOCKSS Spring Boot Conventions Plugin
 *
 * This plugin configures Spring Boot projects with:
 * - Spring Boot plugin and dependency management
 * - Fat JAR creation with dependencies
 * - Swagger/OpenAPI code generation
 * - Docker image building
 */

plugins {
    java
    `java-library`
    `maven-publish`
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("org.openapi.generator")
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
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
    testLogging {
        events("passed", "skipped", "failed")
        showExceptions = true
        showCauses = true
        showStackTraces = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
    systemProperty("org.lockss.defaultLogLevel", "info")
}

// Add generated sources to the main source set
sourceSets {
    main {
        java {
            srcDir(layout.buildDirectory.dir("generated/sources/openapi/src/main/java"))
        }
    }
}

springBoot {
    buildInfo()
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveClassifier.set("with-deps")
    launchScript()
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// Keep the regular JAR as well
tasks.named<Jar>("jar") {
    archiveClassifier.set("")
    enabled = true
}

// Extension for configuring OpenAPI code generation
interface OpenApiExtension {
    val specFile: RegularFileProperty
    val basePackage: Property<String>
    val skip: Property<Boolean>
}

val openapi = extensions.create<OpenApiExtension>("openapi")

// Set defaults
openapi.skip.convention(false)

// Configure OpenAPI Generator task
afterEvaluate {
    val specFile = openapi.specFile.orNull?.asFile
    val basePackage = openapi.basePackage.orNull

    if (specFile != null && specFile.exists() && basePackage != null && !openapi.skip.get()) {
        tasks.named<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiGenerate") {
            generatorName.set("spring")
            inputSpec.set(specFile.absolutePath)
            outputDir.set(layout.buildDirectory.dir("generated/sources/openapi").get().asFile.absolutePath)

            apiPackage.set("${basePackage}.api")
            modelPackage.set("${basePackage}.model")
            invokerPackage.set(basePackage)

            configOptions.set(mapOf(
                "sourceFolder" to "src/main/java",
                "delegatePattern" to "true",
                "useSpringBoot3" to "true",
                "useTags" to "false",
                "hideGenerationTimestamp" to "true",
                "openApiNullable" to "false",
                "documentationProvider" to "springdoc",
                "interfaceOnly" to "false",
                "skipDefaultInterface" to "false",
                "useJakartaEe" to "true"
            ))

            // Only generate API and model files, not supporting files
            globalProperties.set(mapOf(
                "apis" to "",
                "models" to "",
                "supportingFiles" to "ApiUtil.java"
            ))
        }

        // Register a post-processing task that runs swaggerProcessing.sh if it exists
        val postProcessOpenApi = tasks.register("postProcessOpenApi") {
            dependsOn("openApiGenerate")

            val scriptFile = file("scripts/swaggerProcessing.sh")
            val generatedDir = layout.buildDirectory.dir("generated/sources/openapi/src/main/java").get().asFile

            inputs.dir(generatedDir)
            outputs.dir(generatedDir)

            onlyIf { scriptFile.exists() }

            doLast {
                // Run the swaggerProcessing.sh script with modified paths
                val apiDir = File(generatedDir, "${basePackage.replace('.', '/')}/api")
                if (apiDir.exists()) {
                    project.exec {
                        workingDir = projectDir
                        environment("GENERATED_DIR", "build/generated/sources/openapi/src/main/java")
                        commandLine("bash", scriptFile.absolutePath)
                    }
                }
            }
        }

        // Make compileJava depend on postProcessOpenApi (which depends on openApiGenerate)
        tasks.named("compileJava") {
            dependsOn(postProcessOpenApi)
        }

        // Make sourcesJar depend on postProcessOpenApi as well
        tasks.named("sourcesJar") {
            dependsOn(postProcessOpenApi)
        }
    } else {
        // Disable the task if no spec file configured
        tasks.named<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiGenerate") {
            enabled = false
        }
    }
}

// Extension for Docker configuration
interface DockerExtension {
    val imageName: Property<String>
    val repository: Property<String>
    val tag: Property<String>
    val restPort: Property<Int>
    val uiPort: Property<Int>
}

val docker = extensions.create<DockerExtension>("docker")

// Docker build task (to be implemented by projects that need it)
tasks.register("dockerBuild") {
    group = "docker"
    description = "Builds Docker image"
    dependsOn("bootJar")

    onlyIf { docker.imageName.isPresent }

    doLast {
        logger.lifecycle("Docker image build should be configured per-project")
    }
}
