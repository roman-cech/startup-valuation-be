package cz.utb.fai.dto.type;

import cz.utb.fai.drools.TrackingAgendaEventListener;
import org.kie.api.runtime.rule.FactHandle;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Probability implements Comparable<Probability>, Uncertainty {

    private Double probability;
    private String description;

    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Probability(Double probability, String description) {
        this.probability = probability;
        this.description = description;
    }


    @Override
    public void updateProbability() {
        Map<String, Double> supportFactors = Collections.emptyMap();
        double newProbability;

        FactHandle fHandle = TrackingAgendaEventListener.getKieSession().getFactHandle(this);

        List<Double> lhsProbabilities = TrackingAgendaEventListener.getLHSProbabilities();

        if (TrackingAgendaEventListener.getRuleType() == RuleType.PROBABILISTIC) {
            try {
                supportFactors = TrackingAgendaEventListener.getMetaData().entrySet().stream()
                        .filter(entry -> entry.getValue() instanceof Double)
                        .collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), (Double) entry.getValue()), HashMap::putAll);
            } catch (Exception E) {
                System.exit(0);
            }

            // adjust LS and LN values
            double odd = prob2odd(getProbability());

            for (int i = 1; i <= lhsProbabilities.size(); i++) {
                if (lhsProbabilities.get(i - 1) < 1.0 && lhsProbabilities.get(i - 1) >= 0.5) {
                    // adjust LSi
                    supportFactors.put("LS" + i, getAdjustedLS(lhsProbabilities.get(i - 1), supportFactors.get("LS" + i)));

                    odd = odd * supportFactors.get("LS" + i);
                }
                if (lhsProbabilities.get(i - 1) >= 0.0 && lhsProbabilities.get(i - 1) < 0.5) {
                    // adjust LNi
                    supportFactors.put("LN" + i, getAdjustedLN(lhsProbabilities.get(i - 1), supportFactors.get("LN" + i)));

                    odd = odd * supportFactors.get("LN" + i);
                }
            }

            newProbability = odd2prob(odd);
        } else {
            newProbability = 1.0;
            for (int i = 1; i <= lhsProbabilities.size(); i++) {
                newProbability = newProbability * lhsProbabilities.get(i - 1);
            }
        }

        // update conclusion's probability value
        this.setProbability(newProbability);
        TrackingAgendaEventListener.getKieSession().update(fHandle, this);
    }

    private double getAdjustedLS(double prob, double ls) {
        return (2 * (ls - 1) * prob) + 2 - ls;
    }

    private double getAdjustedLN(double prob, double ln) {
        return (2 * (1 - ln) * prob) + ln;
    }

    //Probability 1.0 can not be substituted, so we are using 99.99 instead by the limits of 1.0 approximated by 0.99...
    private double prob2odd(double prob) {
        return prob == 1.0 ? 99.99 : prob / (1 - prob);
    }

    private double odd2prob(double odd) {
        return odd / (odd + 1);
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure {@link Integer#signum
     * signum}{@code (x.compareTo(y)) == -signum(y.compareTo(x))} for
     * all {@code x} and {@code y}.  (This implies that {@code
     * x.compareTo(y)} must throw an exception if and only if {@code
     * y.compareTo(x)} throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * {@code (x.compareTo(y) > 0 && y.compareTo(z) > 0)} implies
     * {@code x.compareTo(z) > 0}.
     *
     * <p>Finally, the implementor must ensure that {@code
     * x.compareTo(y)==0} implies that {@code signum(x.compareTo(z))
     * == signum(y.compareTo(z))}, for all {@code z}.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     * @apiNote It is strongly recommended, but <i>not</i> strictly required that
     * {@code (x.compareTo(y)==0) == (x.equals(y))}.  Generally speaking, any
     * class that implements the {@code Comparable} interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     */
    @Override
    public int compareTo(Probability o) {
        if (this.getProbability() < o.getProbability()) {
            return -1;
        } else if (this.getProbability() > o.getProbability()) {
            return 1;
        }
        return 0;
    }

}
