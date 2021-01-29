package nl.dusmartijngames.bot.NLKingdom.commands.support;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.dusmartijngames.bot.NLKingdom.config.Config;
import nl.dusmartijngames.bot.NLKingdom.database.DatabaseManager;
import nl.dusmartijngames.bot.NLKingdom.objects.CommandContext;
import nl.dusmartijngames.bot.NLKingdom.objects.ICommand;

public class SupportEmbedCommand implements ICommand {
    @Override
    public void handle(CommandContext event) {
        TextChannel channel = event.getChannel();
        Member member = event.getMember();
        Role supembrole = event.getGuild().getRoleById(Config.getLong("supembrole"));

        if (member.getRoles().get(0).getPositionRaw() < supembrole.getPositionRaw()) {
            channel.sendMessage("Je hebt geen rechten om dit commando uit te voeren.").queue();
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        Emote emote = event.getJDA().getEmoteById(Config.getLong("emoteid"));

        eb.setTitle("Een ticket aanmaken!")
                .addField("", "Klik op de emoji onder dit bericht om een ticket aan te maken.", false);

        channel.sendMessage(eb.build()).queue(m -> {
            m.addReaction(emote).queue();
            DatabaseManager.INSTANCE.setSupportMessage(event.getGuild().getIdLong(), m.getIdLong());
        });

    }

    @Override
    public String getName() {
        return "supportmessage";
    }

    @Override
    public String getHelp(CommandContext event) {
        return "Stuurt het support embed in het kanaal waar je dit commando uitvoert.";
    }

    @Override
    public String getCat() {
        return "support";
    }
}
