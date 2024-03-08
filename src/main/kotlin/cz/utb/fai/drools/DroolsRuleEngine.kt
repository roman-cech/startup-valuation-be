package cz.utb.fai.drools

import cz.utb.fai.model.dto.StartupValuationReq
import cz.utb.fai.model.dto.StartupValuationRes
import cz.utb.fai.model.type.Skills
import org.kie.api.runtime.KieContainer
import org.springframework.stereotype.Component

private const val DROOLS_RES = "response"
private const val DROOLS_LISTENER = "listener"


@Component
class DroolsRuleEngine(
    private val kieContainer: KieContainer
) {
    fun computeValuation(request: StartupValuationReq): StartupValuationRes =
        kieContainer.newKieSession().let { kieSession ->
            try {
                StartupValuationRes().run {
                    kieSession.addEventListener(TrackingAgendaEventListener())//TODO ? ProcessEventListener, RuleProcessListener
                    kieSession.setGlobal(DROOLS_RES, this)
                    kieSession.setGlobal(DROOLS_LISTENER, TrackingAgendaEventListener())
                    request.evidences.forEach { evidence -> kieSession.insert(evidence) }
                    kieSession.fireAllRules()
                    this
                }
            } finally {
                kieSession.dispose()
            }
        }
}