package iomodule;

import java.io.*;
import java.util.Hashtable;
import java.util.Scanner;
import statsmodule.StatsCalculator;

public class FileSplitter {

    private File file;
    private double[] chunkSizes;
    private int numClients;
    private long fileSize;

    public FileSplitter(String fname) {

        file = new File(fname);
        fileSize = FileHandler.getInstance().getNumInts(fname);
        chunkSizes = (double[]) FileHandler.getInstance().loadObject("filechunks");
        numClients = chunkSizes.length;
        split();
        writeClientFiles();
    }

    public void split() {
        try {
            long startindex = 0;
            long lastindex = -1;
            Scanner in = new Scanner(new BufferedReader(new FileReader(file)));
            for (int fileCount = 0; fileCount < numClients; fileCount++) {
                File newFile = new File("file" + fileCount + ".txt");
                BufferedWriter out = new BufferedWriter(new FileWriter(newFile));
                int size = (int) (chunkSizes[fileCount] * fileSize);
                startindex = lastindex + 1;
                if (fileCount == numClients - 1) {
                    lastindex = fileSize - 1;
                } else {
                    lastindex = startindex + size - 1;
                }
                for (int i = 0; i <= lastindex - startindex; i++) {
                    out.write(String.valueOf(in.nextInt()) + " ");
                }
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readWrite(RandomAccessFile raf, BufferedOutputStream out, long numInts) {
        try {
            for (int i = 0; i < numInts; i++) {
                out.write(raf.readInt());
            }
        } catch (IOException e) {
        }
    }

    public void writeClientFiles() {
        Hashtable<String, String> clientFiles = new Hashtable<String, String>();
        Hashtable<String, StatsCalculator> clientStats = (Hashtable<String, StatsCalculator>) FileHandler.getInstance().loadObject("clientstats");
        int i = 0;
        for (String ipAddress : clientStats.keySet()) {
            clientFiles.put(ipAddress, "file" + i + ".txt");
            i++;
        }
        FileHandler.getInstance().saveObject("clientfiles", clientFiles);
    }

}
