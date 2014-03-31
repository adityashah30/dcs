package networkmodule;

import java.io.*;
import java.net.*;
import java.util.*;
import iomodule.FileHandler;
import statsmodule.StatsCalculator;

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
                    File tempFile = new File("tempstats");
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
                    String ipAddress = socket.getInetAddress().toString().substring(1);
                    StatsCalculator statsObject = (StatsCalculator) FileHandler.getInstance().loadObject("tempstats");
                    this.getInstance().writeStats(ipAddress, statsObject);
                    tempFile.delete();
                } else {
                    Hashtable<String, String> clientfiles = (Hashtable< String, String>) FileHandler.getInstance().loadObject("clientfiles");
                    String filename = clientfiles.get(socket.getInetAddress().toString().substring(1));
                    outputFile = new File(filename);
                    long fileSize = 0;
                    int n = 0;
                    DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream(), bufferSize));
                    FileOutputStream fout = new FileOutputStream(outputFile);
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

    public synchronized static void writeStats(String ipAddress, StatsCalculator statsObject) {
        Hashtable<String, StatsCalculator> clientStats = (Hashtable<String, StatsCalculator>) FileHandler.getInstance().loadObject("clientstats");
        if (clientStats == null) {
            clientStats = new Hashtable<String, StatsCalculator>();
        }
        clientStats.put(ipAddress, statsObject);
        FileHandler.getInstance().saveObject("clientstats", clientStats);

    }
}
