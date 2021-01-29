package nl.dusmartijngames.bot.NLKingdom.commands.admin;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nl.dusmartijngames.bot.NLKingdom.config.Config;
import nl.dusmartijngames.bot.NLKingdom.objects.CommandContext;
import nl.dusmartijngames.bot.NLKingdom.objects.MessageWaiter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class EmbedCommand extends Command {

    //TODO Dit werkende maken. vragen stellen en dan op reactie wachten.

    private final EventWaiter waiter;
    private final Set<Long> current;
    private final static List<String> CANCEL_WORDS = Arrays.asList("cancel", "annuleer", "!annuleer");

    public EmbedCommand(EventWaiter waiter) {
        this.current = new HashSet<>();
        this.waiter = waiter;
        super.name = "embed";
        super.help = "Maak een embed";
        super.arguments = "[#kanaal]";
        super.category = new Category("admin");

    }

    @Override
    protected void execute(CommandEvent event) {
        List<TextChannel> channels = event.getMessage().getMentionedChannels();
        Member member = event.getMember();
        TextChannel channel = event.getTextChannel();
        EmbedBuilder eb = new EmbedBuilder();
        Role embedRole = event.getGuild().getRoleById(Config.getLong("embedRole"));

        if (member.getRoles().get(0).getPositionRaw() < embedRole.getPositionRaw()) {
            channel.sendMessage("Jij hebt geen permissies om dit commando uit te voeren").queue();
            return;
        }

        if (channels.isEmpty()) {
            channel.sendMessage("Niet genoet argumenten. !embed <#kanaal>").queue();
            return;
        }

        TextChannel targetChannel = channels.get(0);

        channel.sendMessage("Wat moet de titel zijn?").queue();

        waiter.waitForEvent(GuildMessageReceivedEvent.class, e -> e.getAuthor().equals(event.getAuthor()), e -> {
            String title = e.getMessage().getContentRaw();

            channel.sendMessage("oke, Wat wil je als bericht?").queue();

            waiter.waitForEvent(GuildMessageReceivedEvent.class, ev -> ev.getAuthor().equals(event.getAuthor()) && ev.getChannel().equals(channel), ev -> {
                String description = ev.getMessage().getContentRaw();

                eb.setTitle(title)
                        .addField("", description, false);

                targetChannel.sendMessage(eb.build()).queue();
            }, 1, TimeUnit.MINUTES, () -> channel.sendMessage("Niet optijd gereageerd.").queue());
        }, 10, TimeUnit.SECONDS, () -> channel.sendMessage("Niet optijd gereageerd.").queue());
    }

    private void waitForTitle(CommandContext event, Long lastMessage) {
        wait(event, lastMessage, e -> {

                }
                );
    }

    private void wait(CommandContext event, long lastMessage, Consumer<GuildMessageReceivedEvent> action)
    {
        getWaiter(event).waitForGuildMessageReceived(
                e -> e.getAuthor().equals(event.getAuthor()) && e.getChannel().equals(event.getChannel()) && e.getMessageIdLong() != lastMessage,
                e ->
                {
                    // manual cancel
                    if(CANCEL_WORDS.contains(e.getMessage().getContentRaw().toLowerCase()))
                    {

                        current.remove(event.getChannel().getIdLong());
                        return;
                    }
                    // run action
                    action.accept(e);
                }, 2, TimeUnit.MINUTES, new Timeout(event));
    }

    private MessageWaiter getWaiter(CommandContext event)
    {
        for(Object ev: event.getJDA().getEventManager().getRegisteredListeners())
            if(ev instanceof MessageWaiter)
                return (MessageWaiter) ev;
        return null;
    }

    private class Timeout implements Runnable
    {
        private final CommandContext event;
        private boolean ran = false;

        private Timeout(CommandContext event)
        {
            this.event = event;
        }

        @Override
        public void run()
        {
            if(ran)
                return;
            ran = true;
            event.getEvent().getChannel().sendMessage("Uh oh! You took longer than 2 minutes to respond, "+event.getAuthor().getAsMention()+"!").queue();
            current.remove(event.getChannel().getIdLong());
        }
    }
}
