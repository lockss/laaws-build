/*
 * LOCKSS POM Bundle Conventions Plugin
 *
 * This plugin is for dependency-only projects (equivalent to Maven pom packaging).
 * These projects don't produce a JAR but instead define a set of dependencies
 * that other projects can depend on as a group.
 */

plugins {
    `java-platform`
    `maven-publish`
}

group = "org.lockss"

// Allow declaring dependencies in the platform
javaPlatform {
    allowDependencies()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["javaPlatform"])

            pom {
                name.set(project.name)
                description.set(project.description ?: project.name)
                url.set("https://www.lockss.org/")
                packaging = "pom"

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
                        id.set("lockss")
                        name.set("LOCKSS Program")
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
