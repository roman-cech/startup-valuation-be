package cz.utb.fai.drools

import cz.utb.fai.dto.StartupValuationReq
import cz.utb.fai.dto.StartupValuationRes
import org.kie.api.KieServices
import org.kie.api.runtime.KieContainer
import org.kie.api.runtime.KieSession
import org.springframework.stereotype.Component

private const val DROOLS_RES = "response"
private const val KIE_BASE = "startup-valuation"

@Component
class DroolsRuleEngine {
    private val kieContainer: KieContainer by lazy { KieServices.Factory.get().kieClasspathContainer }

    fun computeValuation(request: StartupValuationReq): StartupValuationRes =
        kieContainer.getKieBase(KIE_BASE).newKieSession().let {kieSession ->
            try {
                StartupValuationRes().run {
                    kieSession.addEventListener(TrackingAgendaEventListener())
                    kieSession.setGlobal(DROOLS_RES, this)
                    request.evidences.forEach { evidence -> kieSession.insert(evidence) }
                    kieSession.fireAllRules()
                    this
                }
            } finally {
                kieSession.dispose()
            }
        }

}