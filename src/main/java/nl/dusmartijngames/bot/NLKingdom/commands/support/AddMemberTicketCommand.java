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

public class AddMemberTicketCommand implements ICommand {
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

        EnumSet<Permission> allow = EnumSet.of(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION);
        EnumSet<Permission> deny = EnumSet.of(Permission.MANAGE_CHANNEL);
        Role addRole;
        Member addMember;

        if (!roles.isEmpty()) {
            addRole = roles.get(0);;
            manager.putPermissionOverride(addRole, allow, deny).queue();
            channel.sendMessage("Role " + addRole.getName() + " Is toegevoegd aan dit support ticket").queue();
        } else {
            addMember = mentionedMembers.get(0);


            manager.putPermissionOverride(addMember, allow, deny).queue();
            channel.sendMessage(addMember.getEffectiveName() + " Is toegevoegd aan dit support ticket").queue();
        }
    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getHelp(CommandContext event) {
        return "voegt een member toe aan het huidige ticket";
    }

    @Override
    public String getCat() {
        return "support";
    }
}
