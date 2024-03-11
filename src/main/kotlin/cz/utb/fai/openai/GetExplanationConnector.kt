package cz.utb.fai.openai

import org.springframework.stereotype.Component

@Component
interface GetExplanationConnector {
    fun getExplanation(prompt: String): GetExplanationResponse
}
