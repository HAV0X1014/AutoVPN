# AutoVPN
A java discord bot to automatically create .ovpn connection files, using javacord as the frontend.

## Usage
### You will need to use the `openvpn-install.sh` supplied in this repo, as others may not perform the same.

1. Compile the jar with `gradlew shadowjar`, and place it on your server with your config in `~/ServerFiles/config.json`.
2. Run the `openvpn-install.sh` file, and take note of the PEM pass phrase you use. This phrase needs to be more than 3 characters. (Other tips for setup, use TCP for the protocol, 443 for the port, and your Current System Resolvers for the DNS server.)
3. Put your sudo password and PEM pass phrase in `~/ServerFiles/config.json` under "SudoPassword" and "Key" respectively.
4. Fill in the rest of the config with the rest of your information. (Note: "ServerID" is currently unused. Set "RegisterSlashCommands" to `true` on the first run.)
5. Use `tmux` or similar, and run the bot with `java -jar autoVPN-1.0-all.jar`.
6. Set "RegisterSlashCommands" to `false` after the first successful login.

## Notes
- The .ovpn file will be prefixed by the user's userID. For example, if HAV0X#1009 made a connection file, it would be 338129520278110221.ovpn
- Users can delete or revoke access to their existing connection with the /deletevpn command.
- Whitelisted members can revoke access to other user's VPN access with the /revokevpn command. The command accepts userIDs as strings. Add a user's userID to "Whitelist" to give them access. This should only be given to the server operator.

- A good cheap VPS host for the US (in Montreal) is Webdock. My affiliate link -> https://webdock.io/en?maff=wdaff--257

- A good chep VPS host for nearly anywhere else is HostHatch. My affiliate link -> https://cloud.hosthatch.com/a/3718

## Credit
This is based off of Nyr's [openvpn-install](https://github.com/Nyr/openvpn-install) script, using many of the same methods and shell commands. This is essentially an automatic way to generate and revoke new clients without needing access to the command line.

The script this installer is based on is a different version from the latest release. The required script is in this repo.

The "Green Grid" image comes from [here](https://commons.wikimedia.org/wiki/File:Grid_in_green_(15993673965).jpg) licensed under CC-2.0
