package statsmodule;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import java.io.Serializable;

public class StatsCalculator implements Serializable {

    private double freeRAM;
    private int cpuFreq;
    private double[] cpuLoad;
    private int numCores;

    public StatsCalculator() {
        calculateStats();
    }

    public void calculateStats() {
        numCores = Runtime.getRuntime().availableProcessors();
        Sigar sigar = new Sigar();
        try {
            CpuInfo[] cpuInfo = sigar.getCpuInfoList();
            cpuFreq = cpuInfo[0].getMhz();
            Mem memInfo = sigar.getMem();
            freeRAM = memInfo.getFreePercent();
            cpuLoad = sigar.getLoadAverage();
            for (int i = 0; i < getCpuLoad().length; i++) {
                cpuLoad[i] /= numCores;
            }
        } catch (SigarException ex) {
            ex.printStackTrace();
        }
    }

    public double getFreeRAM() {
        return freeRAM;
    }

    public int getCpuFreq() {
        return cpuFreq;
    }

    public double[] getCpuLoad() {
        return cpuLoad;
    }

    public void printStats() {
        System.out.println("---------------------");
        System.out.println("CPUFreq: " + cpuFreq);
        System.out.println("NumCores: " + numCores);
        System.out.println("FreeRAM: " + freeRAM);
        System.out.println("Load: ");
        for (double load : cpuLoad) {
            System.out.println("Load: " + load);
        }
        System.out.println("---------------------");
    }
}
