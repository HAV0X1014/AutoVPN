package miat;

import miat.FileHandlers.*;
import miat.UtilityCommands.CreateVPN;
import miat.UtilityCommands.RevokeVPN;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;

import java.time.Duration;
import java.util.Arrays;

public class VPNMain {
    public static String configFile = ReadFull.read("ServerFiles/config.json");
    static String token = ConfigHandler.getString("Token", configFile);
    static String statusText = ConfigHandler.getString("StatusText", configFile);
    static Boolean registerSlashCommands = ConfigHandler.getBoolean("RegisterSlashCommands", configFile);
    static String key = ConfigHandler.getString("Key", configFile);
    static String sudoPassword = ConfigHandler.getString("SudoPassword", configFile);
    static String prefix = ConfigHandler.getString("Prefix", configFile);

    public static void main(String[] args) {
        DiscordApi api = new DiscordApiBuilder().setToken(token).setIntents(Intent.MESSAGE_CONTENT, Intent.GUILD_MESSAGES, Intent.DIRECT_MESSAGES).login().join();
        int startTime = (int) (System.currentTimeMillis() / 1000);
        User self = api.getYourself();
        System.out.println(self.getName() + " logged in.");
        api.updateActivity(ActivityType.PLAYING,statusText);

        if (registerSlashCommands) {
            SlashCommand.with("vpn", "Make a private VPN connection file specifically for you. One per person.").createGlobal(api).join();
            SlashCommand.with("deletevpn","Revoke your VPN connection. !!WARNING!! This will remove your ability to connect to the VPN!", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.BOOLEAN, "Check", "THIS WILL MAKE YOUR CONNECTION FILE INVALID!!", true))).createGlobal(api).join();
            SlashCommand.with("revokevpn","(Whitelist/owner ONLY) Revoke a user's VPN access by userID.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.STRING, "User", "Revokes the selected user's access", true))).createGlobal(api).join();
            System.out.println("SLASH COMMANDS REGISTERED! Set \"RegisterSlashCommands\" to \"false\" in config.json!");
        }
        api.addSlashCommandCreateListener(event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            String command = interaction.getCommandName();
            switch (command) {
                case "vpn":
                    String userIDvpn = interaction.getUser().getIdAsString();
                    interaction.getUser().openPrivateChannel().join().sendMessage(CreateVPN.create(userIDvpn, key, sudoPassword), userIDvpn + ".ovpn");
                    interaction.createImmediateResponder().setContent("The ``.ovpn`` connection file has been sent to your DMs!\nDownload [OpenVPN Connect](https://openvpn.net/client/), and import the file to connect to the server.\nIf you wish to remove access to your VPN file, use ``/deletevpn``.").respond();
                    break;
                case "deletevpn":
                    String userIDdlt = interaction.getUser().getIdAsString();
                    RevokeVPN.revoke(userIDdlt, key, sudoPassword);
                    interaction.createImmediateResponder().setContent("Your previous ``.ovpn`` file has been deleted/revoked.").setFlags(MessageFlag.EPHEMERAL).respond();
                    interaction.getUser().openPrivateChannel().join().sendMessage("Your previous ``.ovpn`` file has been deleted/revoked.");
                    break;
                case "revokevpn":
                    if (Whitelist.whitelisted(interaction.getUser().getIdAsString())) {
                        String userIDrvk = interaction.getArgumentByName("user").get().getStringValue().get();
                        RevokeVPN.revoke(userIDrvk, key, sudoPassword);
                        interaction.createImmediateResponder().setContent("``.ovpn`` connection for <@" + userIDrvk + "> has been revoked.").setFlags(MessageFlag.EPHEMERAL).respond();
                    } else {
                        interaction.createImmediateResponder().setContent("You must be on the whitelist to execute this command.").setFlags(MessageFlag.EPHEMERAL).respond();
                    }
                    break;
            }
        });
        api.addMessageCreateListener(mc -> {
            String m = mc.getMessageContent();
            if (m.startsWith(prefix)) {
                String[] parts = m.split(" ",2);
                String command = parts[0].toLowerCase().replace(prefix,"");
                switch (command) {
                    case "ping":
                        mc.getMessage().reply("``Pong``");
                        break;
                    case "uptime":
                        int systemTime = (int) (System.currentTimeMillis() / 1000);
                        long upTimeSeconds = systemTime - startTime;
                        Duration duration = Duration.ofSeconds(upTimeSeconds);
                        long hours = duration.toHours();
                        long minutes = duration.minusHours(hours).toMinutes();
                        mc.getMessage().reply(String.format("``%04d`` hours, ``%02d`` minutes",hours,minutes));
                    case "vpn":
                        String userID = mc.getMessage().getAuthor().getIdAsString();
                        mc.getMessage().getUserAuthor().get().sendMessage(CreateVPN.create(userID, key, sudoPassword), userID + ".ovpn");
                        mc.getMessage().reply("The ``.ovpn`` connection file has been sent to your DMs!\nDownload [OpenVPN Connect](https://openvpn.net/client/), and import the file to connect to the server.\nIf you wish to remove access to your VPN file, use ``/deletevpn``.");
                }
            }
        });
    }
}
