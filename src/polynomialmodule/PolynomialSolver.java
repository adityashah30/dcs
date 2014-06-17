package polynomialmodule;

import java.util.ArrayList;
import java.util.Collections;

public class PolynomialSolver {

    private Polynomial p;

//    public static void main(String[] args) {
//        Polynomial p = new Polynomial();
//        p.add(new Term(1, 2));
//        p.add(new Term(2, 1));
//        p.add(new Term(1, 0));
//        System.out.println(p);
//        PolynomialSolver ps = new PolynomialSolver(p);
//        System.out.println(ps.solve());
//    }
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
