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
}

var exclusions = project.properties["test_exclusions"].toString()

tasks.test { useJUnitPlatform() }

dependencies {
    implementation(project(":schedule_construction_api"))

    implementation(libs.spring.starter.webflux)
    implementation(libs.spring.starter.r2dbc)
    implementation(libs.spring.starter.validation)

    implementation(libs.openapi.webflux.ui)
    implementation(libs.openapi.security)
    implementation(libs.openapi.kotlin)

    implementation(libs.flyway)
    runtimeOnly(libs.postgres)
    runtimeOnly(libs.postgres.r2dbc)

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
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

// Configure JaCoCo verification rules
/*    tasks.withType<JacocoCoverageVerification> {
        violationRules {
            rule {
                limit {
                    minimum = "0.7".toBigDecimal()
                }
            }
        }
    }*/

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

sonarqube  {
    properties {
        property("sonar.projectKey", "AlexOmarov_schedule_construction_service")
        property("sonar.organization", "alexomarov")
        property("sonar.qualitygate.wait", "true")
        property("sonar.core.codeCoveragePlugin", "jacoco")
        property("sonar.coverage.jacoco.xmlReportPaths", "${project.buildDir}/reports/jacoco/test/jacocoTestReport.xml")
        property("sonar.cpd.exclusions", exclusions)
        property("sonar.jacoco.excludes", exclusions)
        property("sonar.coverage.exclusions", exclusions)
    }
}


// Print total coverage to console
tasks.register("coverage") {
    doLast {
        val testReportFile = project.file("${project.buildDir}/reports/jacoco/test/jacocoTestReport.xml")
        if (testReportFile.exists()) {
            val str: String = testReportFile.readText().replace("<!DOCTYPE[^>]*>".toRegex(), "")
            val rootNode = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(org.xml.sax.InputSource(StringReader(str)))
            var totalCovered = 0
            var totalMissed = 0

            val counters: org.w3c.dom.NodeList = javax.xml.xpath.XPathFactory.newInstance().newXPath().compile("//counter")
                .evaluate(rootNode, javax.xml.xpath.XPathConstants.NODESET) as org.w3c.dom.NodeList

            for (i in 0 until counters.length) {
                try {
                    totalCovered += Integer.valueOf(counters.item(i).attributes.getNamedItem("covered").textContent)
                    totalMissed += Integer.valueOf(counters.item(i).attributes.getNamedItem("missed").textContent)
                } catch (ignored: Exception) {
                }
            }

            // Test coverage parsing regex: Total:\s[\d\.\,]+%
            String.format("%.2f", 100.0 * totalCovered / (totalMissed + totalCovered))
            println(
                "Coverage Total: ${
                    String.format(
                        "%.2f",
                        100.0 * totalCovered / (totalMissed + totalCovered)
                    )
                }%"
            )
        }
    }
}
