package nl.dusmartijngames.bot.NLKingdom.commands.support;

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

        if(!member.getRoles().contains(supportRole)) {
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

        if (mentionedMembers.isEmpty()) {
            channel.sendMessage("Je hebt geen gebruiker genoemt. gebruik !remove <@gebruiker>").queue();
            return;
        }

        Member removeMember = mentionedMembers.get(0);

        manager.removePermissionOverride(removeMember).queue();
        channel.sendMessage(removeMember.getEffectiveName() + " Is van dit support ticket verwijderd.").queue();
    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getHelp(CommandContext event) {
        return "Verwijder een gebruiker uit het huidige ticket.";
    }

    @Override
    public String getCat() {
        return "support";
    }
}
