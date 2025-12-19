package ru.bmstu.mimapr.numerics;

import ru.bmstu.mimapr.config.Components;
import ru.bmstu.mimapr.config.Data;
import ru.bmstu.mimapr.model.PhaseVariables;
import ru.bmstu.mimapr.model.PrevStateVariables;
import ru.bmstu.mimapr.model.TimeDemon;

// Система для расширенного узлового метода
public final class CircuitSystem {
    private CircuitSystem() {}

    // Построение матрицы Якоби
    public static double[][] createJacobiMatrix(double deltaT, double uCb) {
        double[][] jacobi = new double[Data.N][Data.N];

        // Значение для диода
        final double a =
                1.0 / Components.R_U
                        + Components.I_T / Components.MFT * Math.exp(uCb / Components.MFT);

        // Производные
        jacobi[0][0] = 1.0;
        jacobi[0][5] = -1.0 / deltaT;
        jacobi[1][1] = 1.0;
        jacobi[1][6] = -1.0 / deltaT;
        jacobi[2][2] = 1.0;
        jacobi[2][7] = -1.0 / deltaT;
        jacobi[3][3] = 1.0;
        jacobi[3][8] = -1.0 / deltaT;
        jacobi[4][4] = 1.0;
        jacobi[4][9] = -1.0 / deltaT;

        // Переменные состояния
        jacobi[5][5] = 1.0;
        jacobi[5][10] = 1.0;
        jacobi[5][11] = -1.0;

        jacobi[6][6] = 1.0;
        jacobi[6][13] = 1.0;
        jacobi[6][14] = -1.0;

        jacobi[7][7] = 1.0;
        jacobi[7][11] = 1.0;
        jacobi[7][12] = -1.0;

        jacobi[8][3] = Components.L;
        jacobi[8][10] = 1.0;
        jacobi[8][11] = -1.0;

        jacobi[9][9] = 1.0;
        jacobi[9][14] = -1.0;

        jacobi[10][0] = -Components.C1;
        jacobi[10][5] = -1.0 / Components.R1;
        jacobi[10][8] = -1.0;
        jacobi[10][15] = 1.0;

        jacobi[11][0] = Components.C1;
        jacobi[11][2] = -Components.C_B;
        jacobi[11][5] = 1.0 / Components.R1;
        jacobi[11][7] = -a;
        jacobi[11][8] = 1.0;

        jacobi[12][2] = Components.C_B;
        jacobi[12][7] = a;
        jacobi[12][12] = 1.0 / Components.R_B;
        jacobi[12][13] = -1.0 / Components.R_B;

        jacobi[13][1] = -Components.C2;
        jacobi[13][12] = -1.0 / Components.R_B;
        jacobi[13][13] = 1.0 / Components.R_B;

        jacobi[14][1] = Components.C2;
        jacobi[14][4] = Components.C2;
        jacobi[14][14] = 1.0 / Components.R2;

        // ЭДС
        jacobi[15][10] = 1.0;

        return jacobi;
    }

    // Построение вектора невязок
    public static double[] createVector(
            TimeDemon td, PhaseVariables pvApprox, PrevStateVariables prevState) {

        // Аппроксимированные производные
        final double dUc1 = pvApprox.dUc1;
        final double dUc2 = pvApprox.dUc2;
        final double dUcb = pvApprox.dUcb;
        final double dIl1 = pvApprox.dIl1;
        final double duC3 = pvApprox.duC3;

        // Переменные состояния
        final double uC1 = pvApprox.Uc1;
        final double uC2 = pvApprox.Uc2;
        final double uCb = pvApprox.Ucb;
        final double iL1 = pvApprox.Il1;
        final double uC3 = pvApprox.uC3;

        // Потенциалы
        final double phi1 = pvApprox.phi1;
        final double phi2 = pvApprox.phi2;
        final double phi3 = pvApprox.phi3;
        final double phi4 = pvApprox.phi4;
        final double phi5 = pvApprox.phi5;

        final double iE = pvApprox.iE;

        final double deltaT = td.deltaT;

        final double E_eq = phi1 - Components.currentE1(td.currT);

        // Токи в резисторах I_r = U_r / R
        final double iR1 = uC1 / Components.R1;
        final double iR2 = phi5 / Components.R2;
        final double iRu = uCb / Components.R_U;
        final double iRb = (phi4 - phi3) / Components.R_B;

        // Токи в конденсаторах I_c = C * dU_c / dt
        final double iC1 = Components.C1 * dUc1;
        final double iC2 = Components.C2 * dUc2;
        final double iCb = Components.C_B * dUcb;
        final double iC3 = Components.C3 * duC3;

        // Ток диода I_t * ( e^(U_cb / MFT) - 1)
        final double iD = Components.I_T * (Math.exp(uCb / Components.MFT) - 1.0);

        double[] v = new double[Data.N];

        // Построение вектора невязок

        // Производные
        v[0] = dUc1 - (uC1 - prevState.uC1Prev) / deltaT;
        v[1] = dUc2 - (uC2 - prevState.uC2Prev) / deltaT;
        v[2] = dUcb - (uCb - prevState.uCbPrev) / deltaT;
        v[3] = dIl1 - (iL1 - prevState.iL1Prev) / deltaT;
        v[4] = duC3 - (uC3 - prevState.uC3Prev) / deltaT;

        // ПС
        v[5] = uC1 - (phi2 - phi1);
        v[6] = uC2 - (phi5 - phi4);
        v[7] = uCb - (phi3 - phi2);
        v[8] = Components.L * dIl1 - (phi2 - phi1);
        v[9] = uC3 - phi5;

        // Потенциалы (1 закон Кирхгофа)
        v[10] = -iR1 - iC1 - iL1 + iE;
        v[11] = -iCb - iRu - iD + iR1 + iC1 + iL1;
        v[12] = -iRb + iCb + iRu + iD;
        v[13] = -iC2 + iRb;
        v[14] = iR2 + iC2 + iC3;
        // ЭДС
        v[15] = E_eq;

        return v;
    }

    // Вычисление второй нормы вектора поправок
    public static double calculateVectorNorm(PhaseVariables pv) {
        double sum = 0.0;

        // Производные не включаем в норму, так как они зависят от самих ПС
        sum += pv.Uc1 * pv.Uc1;
        sum += pv.Uc2 * pv.Uc2;
        sum += pv.Ucb * pv.Ucb;
        sum += pv.Il1 * pv.Il1;
        sum += pv.uC3 * pv.uC3;

        sum += pv.phi1 * pv.phi1;
        sum += pv.phi2 * pv.phi2;
        sum += pv.phi3 * pv.phi3;
        sum += pv.phi4 * pv.phi4;
        sum += pv.phi5 * pv.phi5;

        sum += pv.iE * pv.iE;

        return Math.sqrt(sum);
    }
}
