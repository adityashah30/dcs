package polynomialmodule;

import java.io.*;
import java.util.Scanner;

public class PolynomialSolver {

    private Polynomial p;

    public PolynomialSolver(Polynomial p) {
        this.p = p;
    }

    public double solve(boolean flag) {
        if (flag) {
            File f = new File("data/polynomial.txt");
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            double val = 0;
            String s = p.toString();
            try {
                BufferedWriter fout = new BufferedWriter(new FileWriter(f));
                fout.write(s);
                fout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Process p = Runtime.getRuntime().exec("python solve_poly.py");
                p.waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Scanner fin = new Scanner(new BufferedReader(new FileReader("data/polynomial.txt")));
                val = fin.nextDouble();
                fin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return val;

        } else {
            double x0 = 10;
            for (int i = 0; i < 100; i++) {
                x0 = x0 - p.evaluate(x0) / p.getDerivative().evaluate(x0);
            }
            return Math.round(x0 * 1000) / 1000;
        }
    }
}
