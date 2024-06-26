package cz.utb.fai.openai

import cz.utb.fai.openai.dto.GetExplanationRequest
import cz.utb.fai.openai.dto.GetExplanationResponse
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate

private const val PATH = "/v1/chat/completions"
private const val TEMPERATURE = 0.2


open class GetExplanationImpl(
    template: RestTemplate,
    properties: OpenAIProperties
) : OpenAIRestService(template, properties), GetExplanationConnector {

    override fun getExplanation(prompt: String): GetExplanationResponse =
        callService(
            request = GetExplanationRequest(
                model = properties.model,
                prompt = prompt,
                temperature = TEMPERATURE
            ),
            path = PATH,
            httpMethod = HttpMethod.POST,
            queryVariable = mapOf("prompt" to prompt)
        )
}