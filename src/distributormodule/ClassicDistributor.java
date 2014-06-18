package distributormodule;

import iomodule.FileHandler;
import java.util.ArrayList;
import statsmodule.Stats;

public class ClassicDistributor {

    private ArrayList<Stats> clientStats;
    private int numClients;

    public ClassicDistributor() {
        this.clientStats = (ArrayList<Stats>) (FileHandler.loadObject("clientstats.bin"));
        this.numClients = clientStats.size();
        calculate();
    }

    public void calculate() {
        for (int i = 0; i < numClients; i++) {
            clientStats.get(i).setChunkSize(1 / (double) numClients);
        }
        FileHandler.saveObject("clientstats.bin", clientStats);
    }
}
