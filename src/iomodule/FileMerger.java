package iomodule;

import java.util.Hashtable;
import java.util.LinkedList;
import java.io.File;
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
        numFiles = ((Hashtable<String, String>) FileHandler.loadObject("clientfiles")).size();
        mergeFiles();
    }

    public void mergeFiles() {
        try {
            LinkedList<String> fqueue = new LinkedList<String>();
            String file1, file2 = null, lastFile;
            int i;
            for (i = 0; i < numFiles; i++) {
                fqueue.add("file" + i + ".txt");
            }
            while (fqueue.size() != 1) {
                Thread threads[] = new Thread[fqueue.size()];
                if (fqueue.size() % 2 == 0) {
                    for (i = 0; i < fqueue.size(); i += 2) {
                        file1 = fqueue.remove();
                        file2 = fqueue.remove();
                        threads[i / 2] = new Thread(new ExecuteJAR(mergePath, file1 + " " + file2));
                        threads[i / 2].start();
                        fqueue.add(file1);
                    }
                    for (i = 0; i < fqueue.size(); i++) {
                        threads[i].join();
                    }
                } else {
                    for (i = 0; i < fqueue.size() - 1; i += 2) {
                        file1 = fqueue.remove();
                        file2 = fqueue.remove();
                        threads[i / 2] = new Thread(new ExecuteJAR(mergePath, file1 + " " + file2));
                        threads[i / 2].start();
                        fqueue.add(file1);
                    }
                    for (i = 0; i < fqueue.size() - 1; i++) {
                        threads[i].join();
                    }
                    lastFile = fqueue.remove();
                    fqueue.add(lastFile);
                }
                File f = new File(file2);
                if (f.exists()) {
                    f.delete();
                }

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
