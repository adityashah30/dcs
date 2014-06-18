package networkmodule;

import iomodule.FileHandler;
import java.io.*;
import java.net.*;

public class BroadcastListener implements Runnable {

    InetAddress ipAddress;
    int port;

    public BroadcastListener(int port) {
        this.port = port;
    }

    public void run() {
        DatagramSocket listenSocket = null;
        try {
            listenSocket = new DatagramSocket(port);
            DatagramPacket receivePacket;
            while (true) {
                byte[] buf = new byte[2];
                receivePacket = new DatagramPacket(buf, buf.length);
                listenSocket.receive(receivePacket);
                if (buf[0] == 'A' && buf[1] == '7') {
                    ipAddress = receivePacket.getAddress();
                    break;
                }
            }
            FileHandler.saveObject("serverinfo.bin", ipAddress);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            listenSocket.close();
        }
    }
}
