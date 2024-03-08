package cz.utb.fai.model

import cz.utb.fai.model.type.Evidence

data class StartupValuationRequest(val evidences: List<Evidence>)

data class StartupValuationResponse(
     val rate: Double,
     val explanation: String
)