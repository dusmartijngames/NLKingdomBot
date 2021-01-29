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

public class GetTranscriptCommand implements ICommand {
    @Override
    public void handle(CommandContext event) {
        Member member = event.getMember();
        TextChannel channel = event.getChannel();
        Role supportRole = event.getGuild().getRoleById(Config.getLong("supportRoleId"));
        List<String> args = event.getArgs();

        if(member.getRoles().get(0).getPositionRaw() < supportRole.getPositionRaw()) {
            channel.sendMessage("Je mag dit commando niet uitvoeren").queue();
            return;
        }

        if (args.isEmpty()) {
            channel.sendMessage("Niet genoeg argumenten. gebruik !gettranscript <id>").queue();
            return;
        }

        String idString = String.join("", args);
        int id = Integer.parseInt(idString);

        channel.sendMessage(DatabaseManager.INSTANCE.getTranscript(id)).queue();
    }

    @Override
    public String getName() {
        return "gettranscript";
    }

    @Override
    public String getHelp(CommandContext event) {
        return "stuurt een link naar de transcript file van een ticket.";
    }

    @Override
    public String getCat() {
        return "support";
    }
}
