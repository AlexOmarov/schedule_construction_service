package ru.shedlab.scheduleconstruction.infrastructure.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

object CoroutineUtils {
    val job = Job()
    val ioScope = CoroutineScope(Dispatchers.IO + job)
}
