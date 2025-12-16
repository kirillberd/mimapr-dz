package ru.bmstu.mimapr.model;

import java.util.Arrays;

public final class DoubleList {
    private double[] data = new double[256];
    private int size = 0;

    public void add(double v) {
        if (size == data.length) data = Arrays.copyOf(data, data.length * 2);
        data[size++] = v;
    }

    public int size() {
        return size;
    }

    public double get(int i) {
        return data[i];
    }
}
