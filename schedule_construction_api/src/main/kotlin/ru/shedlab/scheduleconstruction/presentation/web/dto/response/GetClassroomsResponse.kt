package ru.shedlab.scheduleconstruction.presentation.web.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import ru.shedlab.scheduleconstruction.presentation.web.dto.common.Classroom

@Schema(description = "Response of the get classroom API")
data class GetClassroomsResponse(val classrooms: List<Classroom>)
