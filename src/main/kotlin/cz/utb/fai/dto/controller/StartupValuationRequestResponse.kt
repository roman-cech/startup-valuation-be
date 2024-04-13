package cz.utb.fai.dto.controller

import cz.utb.fai.dto.type.Evidence

data class StartupValuationRequest(val evidences: List<Evidence>)

data class StartupValuationResponse(
     val rate: Double,
     val explanation: String
)