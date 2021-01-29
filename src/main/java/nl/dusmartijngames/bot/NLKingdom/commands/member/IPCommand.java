package nl.dusmartijngames.bot.NLKingdom.commands.member;

import net.dv8tion.jda.api.entities.TextChannel;
import nl.dusmartijngames.bot.NLKingdom.config.Config;
import nl.dusmartijngames.bot.NLKingdom.objects.CommandContext;
import nl.dusmartijngames.bot.NLKingdom.objects.ICommand;

public class IPCommand implements ICommand {
    @Override
    public void handle(CommandContext event) {
        TextChannel channel = event.getChannel();

        channel.sendMessage("Het server ip is: " + Config.get("ip")).queue();
    }

    @Override
    public String getName() {
        return "ip";
    }

    @Override
    public String getHelp(CommandContext event) {
        return "shows the ip of the server";
    }

    @Override
    public String getCat() {
        return "member";
    }
}
