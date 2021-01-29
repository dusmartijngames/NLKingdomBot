package nl.dusmartijngames.bot.NLKingdom.handlers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import nl.dusmartijngames.bot.NLKingdom.database.DatabaseManager;
import nl.dusmartijngames.bot.NLKingdom.objects.CommandContext;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class RoleHandler extends ListenerAdapter {

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if (event.getUser().isBot()) {
            return;
        }

        long emoteId = event.getReactionEmote().getIdLong();
        Emote emote = event.getGuild().getEmoteById(emoteId);

        if (emote == null) {
            return;
        }

        event.getGuild().getTextChannelById(event.getChannel().getIdLong()).removeReactionById(event.getMessageId(), emote, event.getUser()).queue();

        long roleChannelId = DatabaseManager.INSTANCE.getRoleChannel(event.getGuild().getIdLong());
        TextChannel roleChannel = event.getGuild().getTextChannelById(roleChannelId);

        if (roleChannel == null) {
            return;
        }

        if (event.getChannel() != roleChannel) {
            return;
        }

        long roleId = DatabaseManager.INSTANCE.getRoleFromEmote(event.getGuild().getIdLong(), emoteId);
        Role role = event.getGuild().getRoleById(roleId);

        if (role == null) {
            return;
        }

        if (event.getMember().getRoles().contains(role)) {
            event.getGuild().removeRoleFromMember(event.getMember(), role).queue();
        } else {
            event.getGuild().addRoleToMember(event.getMember(), role).queue();
        }

    }

    public static void updateMessage(CommandContext event) {
        long roleChannelId = DatabaseManager.INSTANCE.getRoleChannel(event.getGuild().getIdLong());
        long roleMessageId = DatabaseManager.INSTANCE.getRoleMessage(event.getGuild().getIdLong());
        TextChannel roleChannel = event.getGuild().getTextChannelById(roleChannelId);
        List<Long> roles = DatabaseManager.INSTANCE.getRoles(event.getGuild().getIdLong());

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Keuze Rollen")
                .setTimestamp(LocalDateTime.now().atZone(ZoneId.systemDefault()))
                .setColor(Color.decode("#0083DA"))
                .setThumbnail(event.getGuild().getIconUrl());

        for (long roleId : roles) {
            Role role = event.getGuild().getRoleById(roleId);
            long emoteId = DatabaseManager.INSTANCE.getEmote(event.getGuild().getIdLong(), roleId);
            Emote emote = event.getGuild().getEmoteById(emoteId);
            eb.addField("", emote.getAsMention() + " - " + role.getName(), false);
        }

        roleChannel.retrieveMessageById(roleMessageId).queue(message -> {
            message.editMessage(eb.build()).queue(m -> {
                m.clearReactions().queue();
                for (long roleId : roles) {
                    long emoteId = DatabaseManager.INSTANCE.getEmote(event.getGuild().getIdLong(), roleId);
                    Emote emote = event.getGuild().getEmoteById(emoteId);
                    m.addReaction(emote).queue();
            }});
        });
    }
}
