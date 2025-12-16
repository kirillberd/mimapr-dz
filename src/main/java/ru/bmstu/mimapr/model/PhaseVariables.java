package ru.bmstu.mimapr.model;

import ru.bmstu.mimapr.config.Data;
import ru.bmstu.mimapr.exceptions.ConversionException;

// Переменные состояния
public final class PhaseVariables {

    // Производные от напряжений конденсатора
    public double dUc1 = 0.0;
    public double dUc2 = 0.0;
    public double dUcb = 0.0;
    // Производные от токов индуктивности
    public double dIl1 = 0.0;
    public double dIl2 = 0.0;

    // Напряжения кондесатора
    public double Uc1 = 0.0;
    public double Uc2 = 0.0;
    public double Ucb = 0.0;
    // Токи индуктивности
    public double Il1 = 0.0;
    public double Il2 = 0.0;

    // Потенциалы на узлах
    public double phi1 = 0.0;
    public double phi2 = 0.0;
    public double phi3 = 0.0;
    public double phi4 = 0.0;
    public double phi5 = 0.0;
    // ЭДС
    public double iE = 0.0;

    public PhaseVariables sum(PhaseVariables o) {
        PhaseVariables r = new PhaseVariables();

        r.dUc1 = this.dUc1 + o.dUc1;
        r.dUc2 = this.dUc2 + o.dUc2;
        r.dUcb = this.dUcb + o.dUcb;
        r.dIl1 = this.dIl1 + o.dIl1;
        r.dIl2 = this.dIl2 + o.dIl2;

        r.Uc1 = this.Uc1 + o.Uc1;
        r.Uc2 = this.Uc2 + o.Uc2;
        r.Ucb = this.Ucb + o.Ucb;
        r.Il1 = this.Il1 + o.Il1;
        r.Il2 = this.Il2 + o.Il2;

        r.phi1 = this.phi1 + o.phi1;
        r.phi2 = this.phi2 + o.phi2;
        r.phi3 = this.phi3 + o.phi3;
        r.phi4 = this.phi4 + o.phi4;
        r.phi5 = this.phi5 + o.phi5;

        r.iE = this.iE + o.iE;

        return r;
    }

    public double[] toArray() {
        return new double[] {
            dUc1, dUc2, dUcb, dIl1, dIl2,
            Uc1, Uc2, Ucb, Il1, Il2,
            phi1, phi2, phi3, phi4, phi5,
            iE
        };
    }

    // Расположения в векторе неизвестных
    public static PhaseVariables fromArray(double[] array) {
        if (array == null || array.length != Data.N) throw new ConversionException();
        PhaseVariables pv = new PhaseVariables();

        pv.dUc1 = array[0];
        pv.dUc2 = array[1];
        pv.dUcb = array[2];
        pv.dIl1 = array[3];
        pv.dIl2 = array[4];

        pv.Uc1 = array[5];
        pv.Uc2 = array[6];
        pv.Ucb = array[7];
        pv.Il1 = array[8];
        pv.Il2 = array[9];

        pv.phi1 = array[10];
        pv.phi2 = array[11];
        pv.phi3 = array[12];
        pv.phi4 = array[13];
        pv.phi5 = array[14];

        pv.iE = array[15];

        return pv;
    }
}
