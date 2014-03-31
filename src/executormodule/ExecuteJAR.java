package executormodule;

import java.io.IOException;

public class ExecuteJAR implements Runnable {

    private String programPath;
    private String args;

    public ExecuteJAR(String ppath, String args) {
        this.programPath = ppath;
        this.args = args;
    }

    public void run() {
        try {
            Process p = Runtime.getRuntime().exec("java -jar " + programPath + " " + args);
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
