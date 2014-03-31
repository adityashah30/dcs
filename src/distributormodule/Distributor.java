package distributormodule;

import java.util.Hashtable;
import iomodule.FileHandler;
import statsmodule.StatsCalculator;

public class Distributor {

    private Hashtable<String, StatsCalculator> statTable;
    private long fileSize;
    private int order;
    private double power;
    private int numClients;
    private double[] chunkSizes;

    public Distributor(long fsize, int order, double power) {
        this.fileSize = fsize;
        this.order = order;
        this.power = power;
        this.statTable = (Hashtable<String, StatsCalculator>) (FileHandler.getInstance().loadObject("clientstats"));
        this.numClients = statTable.size();
        this.chunkSizes = new double[numClients];
        calculate();
    }

    public void calculate() {
        if (order == 1) {
            StatsCalculator[] stats = new StatsCalculator[numClients];
            int i = 0;
            for (String ip : statTable.keySet()) {
                stats[i++] = statTable.get(ip);
            }
            double product[] = new double[numClients];
            double sum = 0;
            for (i = 0; i < numClients; i++) {
                product[i] = Math.pow(stats[i].getCpuFreq() * 100 * (1 - stats[i].getCpuLoad()[0]), 1 / power);
                sum += product[i];
//                System.out.println("Product" + i + ": " + product[i]);
            }
            for (i = 0; i < numClients; i++) {
                chunkSizes[i] = product[i] / sum;
//                System.out.println("Chunksize" + i + ": " + chunkSizes[i]);
            }
        } else if (order == 2) {
            StatsCalculator[] stats = new StatsCalculator[numClients];
            int i = 0;
            for (String ip : statTable.keySet()) {
                stats[i++] = statTable.get(ip);
            }
            double product[] = new double[numClients];
            double pr = 1;
            for (i = 0; i < numClients; i++) {
                product[i] = Math.pow(stats[i].getCpuFreq() * 100 * (1 - stats[i].getCpuLoad()[0]), 1 / power);
                pr *= product[i];
            }
            double c = Math.pow((Math.pow(power, fileSize) / pr), 1 / numClients);
            for (i = 0; i < numClients; i++) {
                chunkSizes[i] = Math.log(c * product[i]) / (fileSize * Math.log(power));
            }
        }
        FileHandler.getInstance().saveObject("filechunks", chunkSizes);
    }

}
