package ru.shedlab.scheduleconstruction.config

import org.junit.jupiter.api.ClassDescriptor
import org.junit.jupiter.api.ClassOrderer
import org.junit.jupiter.api.ClassOrdererContext

internal class TestClassOrderer : ClassOrderer {

    override fun orderClasses(context: ClassOrdererContext?) {
        context!!.classDescriptors.sortWith(myCustomComparator)
    }

    private val myCustomComparator = Comparator<ClassDescriptor> { a, b ->
        when {
            (a.testClass.name == TestClassOrderer::class.java.name) -> return@Comparator -1
            (b.testClass.name == TestClassOrderer::class.java.name) -> return@Comparator 1
            (a.testClass.name == TestClassOrderer::class.java.name) -> return@Comparator -1
            (b.testClass.name == TestClassOrderer::class.java.name) -> return@Comparator 1
            else -> return@Comparator 0
        }
    }
}
