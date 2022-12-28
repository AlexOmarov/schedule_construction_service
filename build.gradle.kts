@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.kotlin) // Sonarqube may break if this plugin will be in subprojects with version
}

allprojects {
    group = "ru.schedlab"
}