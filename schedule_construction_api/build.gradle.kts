@file:Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed

import com.google.protobuf.gradle.id

plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.grpc.compiler)
    alias(libs.plugins.detekt)
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.boot.dm)
    `maven-publish`
}

dependencies {
    implementation(libs.spring.starter.webflux)
    implementation(libs.grpc.kotlin)
    implementation(libs.grpc.java)
    implementation(libs.protobuf.kotlin)
    implementation(libs.protobuf.java)
    implementation(libs.grpc.protobuf)
    implementation(libs.spring.starter.validation)
    implementation(libs.swagger.annotations)

    implementation(platform(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES))
}

sourceSets {
    val main by getting { }
    main.java.srcDirs("build/generated/source/proto/main/kotlin")
    main.java.srcDirs("build/generated/source/proto/main/grpckt")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.22.2"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.52.1"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.3.0:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "ru.shedlab.schedule_construction"
            artifactId = "schedule_construction_api"
            from(components["kotlin"])
        }
    }
    repositories {
        maven {
            url = if (project.version.toString().endsWith("-SNAPSHOT")) {
                uri("https://${System.getenv("CI_ARTIFACT_REPO_HOST")}/repository/maven-snapshots")
            } else {
                uri("https://${System.getenv("CI_ARTIFACT_REPO_HOST")}/repository/maven-releases")
            }
            credentials {
                username = System.getenv("CI_ARTIFACT_REPO_NAME")
                password = System.getenv("CI_ARTIFACT_REPO_TOKEN")
            }
        }
    }
}
