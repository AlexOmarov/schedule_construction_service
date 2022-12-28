package ru.shedlab.scheduleconstruction.integration.presentation.controller

import org.junit.jupiter.api.Test
import org.springframework.test.web.reactive.server.returnResult
import reactor.test.StepVerifier
import ru.shedlab.scheduleconstruction.integration.base.BaseIntegrationTest
import ru.shedlab.scheduleconstruction.api.web.dto.response.GetClassroomsResponse
import ru.shedlab.scheduleconstruction.api.web.standard.StandardResponse

class ClassroomControllerTests : BaseIntegrationTest() {
    @Test
    fun `When get classroom API is called, then return all classrooms in database`() {
        // when
        val response = webClient.get().uri("/classrooms").exchange()

        response.expectStatus().isOk

        // Check the result
        StepVerifier.create(response.returnResult<StandardResponse<GetClassroomsResponse>>().responseBody)
            .expectNextMatches { it.response.classrooms.isEmpty() }
            .verifyComplete()
    }
}