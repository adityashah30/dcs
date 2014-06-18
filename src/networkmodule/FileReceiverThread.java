package networkmodule;

import java.io.*;
import java.net.*;
import java.util.*;
import iomodule.FileHandler;
import statsmodule.StatsCalculator;
import statsmodule.Stats;

public class FileReceiverThread implements Runnable {

    private Socket socket;
    private File outputFile;
    private boolean isMaster;
    private boolean isStats;
    private static FileReceiverThread instance = null;

    public FileReceiverThread() {
    }

    public FileReceiverThread(Socket socket, boolean isMaster, boolean isStats) {
        this.socket = socket;
        this.isMaster = isMaster;
        this.isStats = isStats;
    }

    public static FileReceiverThread getInstance() {
        if (instance == null) {
            instance = new FileReceiverThread();
        }
        return instance;
    }

    public void run() {
        try {
            int bufferSize = 1024;
            byte[] fileBuff = new byte[bufferSize];
            if (isMaster) {
                if (isStats) {
                    String ipAddress = socket.getInetAddress().toString().substring(1);
                    File tempFile = new File(ipAddress);
                    long fileSize = 0;
                    int n = 0;
                    DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream(), bufferSize));
                    FileOutputStream fout = new FileOutputStream(tempFile);
                    fileSize = in.readLong();
                    while ((fileSize > 0) && (n = in.read(fileBuff, 0, (int) Math.min(bufferSize, fileSize))) != -1) {
                        fout.write(fileBuff, 0, n);
                        fout.flush();
                        fileSize -= n;
                    }
                    in.close();
                    fout.close();
                    StatsCalculator statsObject = (StatsCalculator) FileHandler.loadObject(ipAddress);
                    this.getInstance().writeStats(ipAddress, statsObject);
                    tempFile.delete();
                } else {
                    ArrayList<Stats> clientStats = (ArrayList<Stats>) FileHandler.loadObject("clientstats.bin");
                    String filename = "";
                    String socketIpAddress = socket.getInetAddress().toString().substring(1);
                    for (Stats st : clientStats) {
                        if (st.getIpAddress().equals(socketIpAddress)) {
                            filename = st.getFilename();
                            break;
                        }
                    }
                    outputFile = new File(filename);
                    long fileSize = 0;
                    int n = 0;
                    DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream(), bufferSize));
                    FileOutputStream fout = new FileOutputStream(outputFile);
                    long runningTime = in.readLong();
                    this.getInstance().writeTime(socket.getInetAddress().toString().substring(1), runningTime);
                    fileSize = in.readLong();
                    while ((fileSize > 0) && (n = in.read(fileBuff, 0, (int) Math.min(bufferSize, fileSize))) != -1) {
                        fout.write(fileBuff, 0, n);
                        fout.flush();
                        fileSize -= n;
                    }
                    in.close();
                    fout.close();
                }
            } else {
                long programSize = 0;
                long fileSize = 0;
                int n = 0;
                outputFile = new File("program.jar");
                DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream(), bufferSize));
                BufferedOutputStream fout = new BufferedOutputStream(new FileOutputStream(outputFile));
                programSize = in.readLong();
                while ((programSize > 0) && (n = in.read(fileBuff, 0, (int) Math.min(bufferSize, programSize))) != -1) {
                    fout.write(fileBuff, 0, n);
                    fout.flush();
                    programSize -= n;
                }
                fout.close();
                outputFile = new File("file1.txt");
                fout = new BufferedOutputStream(new FileOutputStream(outputFile));
                fileSize = in.readLong();
                while ((fileSize > 0) && (n = in.read(fileBuff, 0, (int) Math.min(bufferSize, fileSize))) != -1) {
                    fout.write(fileBuff, 0, n);
                    fout.flush();
                    fileSize -= n;
                }
                fout.close();
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static synchronized void writeStats(String ipAddress, StatsCalculator statsObject) {
        ArrayList<Stats> clientStats = (ArrayList<Stats>) FileHandler.loadObject("clientstats.bin");
        if (clientStats == null) {
            clientStats = new ArrayList<>();
        }
        Stats statsObj = new Stats(statsObject);
        statsObj.setIpAddress(ipAddress);
        statsObj.setFilename("file" + clientStats.size() + ".txt");
        clientStats.add(statsObj);
        FileHandler.saveObject("clientstats.bin", clientStats);
    }

    public static synchronized void writeTime(String ipAddress, long time) {
        ArrayList<Stats> clientStats = (ArrayList<Stats>) FileHandler.loadObject("clientstats.bin");
        for (int i = 0; i < clientStats.size(); i++) {
            if (clientStats.get(i).getIpAddress().equals(ipAddress)) {
                clientStats.get(i).setTime(time);
                break;
            }
        }
        FileHandler.saveObject("clientstats.bin", clientStats);
    }
}
