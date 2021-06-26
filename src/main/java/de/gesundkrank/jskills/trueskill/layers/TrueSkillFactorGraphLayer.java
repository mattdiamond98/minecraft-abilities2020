package de.gesundkrank.jskills.trueskill.layers;

import de.gesundkrank.jskills.trueskill.TrueSkillFactorGraph;
import de.gesundkrank.jskills.factorgraphs.Factor;
import de.gesundkrank.jskills.factorgraphs.FactorGraphLayer;
import de.gesundkrank.jskills.factorgraphs.Variable;
import de.gesundkrank.jskills.numerics.GaussianDistribution;

public abstract class TrueSkillFactorGraphLayer<TInputVariable extends Variable<GaussianDistribution>, 
                                                TFactor extends Factor<GaussianDistribution>,
                                                TOutputVariable extends Variable<GaussianDistribution>>
    extends FactorGraphLayer
            <TrueSkillFactorGraph, GaussianDistribution, Variable<GaussianDistribution>, TInputVariable,
            TFactor, TOutputVariable> 
{
    public TrueSkillFactorGraphLayer(TrueSkillFactorGraph parentGraph)
    {
        super(parentGraph);
    }
}