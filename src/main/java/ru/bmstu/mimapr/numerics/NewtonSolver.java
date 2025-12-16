package ru.bmstu.mimapr.numerics;

import ru.bmstu.mimapr.config.Data;
import ru.bmstu.mimapr.model.*;

public final class NewtonSolver {
    private NewtonSolver() {}

    // Решение методом Ньютона
    public static NewtonMethodResults newtonMethod(
            TimeDemon td, PhaseVariables initApprox, PrevStateVariables prevState) {

        int n = 0;
        // Текущая аппроксимация равна начальной при старте итерации
        PhaseVariables currApprox = initApprox;

        while (n < Data.NEWTON_STEPS) {
            // Вычисление матрицы Якоби на основе начальной аппроксимации
            double[][] jacobi = CircuitSystem.createJacobiMatrix(td.deltaT, initApprox.Ucb);

            // Построение вектора невязок
            double[] vectorForNewton = CircuitSystem.createVector(td, currApprox, prevState);

            // Домножение на -1 для того, чтобы соблюдалось уравнение для метода Ньютона
            for (int i = 0; i < vectorForNewton.length; i++) vectorForNewton[i] *= -1.0;

            // Получение вектора поправок методом Гаусса
            double[] gaussResults = LinearAlgebra.gauss(jacobi, vectorForNewton);
            PhaseVariables deltas = PhaseVariables.fromArray(gaussResults);

            // Получение текущего шага аппроксимации
            PhaseVariables newApprox = currApprox.sum(deltas);
            currApprox = newApprox;

            // Проверка второй нормы вектора поправок на порог точности
            if (CircuitSystem.calculateVectorNorm(deltas) < Data.EPSILON) break;

            n++;
        }
        // Если метод не сошелся, то итерация помечается как неуспешная
        if (n >= Data.NEWTON_STEPS) {
            return new NewtonMethodResults(false, new PhaseVariables());
        }
        return new NewtonMethodResults(true, currApprox);
    }
}
