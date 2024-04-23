package cz.utb.fai.drools;

import java.text.DecimalFormat;
import java.util.*;

import cz.utb.fai.dto.type.Probability;
import cz.utb.fai.dto.type.RuleType;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;

public class TrackingAgendaEventListener implements AgendaEventListener {

    private static KieSession kieSession;
    private static List<Object> activations;
    private static Map<String, Object> metaData;
    private static RuleType rType;

    static public KieSession getKieSession() {
        return kieSession;
    }

    static public RuleType getRuleType() {
        return rType;
    }

    public static Map<String, Object> getMetaData() {
        return metaData;
    }

    public TrackingAgendaEventListener() {
        super();
        TrackingAgendaEventListener.kieSession = null;
        TrackingAgendaEventListener.activations = new ArrayList<>();
        TrackingAgendaEventListener.metaData = new HashMap<>();
    }

    static public List<Double> getLHSProbabilities() {
        ArrayList<Double> probabilityList = new ArrayList<>();

        for (Object activation : activations) {
            if (activation instanceof Probability) {
                Probability fact = (Probability) activation;
                probabilityList.add(fact.getProbability());
            }
        }

        return probabilityList;
    }

    public static double adjustFrameScore(double score, String value) {
        return Double.parseDouble(new DecimalFormat("#.##").format((Double.parseDouble(value) / 9) * score));
    }

    public static double parseLtvAndCac(double ltv, double cac) {
        double ratio = (ltv / cac) * 2;
        return (ratio <= 0.0) ? 0.0 : Math.min(ratio, 10.0);
    }

    public static double parseSamToMoney(double sam, double money) {
        double ratio = (sam / money);
        return (ratio >= 10) ? 8.0 : 2.0;
    }

    public static double parseTamToSam(double tam, double sam) {
        double ratio = tam/sam;
        return (ratio >= 10) ? 10.0 : 0.0;
    }

    static public Probability getProbabilityRef(Class<?> c, String description) {
        Collection<Probability> facts = (Collection<Probability>) kieSession.getObjects( new ClassObjectFilter(c) );
        for (Probability fact : facts) {
            if (fact.getDescription().compareTo(description) == 0) {
                return fact;
            }
        }
        return null;
    }

    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) { TrackingAgendaEventListener.activations.clear(); }

    @Override
    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) { }

    @Override
    public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) { }

    @Override
    public void agendaGroupPopped(AgendaGroupPoppedEvent event) { }

    @Override
    public void agendaGroupPushed(AgendaGroupPushedEvent event) { }

    @Override
    public void beforeMatchFired(BeforeMatchFiredEvent event) {
        TrackingAgendaEventListener.kieSession = (KieSession) event.getKieRuntime().getKieBase().getKieSessions()
                .toArray()[0];
        TrackingAgendaEventListener.activations.addAll(event.getMatch().getObjects());
        TrackingAgendaEventListener.metaData = event.getMatch().getRule().getMetaData();
        if (metaData.isEmpty()) {
            TrackingAgendaEventListener.rType = RuleType.DETERMINISTIC;
        } else {
            TrackingAgendaEventListener.rType = RuleType.PROBABILISTIC;
        }

        System.out.println("Before Match Fired");
    }

    @Override
    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) { }

    @Override
    public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) { }

    @Override
    public void matchCancelled(MatchCancelledEvent event) { }

    @Override
    public void matchCreated(MatchCreatedEvent event) {
        System.out.println("Match created: " + event.getMatch());
    }

}