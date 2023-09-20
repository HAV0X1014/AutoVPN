package miat;

import miat.FileHandlers.*;
import miat.UtilityCommands.CreateVPN;
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
    static String serverID = ConfigHandler.getString("ServerID", configFile);
    public static void main(String[] args) {
        DiscordApi api = new DiscordApiBuilder().setToken(token).setIntents(Intent.MESSAGE_CONTENT, Intent.GUILD_MESSAGES, Intent.DIRECT_MESSAGES).login().join();
        int startTime = (int) (System.currentTimeMillis() / 1000);
        User self = api.getYourself();
        System.out.println(self.getName() + " logged in.");
        api.updateActivity(ActivityType.PLAYING,statusText);

        if (registerSlashCommands) {
            SlashCommand.with("vpn", "Make a private VPN connection file for you. One per person.").createForServer(api, Long.parseLong(serverID)).join();
            SlashCommand.with("deletevpn","Revoke your VPN connection.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.BOOLEAN, "Check", "THIS WILL MAKE YOUR CONNECTION FILE INVALID!!", true))).createForServer(api, Long.parseLong(serverID)).join();
            SlashCommand.with("revokevpn","(Whitelist/owner ONLY) Revoke a user's VPN access.", Arrays.asList(SlashCommandOption.create(SlashCommandOptionType.STRING, "User", "Revokes the selected user's access", true))).createForServer(api, Long.parseLong(serverID)).join();
            System.out.println("SLASH COMMANDS REGISTERED! Set \"RegisterSlashCommands\" to \"false\" in config.json!");
        }
        api.addSlashCommandCreateListener(event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            String command = interaction.getCommandName();
            switch (command) {
                case "vpn":
                    String uid = String.valueOf(interaction.getUser().getId());
                    interaction.getUser().openPrivateChannel().join().sendMessage(CreateVPN.create(uid, key, sudoPassword));
                    interaction.createImmediateResponder().setContent("The ``.ovpn`` connection file has been sent to your DMs!").setFlags(MessageFlag.EPHEMERAL).respond();
                    break;
                case "deletevpn":
                    interaction.createImmediateResponder().setContent("Not yet implemented").setFlags(MessageFlag.EPHEMERAL).respond();
                    break;
                case "revokevpn":
                    interaction.createImmediateResponder().setContent("Not yet implemented").setFlags(MessageFlag.EPHEMERAL).respond();
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
                }
            }
        });
    }
}
