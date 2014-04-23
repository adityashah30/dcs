package distributormodule;

import iomodule.FileHandler;
import java.util.Hashtable;
import statsmodule.StatsCalculator;

public class ClassicDistributor {

    private Hashtable<String, StatsCalculator> statTable;
    private int numClients;
    private double[] chunkSizes;

    public ClassicDistributor() {
        this.statTable = (Hashtable<String, StatsCalculator>) (FileHandler.loadObject("clientstats"));
        this.numClients = statTable.size();
        this.chunkSizes = new double[numClients];
        calculate();
    }

    public void calculate() {
        for (int i = 0; i < numClients; i++) {
            chunkSizes[i] = 1 / (double)numClients;
        }
        FileHandler.saveObject("filechunks", chunkSizes);
    }
}
