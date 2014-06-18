package executormodule;

import java.io.IOException;
import controlmodule.ControlModule;

public class ExecuteJAR implements Runnable {

    private String programPath;
    private String args;
    private Runnable parent;

    public ExecuteJAR(String ppath, String args) {
        this.programPath = ppath;
        this.args = args;
        this.parent = null;
    }

    public ExecuteJAR(String ppath, String args, Runnable parent) {
        this(ppath, args);
        this.parent = parent;
    }

    public void run() {
        try {
            Process p = Runtime.getRuntime().exec("java -jar " + programPath + " " + args);
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if (parent != null) {
            synchronized (parent) {
                parent.notify();
            }
        }
    }
}
