package dev.trxsson.onekissbot;

import dev.trxsson.onekissbot.events.commands.OneKissCommandListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommand;

public class OneKissBot {

    public static void main(String[] args) {
        var token = System.getenv("TOKEN");
        DiscordApi api = new DiscordApiBuilder()
                .setToken(token)
                .setAllIntents()
                .login()
                .join();
        SlashCommand.with("onekiss", "One kiss is all it takes!")
                .setEnabledInDms(false)
                .setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR)
                .createGlobal(api)
                .join();
        api.addListener(new OneKissCommandListener(api));
        System.out.println("LOADED!");
        System.out.println("Invite link: "+ api.createBotInvite());
    }

}
