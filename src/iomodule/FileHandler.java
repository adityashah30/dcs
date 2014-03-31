package iomodule;

import java.util.Scanner;
import java.io.*;

public class FileHandler {

    private static FileHandler instance = null;

    public static FileHandler getInstance() {
        if (instance == null) {
            instance = new FileHandler();
        }
        return instance;
    }

    public static long getNumInts(String file) {
        try {
            File f = new File(file);
            Scanner in = new Scanner(new BufferedReader(new FileReader(f)));
            long count = 0;
            while (in.hasNextInt()) {
                int i = in.nextInt();
                count++;
            }
            return count;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Object loadObject(String file) {
        Object obj = null;
        File f = new File(file);
        try {
            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));
            try {
                obj = in.readObject();
            } catch (ClassNotFoundException ex) {
            }
            in.close();
        } catch (FileNotFoundException e) {
            new File(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public synchronized static void saveObject(String file, Object obj) {
        File f = new File(file);
        try {
            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
            out.writeObject(obj);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
