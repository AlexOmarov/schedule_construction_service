package ru.shedlab.scheduleconstruction.arch

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AnalyzeClasses(packages = ["ru.shedlab.scheduleconstruction"])
class ArchUnitTests {
    @ArchTest
    fun `there are no package cycles`(importedClasses: JavaClasses) {
        SlicesRuleDefinition.slices()
            .matching("$BASE_PACKAGE.(**)..")
            .should()
            .beFreeOfCycles()
            .check(importedClasses)
    }

    companion object {
        private const val BASE_PACKAGE = "ru.shedlab.scheduleconstruction"
    }
}
