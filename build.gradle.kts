import java.net.URI
import groovy.lang.Closure
import groovy.lang.GroovyObject
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.js.translate.context.Namer.kotlin
import org.jfrog.gradle.plugin.artifactory.dsl.ResolverConfig
import org.jfrog.gradle.plugin.artifactory.task.ArtifactoryTask
import org.owasp.dependencycheck.gradle.extension.DependencyCheckExtension

val springBootVersion = "2.0.3.RELEASE"

plugins {
    val kotlinVersion = "1.2.51"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    id("io.spring.dependency-management") version "1.0.5.RELEASE"
    id("com.jfrog.artifactory") version "4.7.3"
    id("org.owasp.dependencycheck") version "3.2.1"
}

//Adding this solves the issue
/*repositories {
    maven {
        url = URI("https://my-artifactory-uri")
        credentials {
            username = "user"
            password = "password"
        }
    }
}*/

subprojects {
    group = "com.beltram"
    version = "0.0.1-SNAPSHOT"

    dependencyManagement {
        imports {
            mavenBom("org.springframework.boot:spring-boot-starter-parent:$springBootVersion")
        }
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation(kotlin("reflect"))
        compile("org.springframework.boot:spring-boot-starter")
        testCompile("org.springframework.boot:spring-boot-starter-test")
    }

    artifactory {
        fun artifactoryRepo(repoKey: String) = delegateClosureOf<GroovyObject> {
            setProperty("repoKey", repoKey)
            setProperty("username", project.findProperty("artifactory_user"))
            setProperty("password", project.findProperty("artifactory_password"))
            setProperty("maven", true)
        }
        setContextUrl(project.findProperty("artifactory_contextUrl"))
        resolve(delegateClosureOf<ResolverConfig> {
            repository(artifactoryRepo("gradle-repo"))
        })
    }

    tasks {
        val build by tasks
        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "1.8"
                freeCompilerArgs = listOf("-Xjvm-default=compatibility")
            }
        }
        withType<Test> { useJUnitPlatform() }
        withType<ArtifactoryTask> { dependsOn(build) }
    }
}