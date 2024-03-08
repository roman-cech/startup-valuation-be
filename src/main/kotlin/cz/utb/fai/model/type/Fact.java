package cz.utb.fai.model.type;

public class Fact {
    private String description;
    public Fact() {}

    public Fact(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private double getAdjustedLS(double prob, double ls) { return (2 * (ls-1) * prob) + 2 - ls; }

    private double getAdjustedLN(double prob, double ln) { return (2 * (1 - ln) * prob) + ln; }

    //Probability 1.0 can not be substituted so we are using 99.99 instead by the limits of 1.0 approximated by 0.99...
    private double prob2odd(double prob) { return prob == 1.0 ? 99.99 : prob / (1 - prob); }

    private double odd2prob(double odd) { return odd / (odd + 1); }

}
