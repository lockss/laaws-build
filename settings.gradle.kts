/*
 * Copyright (c) 2000-2025, Board of Trustees of Leland Stanford Jr. University
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

rootProject.name = "laaws-build"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        // LOCKSS Legacy JARs
        maven {
            url = uri("https://github.com/lockss/lockss-legacy-jars/raw/master/maven2")
            content {
                includeGroup("org.lockss.legacy")
                includeGroup("org.lockss")
            }
            metadataSources {
                artifact()  // Only use artifacts, skip POM metadata (some have typos)
            }
        }
        // Sonatype OSSRH Snapshots
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
            mavenContent {
                snapshotsOnly()
            }
        }
    }
}

// POM bundle projects (these are dependency-only projects, equivalent to pom packaging)
include("lockss-pom-bundles:lockss-junit4-bundle")
include("lockss-pom-bundles:lockss-junit5-bundle")
include("lockss-pom-bundles:lockss-core-bundle")
include("lockss-pom-bundles:lockss-core-tests-bundle")
include("lockss-pom-bundles:lockss-plugins-parent-pom")

// Utility libraries
include("lockss-util:lockss-util-core")
include("lockss-util:lockss-util-entities")
include("lockss-util:lockss-util-rest")

// TDB tools
include("lockss-tdb-tools:lockss-tdb-processor")
include("lockss-tdb-tools:lockss-tdbxml-gradle-plugin")

// Core libraries
include("lockss-core")
include("lockss-plugin-compat")

// Spring bundle
include("lockss-spring-bundle")

// LAAWS Services
include("laaws-repository-service")
include("laaws-repository-tools")
include("laaws-configservice")
include("laaws-poller")
include("laaws-crawler-service")
include("laaws-metadataextractor-common")
include("laaws-metadataservice")
include("laaws-soap-service")
