package cz.utb.fai.drools

import cz.utb.fai.dto.StartupValuationReq
import cz.utb.fai.dto.StartupValuationRes
import org.kie.api.KieServices
import org.kie.api.runtime.KieSession
import org.springframework.stereotype.Component

private const val DROOLS_RES = "response"
private const val KIE_BASE = "startup-valuation"

@Component
class DroolsRuleEngine {
    private var kieSession: KieSession

    init {
        val kContainer = KieServices.Factory.get().kieClasspathContainer
        kieSession = kContainer.getKieBase(KIE_BASE).newKieSession()
        kieSession.addEventListener(AgendaListener())

        kieSession.setGlobal(DROOLS_RES, StartupValuationRes())
    }

    fun computeValuation(request: StartupValuationReq): StartupValuationRes {
        val result = StartupValuationRes()

        try {
            request.evidences.forEach { evidence ->
                kieSession.insert(evidence)
            }

            kieSession.fireAllRules()
        } finally {
            kieSession.dispose()
        }

        return result
    }
}