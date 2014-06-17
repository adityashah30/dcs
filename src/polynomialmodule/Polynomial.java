package polynomialmodule;

import java.util.ArrayList;
import java.util.Collections;

public class Polynomial {

    ArrayList<Term> polynomial;

    public Polynomial() {
        polynomial = new ArrayList<>();
    }

    public Polynomial(ArrayList<Term> poly) {
        polynomial = poly;
        Collections.sort(polynomial);
    }

    public Polynomial(Term[] poly) {
        for (Term t : poly) {
            add(t);
        }
    }

    public void add(Term t) {
        for (int i = 0; i < polynomial.size(); i++) {
            if (polynomial.get(i).degree == t.degree) {
                polynomial.get(i).coefficient += t.coefficient;
                return;
            }
        }
        polynomial.add(t);
        Collections.sort(polynomial);
    }

    public void add(Polynomial p) {
        for (Term t : p.polynomial) {
            add(t);
        }
    }

    public Polynomial getDerivative() {
        Polynomial p = new Polynomial();
        for (Term t : polynomial) {
            if (t.degree != 0) {
                p.add(new Term(t.coefficient * t.degree, t.degree - 1));
            }
        }
        return p;
    }

    public double evaluate(double point) {
        double val = 0;
        for (Term t : polynomial) {
            val += t.coefficient * Math.pow(point, t.degree);
        }
        return val;
    }

    @Override
    public String toString() {
        StringBuilder st = new StringBuilder();
        for (int i = 0; i < polynomial.size() - 1; i++) {
            st.append(polynomial.get(i).toString() + " + ");
        }
        st.append(polynomial.get(polynomial.size() - 1).toString());
        return st.toString();
    }
}
