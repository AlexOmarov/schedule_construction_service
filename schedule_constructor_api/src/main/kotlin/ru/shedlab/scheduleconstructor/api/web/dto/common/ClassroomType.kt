package ru.shedlab.scheduleconstructor.api.web.dto.common

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID
@Schema(description = "Type of the classroom")
data class ClassroomType(val id: UUID, val code: String)