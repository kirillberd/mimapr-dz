package ru.bmstu.mimapr.numerics;

import ru.bmstu.mimapr.config.Data;
import ru.bmstu.mimapr.model.PhaseVariables;
import ru.bmstu.mimapr.model.TimeDemon;

/**
 * TimeStepController — контроллер шага по времени и предиктор начального приближения.
 *
 * <p>Задачи класса: 1) Сформировать начальное приближение X_{n+1}^{(0)} для метода Ньютона (чтобы
 * Ньютон сходился быстрее на следующем шаге времени).
 *
 * <p>2) Реализовать простой адаптивный выбор шага по времени Δt: - если решение меняется "резко"
 * (большая кривизна) → шаг отклоняем и уменьшаем, - если решение умеренно гладкое → шаг принимаем
 * без изменений, - если решение очень гладкое → шаг принимаем и увеличиваем.
 *
 * <p>Критерий "резкости" основан на эвристической оценке локальной кривизны (приближённой второй
 * производной) для узловых потенциалов по трем последним точкам и двум последним шагам по времени.
 */
public final class TimeStepController {
    private TimeStepController() {}

    /**
     * Предиктор (прогноз) для начального приближения на следующем шаге времени.
     *
     * <p>Используется линейная экстраполяция (первый порядок):
     *
     * <p>X_{n+1}^{(0)} = X_n + (X_n - X_{n-1}) = 2*X_n - X_{n-1}
     *
     * <p>Где: - pvPrev = X_n (последнее найденное решение на принятом шаге) - pvPrevPrev = X_{n-1}
     * (решение на предыдущем принятом шаге)
     *
     * <p>Зачем: - дать Ньютону хорошую стартовую точку, - уменьшить число итераций Ньютона, -
     * повысить шанс сходимости на нелинейных режимах.
     */
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

    /**
     * Находит максимальное значение в массиве.
     *
     * <p>Используется, чтобы выбрать "наихудший" (максимальный) показатель резкости среди узлов Это
     * консервативно: шаг по времени подстраивается под самый быстро меняющийся узел.
     */
    public static double findMaxValue(double[] values) {
        double maxv = 0.0;
        for (double v : values) if (v > maxv) maxv = v;
        return maxv;
    }

    /**
     * Эвристическая оценка локальной "кривизны" (условно — второй производной) величины x(t).
     *
     * <p>На вход: - curr = x_n - prev = x_{n-1} - prevPrev = x_{n-2} - deltaT = Δt_n (текущий шаг
     * по времени) - prevDeltaT= Δt_{n-1} (предыдущий шаг)
     *
     * <p>Идея: - если изменение почти линейное (скорость изменения почти постоянная), то "кривизна"
     * мала, - при резких фронтах/нелинейностях "кривизна" растёт.
     *
     * <p>Эта величина далее сравнивается с порогами DELTA_1/DELTA_2 для управления шагом Δt.
     */
    public static double secondDerivative(
            double curr, double prev, double prevPrev, double deltaT, double prevDeltaT) {

        // Сравниваем приращения на двух соседних шагах с учётом возможной разницы t
        // и получаем меру отклонения от линейного поведения.
        return 0.5
                * Math.abs(
                        ((curr - prev) - (prev - prevPrev) * deltaT / prevDeltaT)
                                * deltaT
                                / (deltaT + prevDeltaT));
    }

    /**
     * Адаптивный контроль шага по времени.
     *
     * <p>Вход: - td : текущее время t_n и текущий шаг Δt_n - prevDeltaT : предыдущий шаг t_{n-1} -
     * pv : решение X_n (на текущем шаге, уже найденное Ньютоном) - pvPrev : решение X_{n-1} -
     * pvPrevPrev : решение X_{n-2}
     *
     * <p>Алгоритм: 1) Считаем оценку "кривизны" для φ1..φ5 (узловых потенциалов). 2) Берём максимум
     * (самый резкий узел). 3) Сравниваем с порогами:
     *
     * <p>delta > DELTA_1: шаг слишком грубый → шаг отклоняем, Δt уменьшаем в 2 раза, время не
     * двигаем.
     *
     * <p>DELTA_2 < delta <= DELTA_1: шаг приемлем → шаг принимаем, Δt оставляем.
     *
     * <p>delta <= DELTA_2: шаг очень "гладкий" → шаг принимаем, Δt увеличиваем в 2 раза, но не
     * больше MAXIMAL_STEP.
     *
     * <p>Возвращаем: - TimeDemon с (t, Δt, success): success=false означает "шаг отклонён, нужно
     * пересчитать этот же момент времени с меньшим Δt".
     */
    public static TimeDemon calculateDeltaT(
            TimeDemon td,
            double prevDeltaT,
            PhaseVariables pv,
            PhaseVariables pvPrev,
            PhaseVariables pvPrevPrev) {

        double currT = td.currT;
        double deltaT = td.deltaT;

        // tmp хранит оценки "кривизны" для φ1..φ5
        PhaseVariables tmp = new PhaseVariables();
        tmp.phi1 = secondDerivative(pv.phi1, pvPrev.phi1, pvPrevPrev.phi1, deltaT, prevDeltaT);
        tmp.phi2 = secondDerivative(pv.phi2, pvPrev.phi2, pvPrevPrev.phi2, deltaT, prevDeltaT);
        tmp.phi3 = secondDerivative(pv.phi3, pvPrev.phi3, pvPrevPrev.phi3, deltaT, prevDeltaT);
        tmp.phi4 = secondDerivative(pv.phi4, pvPrev.phi4, pvPrevPrev.phi4, deltaT, prevDeltaT);
        tmp.phi5 = secondDerivative(pv.phi5, pvPrev.phi5, pvPrevPrev.phi5, deltaT, prevDeltaT);

        // Берём максимальную оценку (наихудший узел)
        double delta = findMaxValue(tmp.toArray());

        // 1) Слишком большой "изгиб" → уменьшаем шаг и отклоняем текущий шаг времени
        if (delta > Data.DELTA_1) {
            return new TimeDemon(currT, deltaT / 2.0, false);
        }

        // 2) Средняя кривизна → принимаем шаг, шаг оставляем прежним
        if (delta > Data.DELTA_2) {
            return new TimeDemon(currT + deltaT, deltaT, true);
        }

        // 3) Очень гладко → принимаем шаг и пытаемся увеличить Δt
        double newDeltaT = (deltaT > Data.MAXIMAL_STEP) ? Data.MAXIMAL_STEP : deltaT * 2.0;
        return new TimeDemon(currT + deltaT, newDeltaT, true);
    }

    /**
     * Принудительное уменьшение шага по времени в 2 раза (без отклонения по кривизне).
     *
     * <p>Обычно вызывается, когда метод Ньютона НЕ сошёлся на текущем Δt. Тогда мы остаёмся на том
     * же времени t, но пробуем пересчитать шаг с меньшим Δt.
     */
    public static TimeDemon deltaTReduction(TimeDemon td) {
        return new TimeDemon(td.currT, td.deltaT / 2.0, true);
    }
}
