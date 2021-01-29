package nl.dusmartijngames.bot.NLKingdom.commands.member;

import nl.dusmartijngames.bot.NLKingdom.config.Config;
import nl.dusmartijngames.bot.NLKingdom.objects.CommandContext;
import nl.dusmartijngames.bot.NLKingdom.objects.ICommand;

import java.util.Arrays;
import java.util.List;

public class InviteCommand implements ICommand {
    @Override
    public void handle(CommandContext event) {
        event.getChannel().sendMessage(Config.get("invite")).queue();
    }

    @Override
    public String getName() {
        return "invite";
    }

    @Override
    public String getHelp(CommandContext event) {
        return "Stuurt de invite link voor onze discord server";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("discord");
    }

    @Override
    public String getCat() {
        return "member";
    }
}
