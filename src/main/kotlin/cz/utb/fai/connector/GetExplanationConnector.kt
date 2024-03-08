package cz.utb.fai.connector

import org.springframework.stereotype.Component

@Component
interface GetExplanationConnector {
    fun getExplanation(prompt: String): GetExplanationResponse
}
