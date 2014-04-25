package iomodule;

import java.io.*;
import java.util.Scanner;

public class FileHandler {

    public static long getNumInts(String file) {
        try {
            File f = new File(file);
            long count = 0;
            Scanner in = new Scanner(new BufferedReader(new FileReader(f)));
            while (in.hasNextInt()) {
                count++;
                int a = in.nextInt();
            }
            in.close();
            return count;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static synchronized Object loadObject(String file) {
        Object obj = null;
        ObjectInputStream in = null;
        File f = new File(file);
        try {
            in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));
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

    public static synchronized void saveObject(String file, Object obj) {
        File f = new File(file);
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
            out.writeObject(obj);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
