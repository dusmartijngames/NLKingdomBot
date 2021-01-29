package nl.dusmartijngames.bot.NLKingdom.commands.member;

import nl.dusmartijngames.bot.NLKingdom.config.Config;
import nl.dusmartijngames.bot.NLKingdom.objects.CommandContext;
import nl.dusmartijngames.bot.NLKingdom.objects.ICommand;

public class WebsiteCommand implements ICommand {
    @Override
    public void handle(CommandContext event) {
        event.getChannel().sendMessage(Config.get("wensite")).queue();
    }

    @Override
    public String getName() {
        return "website";
    }

    @Override
    public String getHelp(CommandContext event) {
        return "Stuurt de link naar onze website";
    }

    @Override
    public String getCat() {
        return "member";
    }
}
