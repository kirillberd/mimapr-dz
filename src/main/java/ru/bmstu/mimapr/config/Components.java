package ru.bmstu.mimapr.config;

/*
 * Значения компонентов системы
 *
 **/
public final class Components {
    private Components() {}

    // Индуктивность - для всех катушек одинаковая
    public static final double L = 0.001;
    // Емкости на конденсаторах
    public static final double C1 = 1e-6;
    public static final double C2 = 1e-10;
    // Сопротивления на резисторах
    public static final double R1 = 1000.0;
    public static final double R2 = 1000.0;

    // Значения для диода
    public static final double I_T = 1e-12;
    // Барьерная емкость
    public static final double C_B = 2e-12;
    public static final double MFT = 0.026;
    // Барьерное сопротивление
    public static final double R_B = 20.0;

    // Сопротивление утечки
    public static final double R_U = 1000000.0;

    // Период колебаний
    public static final double E_TIME = 1e-4;
    public static final double E_AMPLITUDE = 10.0; // Вольты

    // Закон по которому изменяется значение ЭДС
    public static double currentE1(double t) {
        return E_AMPLITUDE * Math.sin(2.0 * Math.PI / E_TIME * t);
    }
}
