package miat.UtilityCommands;

import miat.FileHandlers.ReadFull;

import java.io.*;
import java.nio.file.Files;

public class CreateVPN {
    public static InputStream create(String userID, String key, String sudoPassword) {
        try {
            String[] makeClient = {"/bin/sh", "-c", "cd /etc/openvpn/server/easy-rsa/ && echo '" + sudoPassword + "' | sudo -S -k  EASYRSA_CERT_EXPIRE=3650 ./easyrsa --passin=pass:" + key + " build-client-full " + userID + " nopass"};

            ProcessBuilder processBuilder = new ProcessBuilder(makeClient);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuilder caFileContents = new StringBuilder();
        try {
            String[] readCert = {"/bin/sh", "-c", "echo '" + sudoPassword + "' | sudo -S -k cat /etc/openvpn/server/easy-rsa/pki/ca.crt"};

            ProcessBuilder processBuilder = new ProcessBuilder(readCert);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                caFileContents.append(line).append("\n");
            }
            System.out.println(caFileContents.toString());
            reader.close();
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuilder crtFileContents = new StringBuilder();
        try {
            String[] readCert = {"/bin/sh", "-c", "echo '" + sudoPassword + "' | sudo -S -k cat /etc/openvpn/server/easy-rsa/pki/issued/" + userID + ".crt"};

            ProcessBuilder processBuilder = new ProcessBuilder(readCert);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                crtFileContents.append(line).append("\n");
            }
            System.out.println(crtFileContents.toString());
            reader.close();
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuilder keyFileContents = new StringBuilder();
        try {
            String[] readKey = {"/bin/sh", "-c", "echo '" + sudoPassword + "' | sudo -S -k cat /etc/openvpn/server/easy-rsa/pki/private/" + userID + ".key"};

            ProcessBuilder processBuilder = new ProcessBuilder(readKey);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                keyFileContents.append(line).append("\n");
            }
            System.out.println(keyFileContents.toString());
            reader.close();
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuilder tcFileContents = new StringBuilder();
        try {
            String[] readKey = {"/bin/sh", "-c", "echo '" + sudoPassword + "' | sudo -S -k cat /etc/openvpn/server/tc.key"};

            ProcessBuilder processBuilder = new ProcessBuilder(readKey);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                tcFileContents.append(line).append("\n");
            }
            System.out.println(tcFileContents.toString());
            reader.close();
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuilder completeOVPN = new StringBuilder();
        completeOVPN.append(ReadFull.read("/etc/openvpn/server/client-common.txt"));
        completeOVPN.append("<ca>\n");
        completeOVPN.append(caFileContents);
        completeOVPN.append("</ca>\n");
        completeOVPN.append("<cert>\n");
        int startIndex1 = crtFileContents.indexOf("-----BEGIN CERTIFICATE-----");
        String crtString = crtFileContents.substring(startIndex1);
        completeOVPN.append(crtString);
        completeOVPN.append("</cert>\n");
        completeOVPN.append("<key>\n");
        completeOVPN.append(keyFileContents);
        completeOVPN.append("</key>\n");
        completeOVPN.append("<tls-crypt>\n");
        int startIndex2 = tcFileContents.indexOf("-----BEGIN OpenVPN Static key V1-----");
        String tcString = tcFileContents.substring(startIndex2);
        completeOVPN.append(tcString);
        completeOVPN.append("</tls-crypt>");

        System.out.println(completeOVPN.toString());

        File tempFile = null;
        try {
            tempFile = Files.createTempFile(userID, ".ovpn").toFile();
            FileWriter fw = new FileWriter(tempFile);
            fw.write(completeOVPN.toString());
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream ovpn = null;
        try {
            ovpn = new FileInputStream(tempFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ovpn;
    }
}
