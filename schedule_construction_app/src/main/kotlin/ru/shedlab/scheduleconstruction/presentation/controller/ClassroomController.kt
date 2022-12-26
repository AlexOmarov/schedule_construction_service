package ru.shedlab.scheduleconstruction.presentation.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import ru.shedlab.scheduleconstruction.api.web.dto.response.GetClassroomsResponse
import ru.shedlab.scheduleconstruction.api.web.standard.ResponseMetadata
import ru.shedlab.scheduleconstruction.api.web.standard.ResultCode
import ru.shedlab.scheduleconstruction.api.web.standard.StandardResponse
import ru.shedlab.scheduleconstruction.presentation.controller.swagger.ISwaggerClassroomController

@RestController
@RequestMapping("classrooms")
class ClassroomController : ISwaggerClassroomController {
    @GetMapping
    override fun getClassrooms(): Mono<StandardResponse<GetClassroomsResponse>> {
        return Mono.just(StandardResponse(GetClassroomsResponse(listOf()), ResponseMetadata(ResultCode.OK, "")))
    }
}