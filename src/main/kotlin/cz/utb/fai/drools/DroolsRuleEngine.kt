package cz.utb.fai.drools

import cz.utb.fai.dto.StartupValuationReq
import cz.utb.fai.dto.StartupValuationRes
import cz.utb.fai.dto.type.Conclusion
import org.kie.api.KieServices
import org.kie.api.runtime.KieContainer
import org.springframework.stereotype.Component

private const val DROOLS_RES = "response"
private const val DROOLS_YES = "YES"
private const val KIE_BASE = "startup-valuation"

@Component
class DroolsRuleEngine {
    private val kieContainer: KieContainer by lazy { KieServices.Factory.get().kieClasspathContainer }

    fun computeValuation(request: StartupValuationReq): StartupValuationRes =
        kieContainer.getKieBase(KIE_BASE).newKieSession().let { kieSession ->
            try {
                Conclusion().run {
                    kieSession.addEventListener(TrackingAgendaEventListener())
                    kieSession.setGlobal(DROOLS_RES, this)
                    kieSession.setGlobal(DROOLS_YES, "yes")
                    request.evidences.forEach { evidence -> kieSession.insert(evidence) }
                    kieSession.fireAllRules()
                    this
                }.let { conclusion -> StartupValuationRes(conclusion.rate, conclusion.explanations) }
            } finally {
                kieSession.dispose()
            }
        }
}