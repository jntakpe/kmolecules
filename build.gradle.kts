import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.22"
    id("org.jetbrains.dokka") version "1.7.20"
    `java-library`
    `maven-publish`
    signing
}

group = "com.github.jntakpe"
version = "1.0.0-RC3"

repositories {
    mavenCentral()
}

val junitVersion = "5.9.1"
val assertJVersion = "3.23.1"

dependencies {
    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
    withJavadocJar()
}

tasks {
    test {
        useJUnitPlatform()
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
    dokkaJavadoc.configure {
        outputDirectory.set(buildDir.resolve("javadoc"))
    }
    javadoc {
        dependsOn(dokkaJavadoc)
        setDestinationDir(buildDir.resolve("javadoc"))
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components.findByName("java"))
            pom {
                name.set(project.name)
                description.set("jMolecules types for Kotlin")
                url.set("https://github.com/jntakpe/kmolecules")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("jntakpe")
                        name.set("Jocelyn NTAKPE")
                    }
                }
                issueManagement {
                    system.set("Github issues")
                    url.set("https://github.com/jntakpe/kmolecules/issues")
                }
                ciManagement {
                    system.set("Github actions")
                    url.set("https://github.com/jntakpe/kmolecules/actions")
                }
                scm {
                    connection.set("scm:git:git@github.com:jntakpe/kmolecules.git")
                    developerConnection.set("scm:git:git@github.com:jntakpe/kmolecules.git")
                    url.set("https://github.com/jntakpe/kmolecules/")
                }
            }
        }
    }
    repositories {
        maven {
            name = "Maven_central"
            setUrl("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            credentials {
                val sonatypeUsername: String? by project
                val sonatypePassword: String? by project
                username = sonatypeUsername
                password = sonatypePassword
            }
        }
        maven {
            name = "Github_packages"
            setUrl("https://maven.pkg.github.com/jntakpe/kmolecules")
            credentials {
                val githubActor: String? by project
                val githubToken: String? by project
                username = githubActor
                password = githubToken
            }
        }
    }
}

signing {
    if ("CI" in System.getenv()) {
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
    }
    sign(publishing.publications["mavenJava"])
}