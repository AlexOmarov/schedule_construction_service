package ru.shedlab.scheduleconstructor.presentation.controller.swagger

import reactor.core.publisher.Mono
import ru.shedlab.scheduleconstructor.api.web.dto.response.GetClassroomsResponse
import ru.shedlab.scheduleconstructor.api.web.standard.StandardResponse

interface ISwaggerClassroomController {

    fun getClassrooms(): Mono<StandardResponse<GetClassroomsResponse>>
}