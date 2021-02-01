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

import java.util.List;

public class RenameTicketCommand implements ICommand {
    @Override
    public void handle(CommandContext event) {
        Member member = event.getMember();
        TextChannel channel = event.getChannel();
        ChannelManager manager = channel.getManager();
        Member selfMember = event.getGuild().getSelfMember();
        Role supportRole = event.getGuild().getRoleById(Config.getLong("supportRoleId"));
        List<String> args = event.getArgs();

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

        if (args.isEmpty()) {
            channel.sendMessage("Geef een naam op. gebruik *rename <nieuwe naam>").queue();
            return;
        }

        String name = String.join(" ", args);
        name = name.replace(" ", "-");

        manager.setName(name).queue();
        channel.sendMessage("Naam van dit ticket is gezet naar " + name ).queue();
    }

    @Override
    public String getName() {
        return "rename";
    }

    @Override
    public String getHelp(CommandContext event) {
        return "";
    }

    @Override
    public String getCat() {
        return "support";
    }
}
