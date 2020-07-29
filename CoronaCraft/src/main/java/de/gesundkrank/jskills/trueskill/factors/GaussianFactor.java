package de.gesundkrank.jskills.trueskill.factors;

import de.gesundkrank.jskills.factorgraphs.Factor;
import de.gesundkrank.jskills.factorgraphs.Message;
import de.gesundkrank.jskills.factorgraphs.Variable;
import de.gesundkrank.jskills.numerics.GaussianDistribution;

public abstract class GaussianFactor extends Factor<GaussianDistribution> {

    GaussianFactor(String name) { super(name); }

    /** Sends the factor-graph message with and returns the log-normalization constant **/
    @Override
    protected double sendMessage(Message<GaussianDistribution> message, Variable<GaussianDistribution> variable) {
        GaussianDistribution marginal = variable.getValue();
        GaussianDistribution messageValue = message.getValue();
        double logZ = GaussianDistribution.logProductNormalization(marginal, messageValue);
        variable.setValue(marginal.mult(messageValue));
        return logZ;
    }

    @Override
    public Message<GaussianDistribution> createVariableToMessageBinding(Variable<GaussianDistribution> variable) {
        return createVariableToMessageBinding(variable, new Message<>(GaussianDistribution.fromPrecisionMean(0, 0),
                                              "message from %s to %s", this, variable));
    }
}