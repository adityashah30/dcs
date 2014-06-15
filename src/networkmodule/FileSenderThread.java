package networkmodule;

import java.io.*;
import java.net.*;

public class FileSenderThread implements Runnable {

    private File file1;
    private File file2;
    private String ipaddress;
    private int port;
    private Socket sendSocket;
    private boolean isMaster;
    private boolean isrunningtime;
    private long runningTime;

    public FileSenderThread(String filename1, String filename2, String ipaddress, int port, boolean isMaster) {
        file1 = new File(filename1);
        if (filename2 != null) {
            file2 = new File(filename2);
        }
        this.ipaddress = ipaddress;
        this.port = port;
        this.isMaster = isMaster;
        this.sendSocket = null;
        this.isrunningtime = false;
        while (sendSocket == null) {
            try {
                this.sendSocket = new Socket(ipaddress, port);
            } catch (IOException e) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
                this.sendSocket = null;
            }
        }
    }

    public FileSenderThread(String filename1, String filename2, String ipaddress, int port, boolean isMaster, long runningTime) {
        this(filename1, filename2, ipaddress, port, isMaster);
        this.runningTime = runningTime;
        this.isrunningtime = true;
    }

    public void run() {
        try {
            int bufferSize = 1024;
            byte[] b = new byte[bufferSize];
            if (isMaster) {
                DataOutputStream out = new DataOutputStream(new BufferedOutputStream(sendSocket.getOutputStream(), bufferSize));
                BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file1), bufferSize);
                long fileSize1 = file1.length();
                long fileSize2 = file2.length();
                out.writeLong(fileSize1);
                out.flush();
                int len = 0;
                while ((len = fis.read(b)) != -1) {
                    out.write(b, 0, len);
                    out.flush();
                }
                fis.close();
                fis = new BufferedInputStream(new FileInputStream(file2), bufferSize);
                out.writeLong(fileSize2);
                out.flush();
                while ((len = fis.read(b)) != -1) {
                    out.write(b, 0, len);
                    out.flush();
                }
                out.close();
                fis.close();
            } else {
                DataOutputStream out = new DataOutputStream((new BufferedOutputStream(sendSocket.getOutputStream(), bufferSize)));
                BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file1), bufferSize);
                if (isrunningtime) {
                    out.writeLong(runningTime);
                }
                long fileSize1 = file1.length();
                out.writeLong(fileSize1);
                out.flush();
                for (int len; (len = fis.read(b)) > 0;) {
                    out.write(b, 0, len);
                    out.flush();
                }
                out.close();
                fis.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                sendSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
