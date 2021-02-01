package nl.dusmartijngames.bot.NLKingdom.handlers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

public class EmbedHandler extends ListenerAdapter {
    private final long channelId;
    private final long authorId;
    private final long targetChannelId;
    private long lastMessageId;
    private String kop;
    private EmbedBuilder eb = new EmbedBuilder().setFooter("© NLKingdom | Reborn");
    private int stage;


    public EmbedHandler(MessageChannel channel, User user, MessageChannel targetChannel, int stage) {
        this.channelId = channel.getIdLong();
        this.authorId = user.getIdLong();
        this.targetChannelId = targetChannel.getIdLong();
        this.stage = stage;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            if (stage == -1) {
                this.stage = 0;
            }
            return; // don't respond to other bots
        }
        if (event.getChannel().getIdLong() != channelId) return; // ignore other channels
        MessageChannel channel = event.getChannel();
        String content = event.getMessage().getContentRaw();

        // check for canceling of command
        if (content.toLowerCase(Locale.ROOT).equals("stop")) {
            channel.sendMessage("Understood!").queue();
            event.getJDA().removeEventListener(this);
        }
        //check if message author is the same as author of the command
        else if (event.getAuthor().getIdLong() == authorId) {
            switch (stage) {
                case 0:
                    this.eb.setTitle(event.getMessage().getContentRaw());
                    this.lastMessageId = event.getMessage().getIdLong();
                    this.stage = 1;
                    channel.sendMessage("Wat wil je als kop?").queue();
                    break;
                case 1:
                    this.kop = event.getMessage().getContentRaw();
                    this.stage = 2;
                    channel.sendMessage("Wat wil je als tekst onder dit kopje?").queue();
                    break;
                case 2:
                    this.eb.addField(kop, event.getMessage().getContentRaw(), false
                    );
                    this.stage = 3;
                    channel.sendMessage("Wil je nog meer toevoegen? ja? zeg dan doorgaan, nee? zeg dan klaar.").queue();
                    break;
                case 3:
                    if (event.getMessage().getContentRaw().equalsIgnoreCase("klaar")) {
                        this.eb.setTimestamp(LocalDateTime.now().atZone(ZoneId.systemDefault()))
                                .setColor(Color.decode("#3498DB"))
                                .setThumbnail(event.getGuild().getIconUrl());
                        event.getGuild().getTextChannelById(this.targetChannelId).sendMessage(this.eb.build()).queue();
                        event.getJDA().removeEventListener(this);
                        return;
                    } else if (event.getMessage().getContentRaw().equalsIgnoreCase("doorgaan")){
                        this.stage = 1;
                        channel.sendMessage("Wat wil je als kop?").queue();
                    } else {
                        channel.sendMessage("Wil je nog meer toevoegen? ja? zeg dan doorgaan, nee? zeg dan klaar.").queue();
                    }
                    break;
            }

        } else {
            return;
        }
    }
}
