package distributormodule;

import java.util.ArrayList;
import iomodule.FileHandler;
import statsmodule.Stats;

public class Distributor {

    private ArrayList<Stats> clientStats;
    private long fileSize;
    private int order;
    private double power;
    private int numClients;

    public Distributor(long fsize, int order, double power) {
        this.fileSize = fsize;
        this.order = order;
        this.power = power;
        this.clientStats = (ArrayList<Stats>) (FileHandler.loadObject("clientstats"));
        this.numClients = clientStats.size();
        calculate();
    }

    public void calculate() {
        if (order == 1) {
            int i = 0;
            double product[] = new double[numClients];
            double sum = 0;
            for (i = 0; i < numClients; i++) {
                product[i] = Math.pow(clientStats.get(i).getStatsCalculator().getCpuFreq() / clientStats.get(i).getStatsCalculator().getCpuLoad()[0], 1 / power);
                sum += product[i];
            }
            for (i = 0; i < numClients; i++) {
                clientStats.get(i).setChunkSize(product[i] / sum);
            }
        }
        FileHandler.saveObject("clientstats", clientStats);
    }

}
