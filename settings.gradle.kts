rootProject.name = "schedule_constructor_service"

include("schedule_constructor_app", "schedule_constructor_api")

enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
