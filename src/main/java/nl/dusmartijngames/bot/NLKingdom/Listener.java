package nl.dusmartijngames.bot.NLKingdom;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import nl.dusmartijngames.bot.NLKingdom.database.DatabaseManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Listener extends ListenerAdapter {


    private final CommandManager manager;
    Listener(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void onReady(ReadyEvent event) {

       System.out.printf("%s Logged in as %#s\n", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) ,event.getJDA().getSelfUser());

       event.getJDA().getGuildById(612189253824151552L).getTextChannelById(771454723353870418L).sendMessageFormat("%s Started.",
               event.getJDA().getSelfUser().getName()).queue();
        DatabaseManager.INSTANCE.init();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        Guild guild = event.getGuild();
        String prefix = Prefix.PREFIXES.computeIfAbsent(guild.getIdLong(), DatabaseManager.INSTANCE::getPrefix);

            if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "shutdown") &&
                    event.getAuthor().getIdLong() == Constants.OWNER
            ) {
                event.getJDA().getGuildById(612189253824151552L).getTextChannelById(771454723353870418L).sendMessageFormat("%s is shutting down", event.getJDA().getSelfUser().getName()).queue();
                event.getChannel().sendMessage("Shutting down.").queue();
                shutdown(event.getJDA());
                return;
            }

            try {
                if (!event.getAuthor().isBot() && !event.getMessage().isWebhookMessage() &&
                        event.getMessage().getContentRaw().startsWith(prefix)) {
                    manager.handle(event, prefix);
                }
            } catch (NullPointerException e) {

            }
    }

    private void shutdown(JDA jda) {
        System.out.print("stop\n");
        System.out.print(LocalDateTime.now() + " Shutting down\n");
        jda.shutdown();
        System.exit(0);
    }
}