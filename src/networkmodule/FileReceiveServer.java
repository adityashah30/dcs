package networkmodule;

import iomodule.FileHandler;
import java.io.*;
import java.net.*;
import java.util.*;
import statsmodule.Stats;

public class FileReceiveServer implements Runnable {

    private ServerSocket server;
    private int receiveport;
    private boolean isMaster;
    private boolean isStats;
    private ArrayList<Thread> clientThreads;
    private int totalClients;
    private int clientsReceived;

    public FileReceiveServer(int receiveport, boolean isMaster, boolean isStats) {
        this.receiveport = receiveport;
        this.isMaster = isMaster;
        this.isStats = isStats;
        clientThreads = new ArrayList<Thread>();
        if (isMaster && !isStats) {
            totalClients = ((ArrayList<Stats>) FileHandler.loadObject("clientstats.bin")).size();
            clientsReceived = 0;
        }
        try {
            server = new ServerSocket(receiveport);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                Socket clientSocket = server.accept();
                Thread newClientThread = new Thread(new FileReceiverThread(clientSocket, isMaster, isStats));
                clientThreads.add(newClientThread);
                newClientThread.start();
                if (isMaster && !isStats) {
                    clientsReceived++;
                }
            }
        } catch (IOException e) {
        }
    }

    public boolean checkThreadCompletion() {
        if (isMaster) {
            if (clientsReceived < totalClients) {
                return false;
            }
            if (clientThreads.size() > 0) {
                for (Thread t : clientThreads) {
                    if (t != null) {
                        if (t.isAlive()) {
                            return false;
                        }
                    }
                }
                return true;
            }
            return false;
        } else {
            if (clientThreads.size() > 0) {
                for (Thread t : clientThreads) {
                    if (t != null) {
                        if (t.isAlive()) {
                            return false;
                        }
                    }
                }
                return true;
            }
            return false;
        }
    }

    public void stopServer() {
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
