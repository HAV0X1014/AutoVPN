package miat.UtilityCommands;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class RevokeVPN {
    public static void revoke(String userID, String key, String sudoPassword) {
        try {
            String[] batchRevoke = {"/bin/sh", "-c", "cd /etc/openvpn/server/easy-rsa/ && echo '" + sudoPassword + "' | sudo -S -k ./easyrsa --passin=pass:" + key + " --batch revoke " + userID + " && EASYRSA_CRL_DAYS=3650 ./easyrsa --passin=pass:" + key + " gen-crl && rm -f /etc/openvpn/server/crl.pem && cp /etc/openvpn/server/easy-rsa/pki/crl.pem /etc/openvpn/server/crl.pem && chown nobody:nogroup /etc/openvpn/server/crl.pem"};
            ProcessBuilder processBuilder = new ProcessBuilder(batchRevoke);
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
    }
}