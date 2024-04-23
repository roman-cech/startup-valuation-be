package cz.utb.fai.controller


import cz.utb.fai.openai.GetExplanationService
import cz.utb.fai.dto.controller.StartupValuationRequest
import cz.utb.fai.dto.controller.StartupValuationResponse
import cz.utb.fai.dto.StartupValuationReq
import cz.utb.fai.drools.DroolsRuleEngine
import org.springframework.stereotype.Service

@Service
class StartupValuationService(
    private val droolsRuleEngine: DroolsRuleEngine,
    private val getExplanationService: GetExplanationService
) {
    fun evaluateStartup(req: StartupValuationRequest): StartupValuationResponse =
        droolsRuleEngine.computeValuation(
            StartupValuationReq().apply { this.evidences = req.evidences })
            .let { res ->
                val openAIResponse = getExplanationService.getExplanation(res.explanations.joinToString())

                StartupValuationResponse(
                    rate = res.rate,
                    explanation = openAIResponse
                )
            }
}