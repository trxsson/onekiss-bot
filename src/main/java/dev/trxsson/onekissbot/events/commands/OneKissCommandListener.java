package dev.trxsson.onekissbot.events.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.trxsson.onekissbot.audio.LavaPlayerAudioSource;
import org.javacord.api.DiscordApi;
import org.javacord.api.audio.AudioConnection;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.awt.*;

public class OneKissCommandListener implements SlashCommandCreateListener {

    private final DiscordApi api;
    private AudioConnection connection = null;
    private final AudioPlayerManager playerManager;
    private final AudioPlayer player;
    private final AudioSource source;

    public OneKissCommandListener(DiscordApi api) {
        this.api = api;
        playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        player = playerManager.createPlayer();
        source = new LavaPlayerAudioSource(api, player);
        playerManager.loadItem("https://www.youtube.com/watch?v=4B9oklZd5UE", new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                player.playTrack(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (AudioTrack track : playlist.getTracks()) {
                    player.playTrack(track);
                }
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException exception) {

            }
        });
    }

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        var interaction = event.getSlashCommandInteraction();
        var user = interaction.getUser();
        var server = interaction.getServer().orElseThrow();
        if (!"onekiss".equals(interaction.getCommandName())) return;
        if (connection != null) {
            player.setPaused(true);
            connection.close();
            interaction.createImmediateResponder()
                    .setContent("tschusii")
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond().join();
            connection = null;
        } else {
            server.getVoiceChannels().forEach(channel -> {
                if (user.isConnected(channel)) {
                    channel.connect().thenAcceptAsync(connection -> {
                        this.connection = connection;
                        connection.setSelfDeafened(false);
                        connection.setAudioSource(source);
                        player.playTrack(player.getPlayingTrack().makeClone());
                        interaction.createImmediateResponder()
                                .addEmbed(new EmbedBuilder()
                                        .setTitle("One kiss is all it takes!")
                                        .setDescription("**Falling in love with me! :biting_lip: :biting_lip:**")
                                        .setImage("https://media.tenor.com/hL8sPEnLqM0AAAAM/dua-lipa-future-nostalgia-tour.gif")
                                        .setColor(new Color(0x4B84FF)))
                                .setFlags(MessageFlag.EPHEMERAL)
                                .respond().join();
                    });
                }
            });
        }
    }
}
