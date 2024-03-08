package cz.utb.fai.connector

import org.springframework.stereotype.Service

@Service
class GetExplanationService(
    private val getExplanationConnector: GetExplanationConnector
) {
    fun getExplanation(prompt: String): String =
        getExplanationConnector.getExplanation("Generate explanation for: $prompt").choices.first().message.content
}