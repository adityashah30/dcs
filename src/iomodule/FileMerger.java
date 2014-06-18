package iomodule;

import java.util.ArrayList;
import java.util.LinkedList;
import java.io.File;
import statsmodule.Stats;
import executormodule.ExecuteJAR;

public class FileMerger {

    private int numFiles;
    private String mergePath;

    public FileMerger(String mergePath) {
        if (mergePath == null || mergePath.trim() == null || mergePath == "") {
            this.mergePath = "merge.jar";
        } else {
            this.mergePath = mergePath;
        }
        numFiles = ((ArrayList<Stats>) FileHandler.loadObject("clientstats.bin")).size();
        mergeFiles();
    }

    public void mergeFiles() {
        try {
            LinkedList<String> fqueue = new LinkedList<String>();
            LinkedList<String> dqueue = new LinkedList<String>();
            String file1, file2;
            for (int i = 0; i < numFiles; i++) {
                fqueue.addLast("file" + i + ".txt");
            }
            while (fqueue.size() != 1) {
                int qSize = fqueue.size();
                Thread[] threads = new Thread[qSize / 2];
                for (int i = 0; i < threads.length; i++) {
                    file1 = fqueue.removeFirst();
                    file2 = fqueue.removeFirst();
                    threads[i] = new Thread(new ExecuteJAR(mergePath, file1 + " " + file2));
                    threads[i].start();
                    fqueue.addLast(file1);
                    dqueue.addLast(file2);
                }
                if (qSize % 2 == 1) {
                    fqueue.addLast(fqueue.removeFirst());
                }
                for (int i = 0; i < threads.length; i++) {
                    threads[i].join();
                }
                int dsize = dqueue.size();
                for (int i = 0; i < dsize; i++) {
                    File f = new File(dqueue.removeFirst());
                    if (f.exists()) {
                        f.delete();
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
