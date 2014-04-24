package iomodule;

import java.io.*;
import java.util.StringTokenizer;

public class FileHandler {

    public static long getNumInts(String file) {
        try {
            File f = new File(file);
            BufferedReader in = new BufferedReader(new FileReader(f));
            String s = in.readLine();
            in.close();
            return (long) new StringTokenizer(s).countTokens();
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
