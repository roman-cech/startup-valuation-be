package cz.utb.fai.drools

import cz.utb.fai.dto.type.Probability
import cz.utb.fai.dto.type.RuleType
import org.drools.core.ClassObjectFilter
import org.kie.api.event.rule.*
import org.kie.api.runtime.KieSession
import org.springframework.stereotype.Component
import java.text.DecimalFormat

@Component
class TrackingAgendaEventListener : AgendaEventListener {
    lateinit var kieSession: KieSession
    lateinit var ruleName: String
    lateinit var metaData: Map<String, Any>
    var ruleType: RuleType? = null
    var activations: MutableList<Any> = mutableListOf()

    fun adjustFrameScore(score: Double, value: String): Double =
        DecimalFormat("#.##").format((value.toDouble() / 9) * score).toDouble()

    fun getLHSProbabilities(): List<Double> = activations.map { it as Probability }.mapNotNull { it.probability }

    fun parseLtvAndCac(ltv: Double, cac: Double): Double = when (ltv / cac) {
        in Double.NEGATIVE_INFINITY..0.0 -> 0.0
        in 5.00..Double.POSITIVE_INFINITY -> 5.0
        else -> ltv / cac
    }

    fun getProbabilityRef(c: Class<Probability>, description: String): Probability?  {
        val facts = kieSession.getObjects(ClassObjectFilter(c::class.java)).filterIsInstance<Probability>()
        val iterator = facts.iterator()
        while (iterator.hasNext()) {
            val fact = iterator.next()
            if (fact.description == description) {
                return fact
            }
        }
        return null
    }

    override fun beforeMatchFired(event: BeforeMatchFiredEvent) {
        kieSession = event.kieRuntime.kieBase.kieSessions.first()
        activations.addAll(event.match.objects)
        ruleName = event.match.rule.name
        metaData = event.match.rule.metaData
        ruleType = if (metaData.isEmpty()) RuleType.DETERMINISTIC else RuleType.PROBABILISTIC

        println("Before Match Fired")
    }

    override fun afterMatchFired(event: AfterMatchFiredEvent) = activations.clear()

    override fun matchCreated(event: MatchCreatedEvent) = println("Match Created: ${event.match}")

    override fun matchCancelled(event: MatchCancelledEvent) = println("Match Cancelled")

    override fun agendaGroupPopped(event: AgendaGroupPoppedEvent) = println("Agenda Group Popped")

    override fun agendaGroupPushed(event: AgendaGroupPushedEvent) = println("Agenda Group Pushed")

    override fun beforeRuleFlowGroupActivated(event: RuleFlowGroupActivatedEvent) = println("Before Rule Flow Group Activated")

    override fun afterRuleFlowGroupActivated(event: RuleFlowGroupActivatedEvent) = println("After Rule Flow Group Activated")

    override fun beforeRuleFlowGroupDeactivated(event: RuleFlowGroupDeactivatedEvent) = println("Before Rule Flow Group Deactivated")

    override fun afterRuleFlowGroupDeactivated(event: RuleFlowGroupDeactivatedEvent) = println("After Rule Flow Group Deactivated")
}