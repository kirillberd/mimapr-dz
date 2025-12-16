package ru.bmstu.mimapr.exceptions;

public final class ConversionException extends RuntimeException {
    public ConversionException() {
        super("Gauss results don't match the required size");
    }
}
