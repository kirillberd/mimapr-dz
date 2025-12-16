package ru.bmstu.mimapr.model;

/*
 * Утилита для отслеживания текущего времени и проверки сходимости
 * */
public final class TimeDemon {
    public final double currT;
    public final double deltaT;
    public final boolean success;

    public TimeDemon(double currT, double deltaT, boolean success) {
        this.currT = currT;
        this.deltaT = deltaT;
        this.success = success;
    }
}
