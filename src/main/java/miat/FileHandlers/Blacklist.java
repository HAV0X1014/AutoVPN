package miat.FileHandlers;

import java.io.FileWriter;
import java.io.IOException;

import static miat.VPNMain.blacklist;

public class Blacklist {
    public static void add(String userID) {
        blacklist.getJSONArray("Blacklist").put(userID);
        try {
            FileWriter fw = new FileWriter("ServerFiles/blacklist.json");
            fw.write(String.valueOf(blacklist));
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void remove(String userID) {
        for (int i = 0; i < blacklist.getJSONArray("Blacklist").length(); i++) {
            if (blacklist.getJSONArray("Blacklist").get(i).toString().equals(userID)) {
                blacklist.getJSONArray("Blacklist").remove(i);
                break;
            }
        }
        try {
            FileWriter fw = new FileWriter("ServerFiles/blacklist.json");
            fw.write(String.valueOf(blacklist));
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static boolean check(String userID) {
        for (int i = 0; i < blacklist.getJSONArray("Blacklist").length(); i++) {
            if (blacklist.getJSONArray("Blacklist").get(i).toString().equals(userID)) {
                return true;
            }
        }
        return false;
    }
}
