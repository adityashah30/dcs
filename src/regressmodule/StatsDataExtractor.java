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
        filename = "clientstats.bin";
        File outputDir = new File("data");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        outputFile = "data/regressdata.txt";
        File f = new File(outputFile);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        clientStats = (ArrayList<Stats>) FileHandler.loadObject(filename);
        extractData();
    }

    public void extractData() {
        BufferedWriter fout;
        try {
            fout = new BufferedWriter(new FileWriter(new File(outputFile)));
            for (Stats st : clientStats) {
                long time = st.getTime();
                double chunkSize = Math.pow(st.getChunkSize() * st.getFileSize(), st.getPower());
                int frequency = st.getStatsCalculator().getCpuFreq();
                double load = st.getStatsCalculator().getCpuLoad()[0];
                fout.write(time + " " + chunkSize + " " + frequency + " " + load + "\n");
            }
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();;
        }
    }
}
