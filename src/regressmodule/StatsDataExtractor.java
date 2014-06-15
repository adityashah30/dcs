package regressmodule;

import java.util.ArrayList;
import java.io.*;
import iomodule.FileHandler;
import statsmodule.*;

public class StatsDataExtractor {

    private String filename;
    private String outputFile;
    ArrayList<Stats> clientStats;

    public StatsDataExtractor() {
        filename = "clientstats";
        outputFile = "data/regressdata";
        File f = new File(outputFile);
        if (!f.exists()) {
            f.mkdirs();
        }
        clientStats = (ArrayList<Stats>) FileHandler.loadObject(filename);
        extractData();
    }

    public void extractData() {
        BufferedWriter fout;
        try {
            fout = new BufferedWriter(new FileWriter(outputFile));
            for (Stats st : clientStats) {
                long time = st.getTime();
                double chunkSize = st.getChunkSize();
                int frequency = st.getStatsCalculator().getCpuFreq();
                double load = st.getStatsCalculator().getCpuLoad()[0];
                fout.write(time + " " + chunkSize + " " + frequency + " " + load + "\n");
            }
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
