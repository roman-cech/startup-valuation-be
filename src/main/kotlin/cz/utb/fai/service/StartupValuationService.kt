package cz.utb.fai.service


import cz.utb.fai.openai.GetExplanationService
import cz.utb.fai.model.StartupValuationRequest
import cz.utb.fai.model.StartupValuationResponse
import cz.utb.fai.model.dto.StartupValuationReq
import cz.utb.fai.drools.DroolsRuleEngine
import org.springframework.stereotype.Service

@Service
class StartupValuationService(
    private val droolsRuleEngine: DroolsRuleEngine,
    private val getExplanationService: GetExplanationService
) {
    fun evaluateStartup(req: StartupValuationRequest): StartupValuationResponse =
        droolsRuleEngine.computeValuation(StartupValuationReq().apply { this.evidences = req.evidences })
            .let { res ->
                val openAIResponse = getExplanationService.getExplanation(res.explanation.joinToString())
                //val openAIResponse = res.explanation.joinToString()

                StartupValuationResponse(
                    rate = res.rate,
                    explanation = openAIResponse
                )
            }
}