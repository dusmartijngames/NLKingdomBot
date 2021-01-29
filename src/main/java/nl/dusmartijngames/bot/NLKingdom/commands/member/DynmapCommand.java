package nl.dusmartijngames.bot.NLKingdom.commands.member;

import nl.dusmartijngames.bot.NLKingdom.config.Config;
import nl.dusmartijngames.bot.NLKingdom.objects.CommandContext;
import nl.dusmartijngames.bot.NLKingdom.objects.ICommand;

public class DynmapCommand implements ICommand {
    @Override
    public void handle(CommandContext event) {
        event.getChannel().sendMessage(Config.get("dynmap")).queue();
    }

    @Override
    public String getName() {
        return "dynmap";
    }

    @Override
    public String getHelp(CommandContext event) {
        return "Stuurt de link naar onze Dynmap";
    }

    @Override
    public String getCat() {
        return "member";
    }
}
