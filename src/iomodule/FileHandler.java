package iomodule;

import java.util.Scanner;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public synchronized Object loadObject(String file) {
        Object obj = null;
        ObjectInputStream in = null;
        File f = new File(file);
        try {
            in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));
            try {
                obj = in.readObject();
            } catch (ClassNotFoundException ex) {
            }
        } catch (FileNotFoundException e) {
            new File(file);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }

    public synchronized void saveObject(String file, Object obj) {
        File f = new File(file);
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
            out.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
