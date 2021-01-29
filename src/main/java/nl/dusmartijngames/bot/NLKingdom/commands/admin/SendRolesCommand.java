package nl.dusmartijngames.bot.NLKingdom.commands.admin;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.dusmartijngames.bot.NLKingdom.config.Config;
import nl.dusmartijngames.bot.NLKingdom.database.DatabaseManager;
import nl.dusmartijngames.bot.NLKingdom.objects.CommandContext;
import nl.dusmartijngames.bot.NLKingdom.objects.ICommand;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;

public class SendRolesCommand implements ICommand {
    @Override
    public void handle(CommandContext event) {
        TextChannel channel = event.getChannel();
        Member member = event.getMember();
        Member selfMember = event.getGuild().getSelfMember();
        Role rolesRole = event.getGuild().getRoleById(Config.getLong("RolesRole"));
        EmbedBuilder eb = new EmbedBuilder();

        if (member.getRoles().get(0).getPositionRaw() < rolesRole.getPositionRaw()) {
            channel.sendMessage("Je mag dit commando niet uitvoeren").queue();
            return;
        }

        if (!selfMember.hasPermission(Permission.MANAGE_ROLES)) {
            channel.sendMessage("Voor deze functie heb ik de Manage Roles permissie nodig.").queue();
            return;
        }

        long roleChannelId = DatabaseManager.INSTANCE.getRoleChannel(event.getGuild().getIdLong());

        if (roleChannelId == 0) {
            channel.sendMessage("Het roles kanaal is nog niet ingesteld. gebruik eerst !src <#kanaal> en probeer het dan opnieuw").queue();
            return;
        }

        TextChannel roleChannel = event.getGuild().getTextChannelById(roleChannelId);

        List<Long> roles = DatabaseManager.INSTANCE.getRoles(event.getGuild().getIdLong());

        if (roles.isEmpty()) {
            channel.sendMessage("Geen rollen gevonden. gebruik !setroles add <@role> <:emote:> om een rol toe te voegen").queue();
            return;
        }

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

        roleChannel.sendMessage(eb.build()).queue(m -> {
            for (long roleId : roles) {
                long emoteId = DatabaseManager.INSTANCE.getEmote(event.getGuild().getIdLong(), roleId);
                Emote emote = event.getGuild().getEmoteById(emoteId);
                m.addReaction(emote).queue();
            }
            DatabaseManager.INSTANCE.setRoleMessage(event.getGuild().getIdLong(), m.getIdLong());
        }
        );
    }

    @Override
    public String getName() {
        return "sendroles";
    }

    @Override
    public String getHelp(CommandContext event) {
        return "null";
    }

    @Override
    public String getCat() {
        return "admin";
    }
}
