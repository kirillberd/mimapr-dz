package ru.bmstu.mimapr.numerics;

import ru.bmstu.mimapr.exceptions.MatrixDimensionException;

import java.util.Arrays;

public final class LinearAlgebra {
    private LinearAlgebra() {}

    // Метод Гаусса
    public static double[] gauss(double[][] aIn, double[] bIn) {
        final int n = bIn.length;
        if (aIn.length != n) throw new MatrixDimensionException();
        for (double[] row : aIn) {
            if (row.length != n) throw new MatrixDimensionException();
        }

        double[][] a = new double[n][n];
        for (int i = 0; i < n; i++) {
            a[i] = Arrays.copyOf(aIn[i], n);
        }
        double[] b = Arrays.copyOf(bIn, n);

        double[] x = new double[n];

        for (int j = 0; j < n - 1; ++j) {
            final double ajj = a[j][j];
            for (int i = j + 1; i < n; ++i) {
                final double coeff = a[i][j] / ajj;
                for (int k = j; k < n; ++k) {
                    a[i][k] -= coeff * a[j][k];
                }
                b[i] -= coeff * b[j];
            }
        }

        for (int i = n - 1; i >= 0; --i) {
            double sum = 0.0;
            for (int ind = n - 1; ind >= i + 1; --ind) {
                sum += a[i][ind] * x[ind];
            }
            x[i] = (b[i] - sum) / a[i][i];
        }

        return x;
    }
}
