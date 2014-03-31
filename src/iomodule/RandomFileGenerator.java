/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iomodule;

import java.io.*;

public class RandomFileGenerator {

    public static void main(String[] args) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter("data.txt"));
        for (int i = 0; i < 100000; i++) {
            int randInt = (int) (Math.random() * (10000000));
            out.write(String.valueOf(randInt) + " ");
        }
        out.close();
    }
}
