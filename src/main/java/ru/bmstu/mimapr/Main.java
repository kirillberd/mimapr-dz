package ru.bmstu.mimapr;

import ru.bmstu.mimapr.config.Data;
import ru.bmstu.mimapr.config.FileData;
import ru.bmstu.mimapr.exceptions.TimeStepException;
import ru.bmstu.mimapr.io.OutputUtils;
import ru.bmstu.mimapr.model.*;
import ru.bmstu.mimapr.numerics.NewtonSolver;
import ru.bmstu.mimapr.numerics.TimeStepController;

public class Main {

    public static void main(String[] args) {
        try {
            OutputUtils.clearFiles();

            TimeDemon timeDemon = new TimeDemon(Data.TIME_START, Data.START_DELTA_TIME, true);
            double prevDeltaT = timeDemon.deltaT;

            PrevStateVariables prevStateVariables = new PrevStateVariables();

            PhaseVariables initialApproximation = new PhaseVariables();
            PhaseVariables pvPrev = new PhaseVariables();
            PhaseVariables pvPrevPrev = new PhaseVariables();

            ResultLists results = new ResultLists();
            double nextSaveT = Data.TIME_START;
            final double SAVE_DT = 1e-6;
            int iteration = 0;
            while (timeDemon.currT < Data.TIME_END) {
                NewtonMethodResults nmr =
                        NewtonSolver.newtonMethod(
                                timeDemon, initialApproximation, prevStateVariables);
                iteration++;

                if (nmr.isSuccessful) {
                    PhaseVariables phaseVariables = nmr.phaseVariables;

                    TimeDemon prevTimeDemon = timeDemon;
                    timeDemon =
                            TimeStepController.calculateDeltaT(
                                    timeDemon, prevDeltaT, phaseVariables, pvPrev, pvPrevPrev);

                    if (!timeDemon.success) {
                        continue;
                    }
                    double tAccepted = prevTimeDemon.currT;
                    while (tAccepted >= nextSaveT) {
                        results.phi1List.add(phaseVariables.phi1);
                        results.phi2List.add(phaseVariables.phi2);
                        results.phi4List.add(phaseVariables.phi4);
                        results.phi5List.add(phaseVariables.phi5);
                        results.timeList.add(nextSaveT);
                        nextSaveT += SAVE_DT;
                    }
                    prevDeltaT = prevTimeDemon.deltaT;

                    initialApproximation =
                            TimeStepController.predictPhaseVariables(phaseVariables, pvPrev);

                    prevStateVariables.uC1Prev = phaseVariables.Uc1;
                    prevStateVariables.uC2Prev = phaseVariables.Uc2;
                    prevStateVariables.uCbPrev = phaseVariables.Ucb;
                    prevStateVariables.iL1Prev = phaseVariables.Il1;
                    prevStateVariables.iL2Prev = phaseVariables.Il2;

                    pvPrevPrev = pvPrev;
                    pvPrev = phaseVariables;

                    if (iteration % Data.PRINT_EVERY_SUCCESS_STEPS == 0) {
                        System.out.println(iteration + ", t = " + OutputUtils.fmt(timeDemon.currT));

                        results.phi1List.add(phaseVariables.phi1);
                        results.phi2List.add(phaseVariables.phi2);
                        results.phi4List.add(phaseVariables.phi4);
                        results.phi5List.add(phaseVariables.phi5);
                        results.timeList.add(prevTimeDemon.currT);
                    }
                } else {
                    timeDemon = TimeStepController.deltaTReduction(timeDemon);
                    if (timeDemon.deltaT < Data.MINIMAL_STEP) throw new TimeStepException();
                }
            }

            OutputUtils.printToFile(FileData.PHI1_FILE, results.phi1List);
            OutputUtils.printToFile(FileData.PHI2_FILE, results.phi2List);
            OutputUtils.printToFile(FileData.PHI4_FILE, results.phi4List);
            OutputUtils.printToFile(FileData.PHI5_FILE, results.phi5List);
            OutputUtils.printToFile(FileData.T_FILE, results.timeList);

            System.exit(0);
        } catch (Exception ex) {
            System.err.println("Fatal error: " + ex.getMessage());
            System.exit(2);
        }
    }
}
