package ru.bmstu.mimapr.config;

/*
 * Конфигурация симуляции
 *
 * Время анализа TIME_END = 10e-3с
 * Требуемая точность EPSILON = 1e-3
 * Максимальное число итераций метода Ньютона NEWTON_STEPS - 7
 * Начальный шаг START_DELTA_TIME = 1e-9
 * Максимальный шаг MAXIMAL_STEP = 1e-10
 * Параметры корректировки DELTA_1 = 1e-5, DELTA_2 = 1e-7
 *
 * */
public final class Data {
    private Data() {}

    public static final double START_DELTA_TIME = 1e-9;
    public static final double TIME_START = START_DELTA_TIME;
    public static final double TIME_END = 1e-3;
    public static final double MINIMAL_STEP = 1e-10;
    public static final double MAXIMAL_STEP = 1e-5;

    public static final int N = 16;
    public static final int NEWTON_STEPS = 7;

    public static final double EPSILON = 1e-3;

    public static final double DELTA_1 = 1e-5;
    public static final double DELTA_2 = 1e-7;

    public static final int PRINT_EVERY_SUCCESS_STEPS = 100000;
}
