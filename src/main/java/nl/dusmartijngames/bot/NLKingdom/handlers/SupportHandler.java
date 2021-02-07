package nl.dusmartijngames.bot.NLKingdom.handlers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import nl.dusmartijngames.bot.NLKingdom.config.Config;
import nl.dusmartijngames.bot.NLKingdom.database.DatabaseManager;
import nl.dusmartijngames.bot.NLKingdom.objects.CommandContext;

import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

public class SupportHandler extends ListenerAdapter {

    public static void createTicket(CommandContext event) {
        Member member = event.getMember();
        Category category = event.getGuild().getCategoryById(Config.getLong("catId"));
        Role supportRole = event.getGuild().getRoleById(Config.getLong("supportRoleId"));

        EnumSet<Permission> allow = EnumSet.of(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION);
        EnumSet<Permission> deny = EnumSet.of(Permission.MANAGE_CHANNEL);
        category.createTextChannel("Ticket " + member.getEffectiveName()).addMemberPermissionOverride(member.getIdLong(), allow, deny).queue(c -> {
            DatabaseManager.INSTANCE.setSupportTicket(event.getGuild().getIdLong(), member.getIdLong(), c.getIdLong());
            EmbedBuilder eb = new EmbedBuilder().setTitle("Welkom " + member.getEffectiveName())
                    .addField("", "Bedankt voor het openen van een support ticket. Over enkele momenten zou er iemand van ons " + supportRole.getAsMention() + " team jou te hulp staan\n" +
                            "Typ alvast je vraag uit want dat scheelt best wat tijd", false);
            c.sendMessage(eb.build()).queue();

            c.sendMessage(supportRole.getAsMention()).queue(m -> {
                m.delete().queueAfter(50, TimeUnit.MILLISECONDS);
            });

            int id = DatabaseManager.INSTANCE.getTicketId(event.getGuild().getIdLong(), c.getIdLong());

            String name = id + "-" + member.getEffectiveName();
            name = name.replace(" ", "-");
            c.getManager().setName(name).queue();
        });
    }

    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if (event.getMember() == null) {
            return;
        }

        if (event.getUser().isBot()) {
            return;
        }

        Message message = event.retrieveMessage().complete();
        long supportMessage = DatabaseManager.INSTANCE.getSupportMessage(event.getGuild().getIdLong());

        if (message.getIdLong() != supportMessage) {
            return;
        }

        createTicketReaction(event);
    }

    public static void createTicketReaction(GuildMessageReactionAddEvent event) {
        Member member = event.getMember();
        Category category = event.getGuild().getCategoryById(Config.getLong("catId"));
        Role supportRole = event.getGuild().getRoleById(Config.getLong("supportRoleId"));

        event.getReaction().removeReaction(member.getUser()).queue();

        EnumSet<Permission> allow = EnumSet.of(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION);
        EnumSet<Permission> deny = EnumSet.of(Permission.MANAGE_CHANNEL);
        category.createTextChannel("Ticket " + member.getEffectiveName()).addMemberPermissionOverride(member.getIdLong(), allow, deny).queue(c -> {
            DatabaseManager.INSTANCE.setSupportTicket(event.getGuild().getIdLong(), member.getIdLong(), c.getIdLong());
            EmbedBuilder eb = new EmbedBuilder().setTitle("Welkom " + member.getEffectiveName())
                    .addField("", "Bedankt voor het openen van een support ticket. Over enkele momenten zou er iemand van ons " + supportRole.getAsMention() + " team jou te hulp staan\n" +
                            "Typ alvast je vraag uit want dat scheelt best wat tijd", false);
            c.sendMessage(eb.build()).queue();

            c.sendMessage(supportRole.getAsMention()).queue(m -> {
                m.delete().queueAfter(50, TimeUnit.MILLISECONDS);
            });

            int id = DatabaseManager.INSTANCE.getTicketId(event.getGuild().getIdLong(), c.getIdLong());
            c.getManager().setName(id + "-" + member.getEffectiveName()).queue();
        });
    }
}
