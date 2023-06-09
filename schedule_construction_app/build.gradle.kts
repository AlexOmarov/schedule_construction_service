import java.io.StringReader

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed

plugins {
    id("jacoco")
    kotlin("jvm")
    application

    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.boot.dm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.detekt)
}

var exclusions = project.properties["test_exclusions"].toString()

tasks.test { useJUnitPlatform() }

dependencies {
    implementation(project(":schedule_construction_api"))

    detektPlugins(libs.detekt.ktlint)

    implementation(libs.logstash.logback)

    implementation(libs.reactor.core)
    implementation(libs.hessian)

    implementation(libs.spring.starter.webflux)
    implementation(libs.spring.starter.rsocket)
    implementation(libs.spring.starter.security)
    //implementation(libs.spring.starter.security.rsocket)
    implementation(libs.spring.starter.kafka)
    implementation(libs.spring.starter.r2dbc)
    implementation(libs.spring.starter.validation)
    implementation(libs.spring.starter.aop)
    implementation(libs.spring.starter.grpc)
    implementation(libs.spring.starter.grpc.client)
    implementation(libs.spring.starter.cache)
    implementation(libs.spring.starter.actuator)

    implementation(libs.grpc.kotlin)

    implementation(libs.ehcache)

    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.coroutines.reactive)
    implementation(libs.kotlin.coroutines.reactor)
    implementation(libs.kotlin.extensions)
    implementation(libs.kotlin.jackson)

    implementation(libs.kafka)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.core)
    implementation(libs.jackson.annotations)

    implementation(libs.micrometr)
    implementation(libs.micrometr.reactor)
    implementation(libs.micrometr.tracing)


    implementation(libs.micrometr.tracing.otel)

    implementation(libs.shedlock)
    implementation(libs.shedlock.r2dbc)

    implementation(libs.openapi.webflux.ui)

    implementation(libs.flyway)
    runtimeOnly(libs.postgres)
    runtimeOnly(libs.postgres.r2dbc)

    testImplementation(libs.junit.api)
    testImplementation(libs.mockito)

    testImplementation(libs.reactor.test)
    testImplementation(libs.awaitility)
    testImplementation(libs.testcontainers.junit)
    testImplementation(libs.testcontainers.postgres)
    testImplementation(libs.testcontainers.kafka)
    testImplementation(libs.spring.starter.test)
    testImplementation(libs.spring.starter.test.kafka)
    testRuntimeOnly(libs.junit.engine)
}

springBoot {
    buildInfo()
}

tasks.bootJar {
    archiveFileName.set("${project.name}.jar")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events = setOf(
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
        )
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
    finalizedBy("jacocoTestReport") // Launch JaCoCo coverage verification
}

// Configure generated JaCoCo report
tasks.withType<JacocoReport> {
    reports {
        xml.required.set(true)
    }
    dependsOn("test")

    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) {
                    exclude(
                        exclusions.split(",")
                    )
                }
            }
        )
    )
    finalizedBy("coverage")
}

jacoco {
    toolVersion = "0.8.8"
}

// Print total coverage to console
tasks.register("coverage") {
    doLast {
        val testReportFile = project.file("${project.buildDir}/reports/jacoco/test/jacocoTestReport.xml")
        if (testReportFile.exists()) {
            val str: String = testReportFile.readText().replace("<!DOCTYPE[^>]*>".toRegex(), "")
            val rootNode = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(org.xml.sax.InputSource(StringReader(str)))
            var covered = 0
            var missed = 0

            val counters: org.w3c.dom.NodeList = javax.xml.xpath.XPathFactory.newInstance().newXPath().compile("//counter")
                .evaluate(rootNode, javax.xml.xpath.XPathConstants.NODESET) as org.w3c.dom.NodeList

            for (i in 0 until counters.length) {
                try {
                    covered += Integer.valueOf(counters.item(i).attributes.getNamedItem("covered").textContent)
                    missed += Integer.valueOf(counters.item(i).attributes.getNamedItem("missed").textContent)
                } catch (ignored: Exception) {
                }
            }

            // Test coverage parsing regex: Total:\s[\d\.\,]+%
            String.format("%.2f", 100.0 * covered / (missed + covered))
            println(
                "Coverage Total: ${
                    String.format(
                        "%.2f",
                        100.0 * covered / (missed + covered)
                    )
                }%"
            )
        }
    }
}
