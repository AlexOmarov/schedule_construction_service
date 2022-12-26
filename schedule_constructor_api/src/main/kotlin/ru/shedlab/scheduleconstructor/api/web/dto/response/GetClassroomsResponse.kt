package ru.shedlab.scheduleconstructor.api.web.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import ru.shedlab.scheduleconstructor.api.web.dto.common.Classroom

@Schema(description = "Response of the get classroom API")
data class GetClassroomsResponse(val classrooms: List<Classroom>)
