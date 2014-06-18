package polynomialmodule;

public class Term implements Comparable {

    double degree;
    double coefficient;

    public Term(double coeff, double deg) {
        degree = deg;
        coefficient = coeff;
    }

    public int compareTo(Object other) {
        Term otherTerm = (Term) other;
        if (otherTerm.degree < this.degree) {
            return -1;
        } else if (otherTerm.degree > this.degree) {
            return 1;
        } else {
            return 0;
        }
    }

    public String toString() {
        if (degree != 0) {
            return "(" + coefficient + ")*" + "x**" + degree;
        } else {
            return String.valueOf(coefficient);
        }
    }
}
