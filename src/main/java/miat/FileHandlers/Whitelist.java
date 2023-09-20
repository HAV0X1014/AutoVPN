package miat.FileHandlers;

import java.util.Arrays;

public class Whitelist {
    public static boolean whitelisted(String userID) {
        String configFile = ReadFull.read("ServerFiles/config.json");
        String[] whitelistedMembersArray = miat.FileHandlers.ConfigHandler.getArray("Whitelist", configFile);
        boolean whitelisted = false;
        String whitelistedMembers = Arrays.toString(whitelistedMembersArray);
        if (whitelistedMembers.contains(userID)) {
            whitelisted = true;
        }

        return whitelisted;
    }
}