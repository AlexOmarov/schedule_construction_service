package ru.shedlab.scheduleconstructor.api.web

import java.util.UUID

data class Classroom(val id: UUID, val code: String, val type: ClassroomType)