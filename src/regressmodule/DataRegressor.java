package regressmodule;

import java.io.*;
import java.util.*;
import polynomial.PolynomialRegression;

public class DataRegressor {

    public static void main(String[] args) throws IOException {
        DataRegressor obj1 = new DataRegressor();
        obj1.getEquation();
    }

    public void getEquation() {
        try {
            Scanner in = new Scanner(new BufferedReader(new FileReader(new File("data/regressdata"))));
            ArrayList<Double> T = new ArrayList<>();
            ArrayList<Double> D = new ArrayList<>();
            ArrayList<Double> F = new ArrayList<>();
            ArrayList<Double> L = new ArrayList<>();
            while (in.hasNextDouble()) {
                T.add(in.nextDouble());
                D.add(in.nextDouble());
                F.add(in.nextDouble());
                L.add(in.nextDouble());
            }
            in.close();
            double[][] X = new double[D.size()][3];
            double[][] Y = new double[T.size()][1];
            for (int i = 0; i < X.length; i++) {
                X[i][0] = D.get(i);
                X[i][1] = F.get(i);
                X[i][2] = L.get(i);
                Y[i][0] = T.get(i);
            }
            PolynomialRegression pobj1 = new PolynomialRegression(X, Y, 1, 10);
            pobj1.fit();
            pobj1.getCoefficients().getTheta().saveToFileCSV("data/equation");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}