plugins {
    kotlin("jvm") version "2.0.21"
    `java-library`
    `maven-publish`
    signing
}

group = providers.gradleProperty("group").get()
version = providers.gradleProperty("version").get()

val projectUrl = "https://github.com/animalab-netizen/kotlin-drmanhatan"
val githubRepo = "animalab-netizen/kotlin-drmanhatan"

kotlin {
    jvmToolchain(22)
}

java {
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("com.squareup.okhttp3:okhttp:4.12.0")

    testImplementation(kotlin("test"))
    testImplementation("com.squareup.okhttp3:okhttp:4.12.0")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "drmanhatan"

            pom {
                name.set("drmanhatan")
                description.set("Observable event support for Kotlin applications without coupling to analytics vendors.")
                url.set(projectUrl)

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("animalab")
                        name.set("AnimaLab")
                    }
                }

                scm {
                    url.set(projectUrl)
                    connection.set("scm:git:$projectUrl.git")
                    developerConnection.set("scm:git:$projectUrl.git")
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/$githubRepo")
            credentials {
                username = providers.environmentVariable("GITHUB_ACTOR").orNull
                    ?: providers.gradleProperty("gpr.user").orNull
                password = providers.environmentVariable("GITHUB_TOKEN").orNull
                    ?: providers.gradleProperty("gpr.key").orNull
            }
        }

        maven {
            name = "Sonatype"
            val releasesUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            url = uri(
                if (version.toString().endsWith("SNAPSHOT")) snapshotsUrl else releasesUrl
            )
            credentials {
                username = providers.environmentVariable("OSSRH_USERNAME").orNull
                    ?: providers.gradleProperty("ossrhUsername").orNull
                password = providers.environmentVariable("OSSRH_PASSWORD").orNull
                    ?: providers.gradleProperty("ossrhPassword").orNull
            }
        }
    }
}

signing {
    val signingKey = providers.environmentVariable("SIGNING_KEY").orNull
        ?: providers.gradleProperty("signingKey").orNull
    val signingPassword = providers.environmentVariable("SIGNING_PASSWORD").orNull
        ?: providers.gradleProperty("signingPassword").orNull

    if (!signingKey.isNullOrBlank() && !signingPassword.isNullOrBlank()) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications)
    }
}
