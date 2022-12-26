@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.boot.dm)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.spring)
    application
}

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
