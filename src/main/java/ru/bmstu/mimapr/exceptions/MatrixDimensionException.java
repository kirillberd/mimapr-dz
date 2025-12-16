package ru.bmstu.mimapr.exceptions;

public final class MatrixDimensionException extends RuntimeException {
    public MatrixDimensionException() {
        super("Invalid size of matrix");
    }
}
