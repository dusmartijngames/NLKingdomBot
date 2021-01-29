package nl.dusmartijngames.bot.NLKingdom.commands.support;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.exceptions.MissingAccessException;
import nl.dusmartijngames.bot.NLKingdom.config.Config;
import nl.dusmartijngames.bot.NLKingdom.handlers.SupportHandler;
import nl.dusmartijngames.bot.NLKingdom.objects.CommandContext;
import nl.dusmartijngames.bot.NLKingdom.objects.ICommand;


public class NewTicketCommand implements ICommand {

    @Override
    public void handle(CommandContext event) {
        Category category = event.getGuild().getCategoryById(Config.getLong("catId"));
        Member selfMember = event.getGuild().getSelfMember();

        if (!selfMember.hasPermission(Permission.MANAGE_CHANNEL)) {
            event.getChannel().sendMessage( new MissingAccessException(category, Permission.MANAGE_CHANNEL).getMessage()).queue();
            throw new MissingAccessException(category, Permission.MANAGE_CHANNEL);
        }

        SupportHandler.createTicket(event);

    }

    @Override
    public String getName() {
        return "new";
    }

    @Override
    public String getHelp(CommandContext event) {
        return "maakt een nieuw support ticket aan.";
    }

    @Override
    public String getCat() {
        return "support";
    }
}
