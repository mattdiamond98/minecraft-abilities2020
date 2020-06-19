package de.gesundkrank.jskills.factorgraphs;

import de.gesundkrank.jskills.Guard;

import java.util.*;

public abstract class Factor<TValue> {

    protected final List<Message<TValue>> messages = new ArrayList<>();

    private final Map<Message<TValue>, Variable<TValue>> messageToVariableBinding = new HashMap<>();

    private final String name;
    protected final List<Variable<TValue>> variables = new ArrayList<>();

    protected Factor(String name) {
        this.name = String.format("Factor[%s]", name);
    }

    /**
     * @return Log-normalization constant of that factor
     **/
    public abstract double getLogNormalization();

    /**
     * @return The number of messages that the factor has
     **/
    public int getNumberOfMessages() {
        return messages.size();
    }

    protected List<Variable<TValue>> getVariables() {
        return Collections.unmodifiableList(variables);
    }

    protected List<Message<TValue>> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    /**
     * Update the message and marginal of the i-th variable that the factor is connected to.
     *
     * @param messageIndex Index of the variable.
     * @return updatedMessage
     **/
    public double updateMessage(int messageIndex) {
        Guard.argumentIsValidIndex(messageIndex, messages.size(), "messageIndex");
        return updateMessage(messages.get(messageIndex),
                             messageToVariableBinding.get(messages.get(messageIndex)));
    }

    protected double updateMessage(Message<TValue> message, Variable<TValue> variable) {
        throw new UnsupportedOperationException();
    }

    /**
     * Resets the marginal of the variables a factor is connected to.
     **/
    public void resetMarginals() {
        for (Variable<TValue> variable : messageToVariableBinding.values()) {
            variable.resetToPrior();
        }
    }

    /**
     * Sends the ith message to the marginal and returns the log-normalization constant
     *
     * @param messageIndex Index of the variable.
     * @return the log-normalization constant
     **/
    public double sendMessage(int messageIndex) {
        Guard.argumentIsValidIndex(messageIndex, messages.size(), "messageIndex");

        Message<TValue> message = messages.get(messageIndex);
        Variable<TValue> variable = messageToVariableBinding.get(message);
        return sendMessage(message, variable);
    }

    protected abstract double sendMessage(Message<TValue> message, Variable<TValue> variable);

    public abstract Message<TValue> createVariableToMessageBinding(Variable<TValue> variable);

    protected Message<TValue> createVariableToMessageBinding(Variable<TValue> variable,
                                                             Message<TValue> message) {
        messages.add(message);
        messageToVariableBinding.put(message, variable);
        variables.add(variable);

        return message;
    }

    @Override
    public String toString() {
        return name;
    }
}