@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    kotlin("jvm")

    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.boot.dm)
    alias(libs.plugins.kotlin.spring)
}

dependencies {
    implementation(libs.spring.starter.webflux)
    implementation(libs.spring.starter.validation)
    implementation(libs.swagger.annotations)
}