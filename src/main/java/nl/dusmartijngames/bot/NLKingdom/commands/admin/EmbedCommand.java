package nl.dusmartijngames.bot.NLKingdom.commands.admin;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nl.dusmartijngames.bot.NLKingdom.config.Config;
import nl.dusmartijngames.bot.NLKingdom.handlers.EmbedHandler;
import nl.dusmartijngames.bot.NLKingdom.objects.CommandContext;
import nl.dusmartijngames.bot.NLKingdom.objects.ICommand;
import nl.dusmartijngames.bot.NLKingdom.objects.MessageWaiter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class EmbedCommand implements ICommand {

    //TODO Dit werkende maken. vragen stellen en dan op reactie wachten.

    private final Set<Long> current;
    private final static List<String> CANCEL_WORDS = Arrays.asList("cancel", "annuleer", "!annuleer");

    public EmbedCommand() {
        this.current = new HashSet<>();
    }

    @Override
    public void handle(CommandContext event) {
        List<TextChannel> channels = event.getMessage().getMentionedChannels();
        Member member = event.getMember();
        TextChannel channel = event.getChannel();
        EmbedBuilder eb = new EmbedBuilder();
        Role embedRole = event.getGuild().getRoleById(Config.getLong("embedRole"));

        if (member.getRoles().get(0).getPositionRaw() < embedRole.getPositionRaw()) {
            channel.sendMessage("Jij hebt geen permissies om dit commando uit te voeren").queue();
            return;
        }

        if (channels.isEmpty()) {
            channel.sendMessage("Niet genoet argumenten. *embed <#kanaal>").queue();
            return;
        }

        TextChannel targetChannel = channels.get(0);

        channel.sendMessage("Wat moet de titel zijn?").queue();

        event.getJDA().addEventListener(new EmbedHandler(event.getChannel(), event.getAuthor(), targetChannel, -1));

    }

    @Override
    public String getName() {
        return "embed";
    }

    @Override
    public String getHelp(CommandContext event) {
        return "start het maken van een embed";
    }

    @Override
    public String getCat() {
        return "admin";
    }

}
