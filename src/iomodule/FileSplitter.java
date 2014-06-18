package iomodule;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import statsmodule.Stats;

public class FileSplitter {

    private File file;
    private ArrayList<Stats> clientStats;
    private int numClients;
    private long fileSize;

    public FileSplitter(String fname, long fsize) {

        file = new File(fname);
        fileSize = fsize;
        clientStats = (ArrayList<Stats>) FileHandler.loadObject("clientstats.bin");
        numClients = clientStats.size();
        split();
    }

    public void split() {
        try {
            long startindex = 0;
            long lastindex = -1;
            Scanner in = new Scanner(new BufferedReader(new FileReader(file)));
            for (int fileCount = 0; fileCount < numClients; fileCount++) {
                File newFile = new File(clientStats.get(fileCount).getFilename());
                BufferedWriter out = new BufferedWriter(new FileWriter(newFile));
                int size = (int) (clientStats.get(fileCount).getChunkSize() * fileSize);
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

}
