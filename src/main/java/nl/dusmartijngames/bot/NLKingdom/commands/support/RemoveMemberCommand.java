package nl.dusmartijngames.bot.NLKingdom.commands.support;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.MissingAccessException;
import net.dv8tion.jda.api.managers.ChannelManager;
import nl.dusmartijngames.bot.NLKingdom.config.Config;
import nl.dusmartijngames.bot.NLKingdom.database.DatabaseManager;
import nl.dusmartijngames.bot.NLKingdom.objects.CommandContext;
import nl.dusmartijngames.bot.NLKingdom.objects.ICommand;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;

public class RemoveMemberCommand implements ICommand {
    @Override
    public void handle(CommandContext event) {
        Member member = event.getMember();
        TextChannel channel = event.getChannel();
        ChannelManager manager = channel.getManager();
        Member selfMember = event.getGuild().getSelfMember();
        Role supportRole = event.getGuild().getRoleById(Config.getLong("supportRoleId"));
        List<Member> mentionedMembers = event.getMessage().getMentionedMembers();
        List<Role> roles = event.getMessage().getMentionedRoles();

        if(member.getRoles().get(0).getPositionRaw() < supportRole.getPositionRaw()) {
            channel.sendMessage("Je mag dit commando niet uitvoeren").queue();
            return;
        }

        if(!DatabaseManager.INSTANCE.isTicket(event.getGuild().getIdLong(), channel.getIdLong())) {
            channel.sendMessage("Dit commando kan alleen worden uitgevoerd in een support ticket").queue();
            return;
        }

        if (!selfMember.hasPermission(Permission.MANAGE_CHANNEL)) {
            event.getChannel().sendMessage( new MissingAccessException(channel, Permission.MANAGE_CHANNEL).getMessage()).queue();
            throw new MissingAccessException(channel, Permission.MANAGE_CHANNEL);
        }

        if (mentionedMembers.isEmpty() && roles.isEmpty()) {
            channel.sendMessage("Je hebt geen gebruiker of role genoemt. gebruik *add <@gebruiker>").queue();
            return;
        }

        EnumSet<Permission> allow = EnumSet.noneOf(Permission.class);
        EnumSet<Permission> deny = EnumSet.allOf(Permission.class);
        Role removeRole;
        Member removeMember;
        event.getMessage().delete().queue();
        EmbedBuilder eb = new EmbedBuilder().setTimestamp(LocalDateTime.now().atZone(ZoneId.systemDefault()))
                .setColor(Color.decode("#3498DB"))
                .setFooter("© NLKingdom | Reborn")
                .setThumbnail(event.getGuild().getIconUrl());

        if (!roles.isEmpty()) {
            removeRole = roles.get(0);;
            manager.putPermissionOverride(removeRole, allow, deny).queue();
            eb.setTitle("Rol verwijderd").addField("", "Rol " + removeRole.getName() + " is verwijderd van dit support ticket", false);
            channel.sendMessage(eb.build()).queue();
        } else {
            removeMember = mentionedMembers.get(0);


            manager.putPermissionOverride(removeMember, allow, deny).queue();
            eb.setTitle("Speler verwijderd").addField("", "Speler " + removeMember.getEffectiveName() + " is verwijderd van dit support ticket", false);
            channel.sendMessage(eb.build()).queue();
        }
    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getHelp(CommandContext event) {
        return "Verwijder een gebruiker of rol uit het huidige ticket.";
    }

    @Override
    public String getCat() {
        return "support";
    }
}
