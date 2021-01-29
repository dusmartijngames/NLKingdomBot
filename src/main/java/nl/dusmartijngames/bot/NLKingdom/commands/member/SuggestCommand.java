package nl.dusmartijngames.bot.NLKingdom.commands.member;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.dusmartijngames.bot.NLKingdom.config.Config;
import nl.dusmartijngames.bot.NLKingdom.objects.CommandContext;
import nl.dusmartijngames.bot.NLKingdom.objects.ICommand;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class SuggestCommand implements ICommand {
    @Override
    public void handle(CommandContext event) {
        Member member = event.getMember();
        TextChannel suggestChannel = event.getGuild().getTextChannelById(Config.getLong("suggestChannel"));
        Message message = event.getMessage();
        EmbedBuilder eb = new EmbedBuilder();
        List<String> args = event.getArgs();
        TextChannel channel = event.getChannel();

        if (args.isEmpty()) {
            channel.sendMessage("Ik mis een suggestie. !suggest <suggestie>").queue();
            return;
        }

        String join = String.join(" ", args);

        eb.addField("Verstuurd door:", member.getEffectiveName(), false)
                .addField("Suggestie:", join, false)
                .setThumbnail(member.getUser().getEffectiveAvatarUrl())
                .setColor(member.getColor())
                .setTimestamp(LocalDateTime.now().atZone(ZoneId.systemDefault()));

        suggestChannel.sendMessage(eb.build()).queue(m -> {
            m.addReaction("✅").queue();
            m.addReaction("❎").queue();
        });

        message.delete().queue();

    }

    @Override
    public String getName() {
        return "suggest";
    }

    @Override
    public String getHelp(CommandContext event) {
        return "Gebuik !suggest <suggestie> om een suggestie ";
    }

    @Override
    public String getCat() {
        return "member";
    }
}
