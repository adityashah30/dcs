package polynomialmodule;

public class PolynomialSolver {

    private Polynomial p;

    public PolynomialSolver(Polynomial p) {
        this.p = p;
    }

    public double solve() {
        double x0 = 10;
        for (int i = 0; i < 100; i++) {
            x0 = x0 - p.evaluate(x0) / p.getDerivative().evaluate(x0);
            System.out.println(x0);
        }
        return Math.round(x0 * 1000) / 1000;
    }
}
