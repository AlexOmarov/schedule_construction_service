package ru.shedlab.scheduleconstructor.api.web.dto.common

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

@Schema(description = "Classroom which exists in school")
data class Classroom(val id: UUID, val code: String, val type: ClassroomType)