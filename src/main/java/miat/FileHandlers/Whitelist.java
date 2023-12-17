package miat.FileHandlers;

import java.util.Arrays;

import static miat.VPNMain.configFile;

public class Whitelist {
    public static boolean whitelisted(String userID) {
        String[] whitelistedMembersArray = miat.FileHandlers.ConfigHandler.getArray("Whitelist", configFile);
        boolean whitelisted = false;
        String whitelistedMembers = Arrays.toString(whitelistedMembersArray);
        if (whitelistedMembers.contains(userID)) {
            whitelisted = true;
        }

        return whitelisted;
    }
}