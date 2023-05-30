rootProject.name = "schedule_construction_service"

include("schedule_construction_app", "schedule_construction_api")


dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
    }
}
