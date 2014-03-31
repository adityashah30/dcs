package networkmodule;

import java.io.IOException;
import java.net.*;

public class BroadcastThread implements Runnable {

    private int sendport;
    private DatagramSocket broadcastSocket;
    private DatagramPacket packet;
    private boolean runFlag;

    public BroadcastThread(int sendport) {
        this.sendport = sendport;
        runFlag = true;
    }

    public void run() {
        try {
            broadcastSocket = new DatagramSocket(sendport);
            byte[] buf = {(byte) 'A', (byte) '7'};
            packet = new DatagramPacket(buf, buf.length);
            packet.setAddress(InetAddress.getByAddress(new byte[]{(byte) 255, (byte) 255, (byte) 255, (byte) 255}));
            packet.setPort(sendport);
            while (runFlag) {
                broadcastSocket.send(packet);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopBroadCast() {
        runFlag = false;
        broadcastSocket.close();
    }
}
