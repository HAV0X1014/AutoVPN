package miat.FileHandlers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ReadFirstLine {
    public static String read(String filepath) {
        String line = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filepath));
            line = reader.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }
}
