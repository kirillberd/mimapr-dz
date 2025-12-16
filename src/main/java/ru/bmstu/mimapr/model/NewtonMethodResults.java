package ru.bmstu.mimapr.model;

public final class NewtonMethodResults {
    public final boolean isSuccessful;
    public final PhaseVariables phaseVariables;

    public NewtonMethodResults(boolean isSuccessful, PhaseVariables phaseVariables) {
        this.isSuccessful = isSuccessful;
        this.phaseVariables = phaseVariables;
    }
}
