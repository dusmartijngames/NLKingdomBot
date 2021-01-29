package nl.dusmartijngames.bot.NLKingdom.commands.member;

import net.dv8tion.jda.api.entities.TextChannel;
import nl.dusmartijngames.bot.NLKingdom.config.Config;
import nl.dusmartijngames.bot.NLKingdom.objects.CommandContext;
import nl.dusmartijngames.bot.NLKingdom.objects.ICommand;

import java.util.Arrays;
import java.util.List;

public class ShopCommand implements ICommand {
    @Override
    public void handle(CommandContext event) {
        TextChannel channel = event.getChannel();

        channel.sendMessage(Config.get("webshop")).queue();
    }

    @Override
    public String getName() {
        return "store";
    }

    @Override
    public String getHelp(CommandContext event) {
        return "Stuurt de link naar onze webstore";
    }

    @Override
    public String getCat() {
        return "member";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("webshop");
    }
}
