package cz.utb.fai.openai

import cz.utb.fai.openai.dto.GetExplanationResponse
import org.springframework.stereotype.Component

@Component
interface GetExplanationConnector {
    fun getExplanation(prompt: String): GetExplanationResponse
}
