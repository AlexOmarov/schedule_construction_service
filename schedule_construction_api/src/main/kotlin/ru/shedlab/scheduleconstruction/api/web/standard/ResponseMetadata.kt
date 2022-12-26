package ru.shedlab.scheduleconstruction.api.web.standard

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Metadata of the standard response")
data class ResponseMetadata(val code: ResultCode, val systemMessage: String)
