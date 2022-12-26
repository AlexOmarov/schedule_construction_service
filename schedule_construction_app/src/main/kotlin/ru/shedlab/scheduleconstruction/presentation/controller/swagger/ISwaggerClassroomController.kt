package ru.shedlab.scheduleconstruction.presentation.controller.swagger

import reactor.core.publisher.Mono
import ru.shedlab.scheduleconstruction.api.web.dto.response.GetClassroomsResponse
import ru.shedlab.scheduleconstruction.api.web.standard.StandardResponse

interface ISwaggerClassroomController {

    fun getClassrooms(): Mono<StandardResponse<GetClassroomsResponse>>
}