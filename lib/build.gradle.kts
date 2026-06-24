plugins {
    kotlin("jvm") version "2.0.21"
    `java-library`
    `maven-publish`
}

group = providers.gradleProperty("group").get()
version = providers.gradleProperty("version").get()

val projectUrl = "https://github.com/animalab-netizen/kotlin-drmanhatan"
val pomOrganizationUrl = providers.gradleProperty("organizationUrlKotlinDrManhatan").orNull
val scmUrl = providers.gradleProperty("scmUrlKotlinDrManhatan").orNull
val scmConnection = providers.gradleProperty("scmConnectionKotlinDrManhatan").orNull
val scmDeveloperConnection = providers.gradleProperty("scmDeveloperConnectionKotlinDrManhatan").orNull
val publicationRepositoryUrl = providers.gradleProperty("publicationRepositoryUrl").orNull
val publicationRepositoryUsername = providers.gradleProperty("publicationRepositoryUsername").orNull
    ?: providers.environmentVariable("MAVEN_USERNAME").orNull
val publicationRepositoryPassword = providers.gradleProperty("publicationRepositoryPassword").orNull
    ?: providers.environmentVariable("MAVEN_PASSWORD").orNull

kotlin {
    explicitApi()
    jvmToolchain(11)
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
            artifactId = "kotlin-drmanhatan"

            pom {
                name.set("kotlin-drmanhatan")
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
                        email.set("animalab.desenvolvimento@gmail.com")
                        organization.set("AnimaLab")
                        if (!pomOrganizationUrl.isNullOrBlank()) {
                            organizationUrl.set(pomOrganizationUrl)
                        }
                    }
                }

                organization {
                    name.set("AnimaLab")
                    if (!pomOrganizationUrl.isNullOrBlank()) {
                        url.set(pomOrganizationUrl)
                    }
                }

                scm {
                    url.set(scmUrl ?: projectUrl)
                    connection.set(scmConnection ?: "scm:git:$projectUrl.git")
                    developerConnection.set(scmDeveloperConnection ?: "scm:git:git@github.com:animalab-netizen/kotlin-drmanhatan.git")
                }
            }
        }
    }

    repositories {
        mavenLocal()

        if (!publicationRepositoryUrl.isNullOrBlank()) {
            maven {
                name = "MavenRepository"
                url = uri(publicationRepositoryUrl)
                credentials {
                    username = publicationRepositoryUsername
                    password = publicationRepositoryPassword
                }
            }
        }
    }
}
