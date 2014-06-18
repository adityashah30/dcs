package statsmodule;

import java.io.Serializable;

public class Stats implements Serializable {

    private StatsCalculator statsCalculator;
    private double chunkSize;
    private static double power;
    private static long fileSize;
    private String ipAddress;
    private String filename;
    private long time;

    public Stats() {

    }

    public Stats(StatsCalculator st) {
        statsCalculator = st;
    }

    public StatsCalculator getStatsCalculator() {
        return statsCalculator;
    }

    public void setStatsCalculator(StatsCalculator statsCalculator) {
        this.statsCalculator = statsCalculator;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(double chunkSize) {
        this.chunkSize = chunkSize;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public static double getPower() {
        return Stats.power;
    }

    public static void setPower(double power) {
        Stats.power = power;
    }

    public static long getFileSize() {
        return Stats.fileSize;
    }

    public static void setFileSize(long fileSize) {
        Stats.fileSize = fileSize;
    }

}
