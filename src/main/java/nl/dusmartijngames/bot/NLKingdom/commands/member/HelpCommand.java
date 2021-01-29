package nl.dusmartijngames.bot.NLKingdom.commands.member;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.dusmartijngames.bot.NLKingdom.CommandManager;
import nl.dusmartijngames.bot.NLKingdom.Constants;
import nl.dusmartijngames.bot.NLKingdom.objects.CommandContext;
import nl.dusmartijngames.bot.NLKingdom.objects.ICommand;


import java.awt.*;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class HelpCommand implements ICommand {

    private final CommandManager manager;

    public HelpCommand(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(CommandContext ctx) {
        List<String> args = ctx.getArgs();
        TextChannel channel = ctx.getChannel();
        EmbedBuilder builder = new EmbedBuilder();
        String prefix = Constants.PREFIX;

        if (args.isEmpty()) {
            builder.setTitle("Help command").setColor(Color.decode("#2B7FA8"))
                    .setThumbnail(ctx.getGuild().getSelfMember().getUser().getEffectiveAvatarUrl())
                    .setTimestamp(LocalDateTime.now(Clock.systemUTC()))
                    .setFooter(ctx.getMember().getEffectiveName() + " did " + prefix + "help at", ctx.getMember().getUser().getEffectiveAvatarUrl())
                    .addField("Categories:", "Admin, Mod, Member, Music, Other", true);

            ICommand help = manager.getCommand("help");
            builder.addField(prefix + help.getName(), help.getHelp(ctx), false);
            channel.sendMessage(builder.build()).queue();
            return;
        }

        String category = args.get(0).toLowerCase();

        builder.setTitle("Commands in the " + category + " category").setColor(Color.decode("#2B7FA8"))
                .setThumbnail(ctx.getGuild().getSelfMember().getUser().getEffectiveAvatarUrl())
                .setTimestamp(LocalDateTime.now(Clock.systemUTC()))
                .setFooter(ctx.getMember().getEffectiveName() + " did " + prefix + "help at", ctx.getMember().getUser().getEffectiveAvatarUrl());

        manager.getCommandsCat(category).forEach((it) ->
                builder.addField(prefix + it.getName(), it.getHelp(ctx), false));

        channel.sendMessage(builder.build()).queue();
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getHelp(CommandContext event) {
        String getPrefix = Constants.PREFIX;
        return "Shows the list with commands in the bot\n" +
                "Usage: " + getPrefix + getName() + " [category]";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("commands", "cmds", "commandlist");
    }

    @Override
    public String getCat() {
        return "other";
    }
}