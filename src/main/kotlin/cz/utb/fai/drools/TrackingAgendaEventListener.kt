package cz.utb.fai.drools

import cz.utb.fai.model.dto.StartupValuationRes
import cz.utb.fai.model.type.FrameScore.PRODUCT_SCORE
import cz.utb.fai.model.type.RuleType
import org.kie.api.event.rule.*
import org.kie.api.runtime.KieSession
import org.springframework.stereotype.Component
import java.text.DecimalFormat

@Component
class TrackingAgendaEventListener : AgendaEventListener {

    private lateinit var kieSession: KieSession
    private lateinit var ruleName: String
    private lateinit var metaData: Map<String, Any>

    private var ruleType: RuleType? = null
    private var activations: MutableList<Any> = mutableListOf()

    fun adjustProductScore(value: String): Double = DecimalFormat("#.##").format((value.toDouble() / 9) * PRODUCT_SCORE.score).toDouble()

    fun updateResponse(res: StartupValuationRes, rate: Double, descriptions: List<String>): StartupValuationRes {
        // Retrieve existing explanation
        val existingExplanations = res.explanation

        // Convert the string descriptions to a mutable list
        val newExplanations = descriptions.toMutableList()

        // If there is an existing explanation list, append the new descriptions to it
        if (!existingExplanations.isNullOrEmpty()) {
            newExplanations.addAll(existingExplanations)
        }

        return StartupValuationRes(
            res.rate + rate,
            newExplanations
        )
    }

    override fun beforeMatchFired(event: BeforeMatchFiredEvent) = with(event) {
        kieSession = kieRuntime.kieBase.kieSessions.first()
        activations.addAll(match.objects)
        ruleName = match.rule.name
        metaData = match.rule.metaData
        ruleType = if(metaData.isEmpty()) RuleType.DETERMINISTIC else RuleType.PROBABILISTIC

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