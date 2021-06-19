package app.domain.shared;

import app.domain.shared.exceptions.InvalidLengthException;
import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.distribution.TDistribution;

public class MultiLinearRegression implements Regression {
    private double[][] C;
    private double F0;
    private double intercept;
    private double slope1;
    private double slope2;
    private double r2;
    private double r2Ajusted;
    private double[] betta;
    private double SQt;
    private double SQr;
    private double SQe;
    private double MQr;
    private double MQe;
    private double[][] x;
    private double[] y;
    private int n;
    private int k;

    public MultiLinearRegression(double[][] x, double[] y) {

        if (x.length != y.length) {
            throw new IllegalArgumentException("array lengths are not equal");
        }

        double[][] m1 = new double[x.length][x[0].length + 1];
        for (int i = 0; i < m1.length; i++) {
            m1[i][0] = 1;
            for (int j = 1; j < m1[i].length; j++) {
                m1[i][j] = x[i][j - 1];
            }
        }

        this.k = 2;
        this.n = y.length;
        this.x = m1;
        this.y = y;

        double[][] xt = transpose(this.x);

        double[][] xtx = matrixProduct(xt, this.x);

        double[][] xtxinv = inverse(xtx);

        double[][] xtxinvxt = matrixProduct(xtxinv, transpose(this.x));

        this.betta = new double[xtxinvxt.length];

        for (int j = 0; j < xtxinvxt.length; j++) {
            for (int m = 0; m < xtxinvxt[0].length; m++) {

                betta[j] += xtxinvxt[j][m] * y[m];

            }
        }

        this.C = inverse(matrixProduct(transpose(this.x), this.x));

        this.intercept = this.betta[0];
        this.slope1 = this.betta[1];
        this.slope2 = this.betta[2];


        this.SQt = calculateSQT(y);
        this.SQr = calculateSQR(y, betta, this.x);
        this.SQe = calculateSQE(this.SQt, this.SQr);

        this.r2 = SQr / SQt;


        this.r2Ajusted = calculateR2Adjusted(this.r2, n, k);

        this.MQr = SQr / this.k;


        this.MQe = this.SQe / (this.n - (this.k + 1));

        this.F0 = this.MQr / this.MQe;

    }

    private static double[] matrixVectorProduct(double[][] matrix, double[] vector) {


        double[] product = new double[matrix.length];
        int i = 0;

        for (int j = 0; j < matrix.length; j++) {
            for (int k = 0; k < matrix.length; k++) {
                product[i] += matrix[j][k] * vector[k];
            }

            i++;

        }
        return product;
    }

    private static double[][] matrixProduct(double[][] matrixA, double[][] matrixB) {

        double[][] product = new double[matrixA.length][matrixB[0].length];

        for (int i = 0; i < matrixA.length; i++) {
            for (int j = 0; j < matrixB[0].length; j++) {
                for (int k = 0; k < matrixA[0].length; k++) {
                    product[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }
        return product;
    }

    private static double[][] transpose(double[][] matrix) {
        double[][] transpose = new double[matrix[0].length][matrix.length];

//Code to transpose a matrix
        for (int i = 0; i < matrix[0].length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                transpose[i][j] = matrix[j][i];
            }
        }
        return transpose;
    }

    //dar aqui os creditos
    //uses the laplace theorem to calculate the determinant
    private static double determinant(double[][] matrix) {
        if (matrix.length != matrix[0].length)
            throw new IllegalStateException("matrix should be a square matrizx");

        if (matrix.length == 2)
            return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];

        double det = 0;
        for (int i = 0; i < matrix[0].length; i++)
            det += Math.pow(-1, i) * matrix[0][i]
                    * determinant(minor(matrix, 0, i));
        return det;
    }

    //calculates the inverse matrix using the complement matrix
    private static double[][] inverse(double[][] matrix) {
        double[][] inverse = new double[matrix.length][matrix.length];

        // minors and cofactors
        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; j < matrix[i].length; j++)
                inverse[i][j] = Math.pow(-1, i + j)
                        * determinant(minor(matrix, i, j));

        // adjugate and determinant
        double det = 1.0 / determinant(matrix);
        for (int i = 0; i < inverse.length; i++) {
            for (int j = 0; j <= i; j++) {
                double temp = inverse[i][j];
                inverse[i][j] = inverse[j][i] * det;
                inverse[j][i] = temp * det;
            }
        }

        return inverse;
    }

    private static double[][] minor(double[][] matrix, int row, int column) {
        double[][] minor = new double[matrix.length - 1][matrix.length - 1];

        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; i != row && j < matrix[i].length; j++)
                if (j != column)
                    minor[i < row ? i : i - 1][j < column ? j : j - 1] = matrix[i][j];
        return minor;
    }

    public double getIntercept() {
        return intercept;
    }

    public double getSlope1() {
        return slope1;
    }

    public double getSlope2() {
        return slope2;
    }

    public double getF0() {
        return F0;
    }

    public double getCriticValueStudent(double alpha) {
        TDistribution td = new TDistribution(this.n - this.k - 1);

        return td.inverseCumulativeProbability(1 - alpha);
    }

    public double getCriticValueFisher(double alphaf) {
        FDistribution fDistribution = new FDistribution(this.k, this.n - (this.k + 1));
        return fDistribution.inverseCumulativeProbability(1 - alphaf);
    }

    public double lowerLimitCoeficient(int index, double alpha) {

        double critTD = getCriticValueStudent(alpha);

        double variance = this.MQe;

        return this.betta[index] - critTD * Math.sqrt(variance * this.C[index][index]);

    }

    public double upperLimitCoeficient(int index, double alpha) {

        double critTD = getCriticValueStudent(alpha);

        double variance = this.MQe;

        return this.betta[index] + critTD * Math.sqrt(variance * this.C[index][index]);

    }

    private double calculateSQR(double[] y, double[] betta, double[][] x) {
        double[][] bettat = new double[betta.length][1];
        for (int i = 0; i < betta.length; i++) {
            bettat[i][0] = betta[i];
        }
        x = transpose(x);


        double[] btxt = new double[x[0].length];
        for (int i = 0; i < x[0].length; i++) {
            for (int j = 0; j < bettat.length; j++) {
                btxt[i] += x[j][i] * bettat[j][0];
            }
        }

        double btxty = 0;
        for (int i = 0; i < y.length; i++) {
            btxty += y[i] * btxt[i];
        }

        return btxty - y.length * Math.pow(calculateym(y), 2);

    }

    private double calculateym(double[] y) {
        double ym = 0;

        for (int i = 0; i < y.length; i++) {
            ym += y[i];
        }
        return ym / y.length;

    }

    private double calculateSQT(double[] y) {
        int n = y.length;
        double yty = 0;
        for (int i = 0; i < y.length; i++) {
            yty += y[i] * y[i];
        }

        return yty - n * Math.pow(calculateym(y), 2);
    }

    private double calculateSQE(double SQT, double SQR) {
        return SQT - SQR;
    }

    private double calculateR2Adjusted(double r2, double n, double k) {

        return (1 - (((n - 1) / (n - (k + 1))) * (1 - r2)));
    }

    public double getEstimate(double[] x) throws InvalidLengthException {
        if (x.length != this.betta.length - 1) {
            throw new InvalidLengthException();
        }

        double yEstimated = 0;

        yEstimated += yEstimated + this.betta[0];
        for (int i = 0; i < x.length; i++) {
            yEstimated += this.betta[i + 1] * x[i];
        }

        return yEstimated;
    }

    public double lowerLimitAnswer(double[] x0, double alpha) throws InvalidLengthException {
        if (x0.length != this.betta.length - 1) {
            throw new InvalidLengthException();
        }

        double[] x1 = new double[x0.length + 1];

        x1[0] = 1;

        for (int i = 1; i < x1.length; i++) {
            x1[i] = x0[i - 1];
        }

        double[][] x1t = new double[x1.length][1];

        for (int i = 0; i < x1.length; i++) {
            x1t[i][0] = x1[i];
        }

        double[] cx0 = matrixVectorProduct(this.C, x1);

        double xtcx = 0;

        for (int i = 0; i < cx0.length; i++) {
            xtcx += cx0[i] * x1t[i][0];
        }


        double critTD = getCriticValueStudent(alpha);
        double variance = this.MQe;

        return getEstimate(x0) - critTD * Math.sqrt(variance * (1 + xtcx));

    }

    public double upperLimitAnswer(double[] x0, double alpha) throws InvalidLengthException {
        if (x0.length != this.betta.length - 1) {
            throw new InvalidLengthException();
        }

        double[] x1 = new double[x0.length + 1];

        x1[0] = 1;

        for (int i = 1; i < x1.length; i++) {
            x1[i] = x0[i - 1];
        }

        double[][] x1t = new double[x1.length][1];

        for (int i = 0; i < x1.length; i++) {
            x1t[i][0] = x1[i];
        }

        double[] cx0 = matrixVectorProduct(this.C, x1);

        double xtcx = 0;

        for (int i = 0; i < cx0.length; i++) {
            xtcx += cx0[i] * x1t[i][0];
        }

        double critTD = getCriticValueStudent(alpha);
        double variance = this.MQe;

        return getEstimate(x0) + critTD * Math.sqrt(variance * (1 + xtcx));

    }

    public double getTestStatistics(int index) {
        return this.betta[index] / Math.sqrt(this.MQe * this.C[index][index]);
    }

    public double getR2() {
        return r2;
    }

    @Override
    public double getR() {
        return Math.sqrt(this.r2);
    }

    public double getR2Ajusted() {
        return r2Ajusted;
    }

    public double getSQt() {
        return SQt;
    }

    public double getSQr() {
        return SQr;
    }

    public double getSQe() {
        return SQe;
    }

    public double getMQr() {
        return MQr;
    }

    public double getMQe() {
        return MQe;
    }

    public int getN() {
        return n;
    }

    public int getK() {
        return k;
    }

    @Override
    public String toString() {
        return slope1 + "x1 + " + slope2 + "x2 + " + intercept;

    }
}
