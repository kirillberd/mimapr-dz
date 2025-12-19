package ru.bmstu.mimapr.numerics;

import ru.bmstu.mimapr.config.Data;
import ru.bmstu.mimapr.model.PhaseVariables;
import ru.bmstu.mimapr.model.TimeDemon;

public final class TimeStepController {
    private TimeStepController() {}

    public static PhaseVariables predictPhaseVariables(
            PhaseVariables pvPrev, PhaseVariables pvPrevPrev) {

        double[] prevPrevArr = pvPrevPrev.toArray();
        double[] curArr = pvPrev.toArray();

        double[] newVals = new double[Data.N];
        for (int i = 0; i < Data.N; ++i) {
            newVals[i] = 2.0 * curArr[i] - prevPrevArr[i];
        }

        return PhaseVariables.fromArray(newVals);
    }

    public static double findMaxValue(double[] values) {
        double maxv = 0.0;
        for (double v : values) if (v > maxv) maxv = v;
        return maxv;
    }

    public static double secondDerivative(
            double curr, double prev, double prevPrev, double deltaT, double prevDeltaT) {

        return 0.5
                * Math.abs(
                        ((curr - prev) - (prev - prevPrev) * deltaT / prevDeltaT)
                                * deltaT
                                / (deltaT + prevDeltaT));
    }

    public static double firstDerivativeOfDerivative(
            double currDer, double prevDer, double deltaT) {

        return Math.abs(currDer - prevDer) / deltaT;
    }

    public static TimeDemon calculateDeltaT(
            TimeDemon td,
            double prevDeltaT,
            PhaseVariables pv,
            PhaseVariables pvPrev,
            PhaseVariables pvPrevPrev) {

        double currT = td.currT;
        double deltaT = td.deltaT;

        PhaseVariables tmpPhi = new PhaseVariables();
        tmpPhi.phi1 = secondDerivative(pv.phi1, pvPrev.phi1, pvPrevPrev.phi1, deltaT, prevDeltaT);
        tmpPhi.phi2 = secondDerivative(pv.phi2, pvPrev.phi2, pvPrevPrev.phi2, deltaT, prevDeltaT);
        tmpPhi.phi3 = secondDerivative(pv.phi3, pvPrev.phi3, pvPrevPrev.phi3, deltaT, prevDeltaT);
        tmpPhi.phi4 = secondDerivative(pv.phi4, pvPrev.phi4, pvPrevPrev.phi4, deltaT, prevDeltaT);
        tmpPhi.phi5 = secondDerivative(pv.phi5, pvPrev.phi5, pvPrevPrev.phi5, deltaT, prevDeltaT);

        double delta = findMaxValue(tmpPhi.toArray());
        if (delta > Data.DELTA_1) {
            return new TimeDemon(currT, deltaT / 2.0, false);
        }

        if (delta > Data.DELTA_2) {
            return new TimeDemon(currT + deltaT, deltaT, true);
        }

        double newDeltaT = (deltaT > Data.MAXIMAL_STEP) ? Data.MAXIMAL_STEP : deltaT * 2.0;
        return new TimeDemon(currT + deltaT, newDeltaT, true);
    }

    public static TimeDemon deltaTReduction(TimeDemon td) {
        return new TimeDemon(td.currT, td.deltaT / 2.0, true);
    }
}
