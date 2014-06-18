package distributormodule;

import org.ejml.simple.SimpleMatrix;
import java.util.ArrayList;
import iomodule.FileHandler;
import java.io.IOException;
import statsmodule.Stats;
import polynomialmodule.*;

public class Distributor {

    private ArrayList<Stats> clientStats;
    private long fileSize;
    private int order;
    private double power;
    private int numClients;
    private double[] oldDsize;
    private double[] newDsize;
    private double alpha = 0.25;

    public Distributor(long fsize, int order, double power) {
        this.fileSize = fsize;
        this.order = order;
        this.power = power;
        this.clientStats = (ArrayList<Stats>) (FileHandler.loadObject("clientstats.bin"));
        this.numClients = clientStats.size();
        this.oldDsize = new double[numClients];
        this.newDsize = new double[numClients];
        calculate();
    }

    public void calculate() {
        Stats.setFileSize(fileSize);
        Stats.setPower(power);
        oldCalculate();
        if (newCalculate()) {
            for (int i = 0; i < numClients; i++) {
                double size = (alpha) * oldDsize[i] + (1 - alpha) * newDsize[i];
                clientStats.get(i).setChunkSize(size);
            }
        } else {
            for (int i = 0; i < numClients; i++) {
                double size = oldDsize[i];
                clientStats.get(i).setChunkSize(size);
            }
        }
        FileHandler.saveObject("clientstats.bin", clientStats);
    }

    public void oldCalculate() {
        if (order == 1) {
            int i = 0;
            double product[] = new double[numClients];
            double sum = 0;
            for (i = 0; i < numClients; i++) {
                product[i] = Math.pow(clientStats.get(i).getStatsCalculator().getCpuFreq() / clientStats.get(i).getStatsCalculator().getCpuLoad()[0], 1 / power);
                sum += product[i];
            }
            for (i = 0; i < numClients; i++) {
                oldDsize[i] = product[i] / sum;
            }
        }

    }

    public boolean newCalculate() {
        if (order == 1) {
            SimpleMatrix theta = null;
            try {
                theta = SimpleMatrix.loadCSV("data/equation.txt");
            } catch (IOException e) {
                return false;
            }
            int thetaRows = theta.numRows();
            int thetaPower = (thetaRows - 1) / 3;
            double thetaconst = 0;
            double[] thetadi = new double[thetaPower];
            double[] thetafi = new double[thetaPower];
            double[] thetali = new double[thetaPower];
            if (thetaPower == 1) {
                int row = 0;
                thetaconst = theta.get(row++, 0);
                for (int i = 0; i < thetaPower; i++) {
                    thetadi[i] = theta.get(row++, 0);
                }
                for (int i = 0; i < thetaPower; i++) {
                    thetafi[i] = theta.get(row++, 0);
                }
                for (int i = 0; i < thetaPower; i++) {
                    thetali[i] = theta.get(row++, 0);
                }
                double p = 1 / power;
                double thetasigma = 0;
                for (int i = 0; i < numClients; i++) {
                    thetasigma += (thetaconst + thetafi[0] * clientStats.get(i).getStatsCalculator().getCpuFreq() + thetali[0] * clientStats.get(i).getStatsCalculator().getCpuLoad()[0]);
                }
                Polynomial poly = new Polynomial();
                poly.add(new Term(numClients, p));
                poly.add(new Term(-p * thetasigma, p - 1));
                poly.add(new Term(-thetadi[0] * fileSize, 0));
                double k = new PolynomialSolver(poly).solve(true);
                double dsum = 0;
                for (int i = 0; i < numClients - 1; i++) {
                    newDsize[i] = k - (thetaconst + thetafi[0] * clientStats.get(i).getStatsCalculator().getCpuFreq() + thetali[0] * clientStats.get(i).getStatsCalculator().getCpuLoad()[0]);
                    newDsize[i] /= thetadi[0];
                    newDsize[i] = Math.pow(newDsize[i], p);
                    newDsize[i] /= fileSize;
                    dsum += newDsize[i];
                }
                newDsize[numClients - 1] = 1 - dsum;
            } else if (thetaPower == 2) {
                int row = 0;
                thetaconst = theta.get(row++, 0);
                for (int i = 0; i < thetaPower; i++) {
                    thetadi[i] = theta.get(row++, 0);
                }
                for (int i = 0; i < thetaPower; i++) {
                    thetafi[i] = theta.get(row++, 0);
                }
                for (int i = 0; i < thetaPower; i++) {
                    thetali[i] = theta.get(row++, 0);
                }

            } else if (thetaPower == 3) {

            } else if (thetaPower == 4) {

            }
        }
        return true;
    }

}
