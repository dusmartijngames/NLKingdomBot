package nl.dusmartijngames.bot.NLKingdom.commands.support;

import net.dv8tion.jda.api.entities.*;
import nl.dusmartijngames.bot.NLKingdom.config.Config;
import nl.dusmartijngames.bot.NLKingdom.database.DatabaseManager;
import nl.dusmartijngames.bot.NLKingdom.objects.CommandContext;
import nl.dusmartijngames.bot.NLKingdom.objects.ICommand;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CloseTicketCommand implements ICommand {
    @Override
    public void handle(CommandContext event) {
        TextChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        Member member = event.getMember();
        TextChannel logChannel = guild.getTextChannelById(Config.getLong("logChannel"));

        if (!DatabaseManager.INSTANCE.isTicket(guild.getIdLong(), channel.getIdLong())) {
            channel.sendMessage("Het uitgevoerde command kan alleen in een ticket worden uitgevoerd").queue();
            return;
        }

        long ownerId = DatabaseManager.INSTANCE.getSupportOwner(guild.getIdLong(), channel.getIdLong());
        int id = DatabaseManager.INSTANCE.getTicketId(guild.getIdLong(), channel.getIdLong());
        Member ticketOwner = event.getGuild().getMemberById(ownerId);

        channel.getManager().sync().queue();
        final MessageHistory history = channel.getHistoryFromBeginning(100).complete();

        try {
            File newfile = new File("transcripts/" + id + "-" + ticketOwner.getEffectiveName() + ".txt");
            newfile.createNewFile();
            newfile.setWritable(true);
            FileWriter transcript = new FileWriter(newfile);

            for (Message message : history.getRetrievedHistory()) {
                transcript.write(message.getMember().getEffectiveName() + ": " + message.getContentRaw() + "\n");
            }
            transcript.close();


            logChannel.sendMessage("Ticket " + channel.getName() + " has been closed by: " + member.getEffectiveName()).addFile(newfile).queue(l -> {
                String url = l.getAttachments().get(0).getUrl();
                DatabaseManager.INSTANCE.setTicketActive(guild.getIdLong(), channel.getIdLong(), false, url);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "close";
    }

    @Override
    public String getHelp(CommandContext event) {
        return "sluit het huidige Support ticket.";
    }

    @Override
    public String getCat() {
        return "support";
    }
}
