package ru.shedlab.scheduleconstruction.api.web.standard

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Standard response object with response in 'response' field and metadata")
data class StandardResponse<T>(val response: T, val metadata: ResponseMetadata)