plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.2.2")
    implementation("io.swagger.codegen.v3:swagger-codegen-maven-plugin:3.0.52")
    implementation("com.palantir.gradle.docker:gradle-docker:0.36.0")
    // Eclipse Transformer for javax to jakarta migration
    implementation("org.eclipse.transformer:org.eclipse.transformer:0.5.0")
    implementation("org.eclipse.transformer:org.eclipse.transformer.cli:0.5.0")
    // OpenAPI Generator for Swagger/OpenAPI code generation
    implementation("org.openapitools:openapi-generator-gradle-plugin:7.10.0")
}
