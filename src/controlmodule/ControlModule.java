package controlmodule;

import java.util.concurrent.*;
import javax.swing.JOptionPane;
import java.io.File;
import distributormodule.*;
import executormodule.*;
import iomodule.*;
import java.net.InetAddress;
import java.util.ArrayList;
import networkmodule.*;
import statsmodule.*;
import regressmodule.*;

public class ControlModule implements Runnable {

    private MainGUI gui;
    private int sendPort;
    private int receivePort;
    private int timeOut;
    private boolean isMaster;
    private boolean isAlgo;
    private String programPath;
    private String mergePath;
    private String dataPath;
    private int order;
    private double power;
    private long fileSize;
    private long runningTime;
    private Regressor regressor;

    public ControlModule(MainGUI gui) {
        this.gui = gui;
        sendPort = 6000;
        receivePort = 5000;
        timeOut = 5;
        isMaster = false;
        isAlgo = true;
        programPath = "";
        mergePath = "";
        dataPath = "";
    }

    public void run() {
        if (isMaster) {
            gui.disableStartButton();
            System.out.println("Program starts...");
            gui.setProgressBar(0);
            System.out.println("Broadcast starts...");
            gui.setStatusLabel("BroadCasting");
            broadcast();
            gui.setProgressBar(20);
            System.out.println("Broadcast ends...");
            if (!new File("clientstats.bin").exists()) {
                gui.setStatusLabel("No Clients");
                gui.setProgressBar(100);
                System.out.println("No clients online.. Exiting..");
                JOptionPane.showMessageDialog(gui, "No Clients Online..Exiting..");
                System.exit(0);
            }
            System.out.println("Algo starts...");
            gui.setStatusLabel("Running Algo");
            runAlgo();
            gui.setProgressBar(40);
            System.out.println("Algo ends...");
            System.out.println("FileSplit starts...");
            gui.setStatusLabel("File Splitting");
            splitFile();
            gui.setProgressBar(60);
            System.out.println("FileSplit ends...");
            long startTime = System.currentTimeMillis();
            System.out.println("Send/Receive starts...");
            gui.setStatusLabel("Send/Receive");
            send();
            listen();
            gui.setProgressBar(80);
            System.out.println("Send/Receive ends...");
            System.out.println("Merge starts...");
            gui.setStatusLabel("Merging");
            mergeFiles();
            gui.setProgressBar(100);
            gui.setStatusLabel("Complete");
            System.out.println("Merge ends...");
            long endTime = System.currentTimeMillis();
            regressor = new Regressor();
            fileCleanup();
            System.out.println("Program ends...");
            System.out.println("Time: " + (endTime - startTime) + "ms");
            JOptionPane.showMessageDialog(gui, "Time: " + (endTime - startTime) + "ms");
            gui.enableStartButton();
        } else {
            while (true) {
                gui.disableStartButton();
                gui.setProgressBar(0);
                System.out.println("Program starts...");
                System.out.println("Listening for broadcast...");
                gui.setStatusLabel("Listening for Broadcast");
                listenBroadcast();
                gui.setProgressBar(10);
                System.out.println("Broadcast detected...");
                System.out.println("Calculating Stats...");
                gui.setStatusLabel("Stats Calculation");
                calculateStats();
                gui.setProgressBar(20);
                System.out.println("Calculated Stats...");
                System.out.println("Sending stats...");
                gui.setStatusLabel("Sending Stats");
                sendStats();
                gui.setProgressBar(40);
                System.out.println("Listening for incoming files");
                gui.setStatusLabel("Listening for files");
                listen();
                gui.setProgressBar(60);
                System.out.println("Executing the program...");
                gui.setStatusLabel("Executing Program");
                executeProgram();
                gui.setProgressBar(80);
                System.out.println("Executed the program...");
                System.out.println("Sending back the file...");
                gui.setStatusLabel("Sending Files");
                send();
                gui.setProgressBar(100);
                gui.setStatusLabel("Program Complete");
                System.out.println("Sending complete...");
                fileCleanup();
            }
        }
    }

    public void fileCleanup() {
        if (isMaster) {
            File f = new File("clientstats.bin");
            if (f.exists()) {
                int numClients = ((ArrayList<Stats>) FileHandler.loadObject("clientstats.bin")).size();
                for (int i = 1; i < numClients; i++) {
                    File f2 = new File("file" + i + ".txt");
                    if (f2.exists()) {
                        f2.delete();
                    }
                }
                f.delete();
            }
            f = new File("file0.txt");
            if (f.exists()) {
                File newFile = new File("Processed data.txt");
                if (newFile.exists()) {
                    newFile.delete();
                }
                f.renameTo(newFile);
            }
        } else {
            File f = new File("stats.bin");
            if (f.exists()) {
                f.delete();
            }
            f = new File("serverinfo.bin");
            if (f.exists()) {
                f.delete();
            }
            f = new File("file1.txt");
            if (f.exists()) {
                f.delete();
            }
            f = new File("program.jar");
            if (f.exists()) {
                f.delete();
            }
        }
    }

    public void broadcast() {
        BroadcastThread bthread = new BroadcastThread(sendPort);
        FileReceiveServer serverthread = new FileReceiveServer(receivePort, true, true);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ExecutorService executor1 = Executors.newSingleThreadExecutor();
        Future future = executor.submit(bthread);
        executor1.submit(serverthread);
        try {
            future.get(timeOut, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            bthread.stopBroadCast();
            serverthread.stopServer();
            executor.shutdownNow();
            executor1.shutdownNow();
        }
    }

    public void listenBroadcast() {
        Thread t = new Thread(new BroadcastListener(receivePort));
        t.start();
        while (t.isAlive());
    }

    public void calculateStats() {
        StatsCalculator statsObj = new StatsCalculator();
        FileHandler.saveObject("stats.bin", statsObj);
    }

    public void sendStats() {
        String serverIpAddress = ((InetAddress) FileHandler.loadObject("serverinfo.bin")).toString().substring(1);
        new Thread(new FileSenderThread("stats.bin", null, serverIpAddress, sendPort, isMaster)).start();
    }

    public void runAlgo() {
        if (isAlgo) {
            fileSize = FileHandler.getNumInts(dataPath);
            new Distributor(fileSize, order, power);
        } else {
            new ClassicDistributor();
        }
    }

    public void splitFile() {
        new FileSplitter(dataPath, fileSize);
    }

    public void executeProgram() {
        boolean flag = false;
        Thread t = new Thread(new ExecuteJAR("program.jar", "file1.txt", this));
        long startTime = System.currentTimeMillis();
        t.start();
        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis();
        runningTime = endTime - startTime;
        System.out.println("Program executed in: " + runningTime);
    }

    public void send() {
        if (isMaster) {
            ArrayList<Stats> clientStats = (ArrayList<Stats>) FileHandler.loadObject("clientstats.bin");
            for (int i = 0; i < clientStats.size(); i++) {
                new Thread(new FileSenderThread(programPath, clientStats.get(i).getFilename(), clientStats.get(i).getIpAddress(), sendPort, isMaster)).start();
            }
        } else {
            String serverIpAddress = ((InetAddress) FileHandler.loadObject("serverinfo.bin")).toString().substring(1);
            Thread t = new Thread(new FileSenderThread("file1.txt", null, serverIpAddress, sendPort, isMaster, runningTime));
            t.start();
            while (t.isAlive());
        }
    }

    public void listen() {
        FileReceiveServer server = new FileReceiveServer(receivePort, isMaster, false);
        new Thread(server).start();
        while (!server.checkThreadCompletion()) {
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {

            }
        }
        server.stopServer();
    }

    public void mergeFiles() {
        new FileMerger(mergePath);
    }

    public int getSendPort() {
        return sendPort;
    }

    public void setSendPort(int sendPort) {
        this.sendPort = sendPort;
    }

    public int getReceivePort() {
        return receivePort;
    }

    public void setReceivePort(int receivePort) {
        this.receivePort = receivePort;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public boolean isIsMaster() {
        return isMaster;
    }

    public void setIsMaster(boolean isMaster) {
        this.isMaster = isMaster;
    }

    public String getProgramPath() {
        return programPath;
    }

    public void setProgramPath(String programPath) {
        this.programPath = programPath;
    }

    public String getMergePath() {
        return mergePath;
    }

    public void setMergePath(String mergePath) {
        this.mergePath = mergePath;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public boolean isIsAlgo() {
        return isAlgo;
    }

    public void setIsAlgo(boolean isAlgo) {
        this.isAlgo = isAlgo;
    }
}
