package ru.bmstu.mimapr.exceptions;

public final class TimeStepException extends RuntimeException {
    public TimeStepException() {
        super("Step is now less than the minimum possible");
    }
}
