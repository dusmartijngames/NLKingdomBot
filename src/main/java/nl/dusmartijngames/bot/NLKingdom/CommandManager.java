package nl.dusmartijngames.bot.NLKingdom;


import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nl.dusmartijngames.bot.NLKingdom.commands.admin.EmbedCommand;
import nl.dusmartijngames.bot.NLKingdom.commands.admin.RoleChannelCommand;
import nl.dusmartijngames.bot.NLKingdom.commands.admin.SendRolesCommand;
import nl.dusmartijngames.bot.NLKingdom.commands.admin.SetRolesCommand;
import nl.dusmartijngames.bot.NLKingdom.commands.member.*;
import nl.dusmartijngames.bot.NLKingdom.commands.support.*;
import nl.dusmartijngames.bot.NLKingdom.objects.CommandContext;
import nl.dusmartijngames.bot.NLKingdom.objects.ICommand;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class CommandManager {

    private final List<ICommand> commands = new ArrayList<>();
    EventWaiter waiter = new EventWaiter();

    CommandManager() {

        //organize in alphabetical order please!
        //Member commands
        addCommand(new DynmapCommand());
        addCommand(new HelpCommand(this));
        addCommand(new InviteCommand());
        addCommand(new IPCommand());
        addCommand(new SuggestCommand());
        addCommand(new ShopCommand());
        addCommand(new WebsiteCommand());

        //moderator commands

        //admin commands
        addCommand(new EmbedCommand());
        addCommand(new RoleChannelCommand());
        addCommand(new SendRolesCommand());
        addCommand(new SetRolesCommand());

        //support commands
        addCommand(new AddMemberTicketCommand());
        addCommand(new CloseTicketCommand());
        addCommand(new GetTranscriptCommand());
        addCommand(new NewTicketCommand());
        addCommand(new RemoveMemberCommand());
        addCommand(new RenameTicketCommand());
        addCommand(new SupportEmbedCommand());

    }


    private void addCommand(ICommand cmd) {
        boolean nameFound = this.commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));

        if (nameFound) {
            throw new IllegalArgumentException("A command with this name is already present");
        }

        commands.add(cmd);
    }

    public List<ICommand> getCommands() {
        return commands;
    }

    @Nullable
    public ICommand getCommand(String search) {
        String searchLower = search.toLowerCase();

        for (ICommand cmd : this.commands) {
            if (cmd.getName().equals(searchLower) || cmd.getAliases().contains(searchLower)) {
                return cmd;
            }
        }

        return null;
    }

    public List<ICommand> getCommandsCat(String category) {

        List<ICommand> commandlist = new ArrayList<>(Collections.emptyList());
        for (ICommand command : getCommands()) {
            if (command.getCat().equals(category)) {
                commandlist.add(command);
            }
        }

        return commandlist;
    }

    void handle(GuildMessageReceivedEvent event, String prefix) {
        String[] split = event.getMessage().getContentRaw()
                .replaceFirst("(?i)" + Pattern.quote(prefix), "")
                .split("\\s+");

        String invoke = split[0].toLowerCase();
        ICommand cmd = this.getCommand(invoke);

        if (cmd != null) {
            event.getChannel().sendTyping().queue();
            List<String> args = Arrays.asList(split).subList(1, split.length);

            CommandContext ctx = new CommandContext(event, args);

            cmd.handle(ctx);
        }
    }
}
