import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.kotlin) // Sonarqube may break if this plugin will be in subprojects with version
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.detekt)
}

allprojects {
    group = "ru.schedlab"
}

var exclusions = project.properties["test_exclusions"].toString()

sonar {
    properties {
        property("sonar.qualitygate.wait", "true")
        property("sonar.core.codeCoveragePlugin", "jacoco")
        property(
            "sonar.kotlin.detekt.reportPaths",
            "${project(":schedule_construction_app").buildDir}/reports/detekt/detekt.xml, " +
                    "${project(":schedule_construction_api").buildDir}/reports/detekt/detekt.xml"
        )
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "${project(":schedule_construction_app").buildDir}/reports/jacoco/test/jacocoTestReport.xml"
        )
        property("sonar.cpd.exclusions", exclusions)
        property("sonar.jacoco.excludes", exclusions)
        property("sonar.coverage.exclusions", "$exclusions, **/schedule_construction_api/**")
    }
}

detekt {
    config =
        files("$projectDir/detekt-config.yml") // point to your custom config defining rules to run, overwriting default behavior
}

tasks.withType<Detekt>().configureEach {
    reports {
        xml.required.set(true) // checkstyle like format mainly for integrations like Jenkins
    }
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = "17"
}
tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "17"
}