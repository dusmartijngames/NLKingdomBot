package nl.dusmartijngames.bot.NLKingdom.commands.admin;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.dusmartijngames.bot.NLKingdom.database.DatabaseManager;
import nl.dusmartijngames.bot.NLKingdom.handlers.RoleHandler;
import nl.dusmartijngames.bot.NLKingdom.objects.CommandContext;
import nl.dusmartijngames.bot.NLKingdom.objects.ICommand;

import java.util.List;

public class SetRolesCommand implements ICommand {
    @Override
    public void handle(CommandContext event) {
        Member member = event.getMember();
        List<Emote> emotes = event.getMessage().getEmotes();
        List<Role> roles = event.getMessage().getMentionedRoles();
        TextChannel channel = event.getChannel();
        List<String> args = event.getArgs();

        if (!member.hasPermission(Permission.ADMINISTRATOR)) {
            channel.sendMessage("You are not allowed to use this command!").queue();
            return;
        }

        if(args.isEmpty() || roles.isEmpty()) {
            channel.sendMessage("Invalid arguments. usage: `*" + getName() + " <mode> <role> [emote]`\n" +
                    "modes: add, update, remove").queue();
            return;
        }

        String mode = args.get(0);

        if (mode.equals("remove")) {
            removeRole(event);
            return;
        }

        if(emotes.isEmpty()) {
            channel.sendMessage("Invalid arguments. usage: `*" + getName() + " <mode> <role> [emote]`\n" +
                    "modes: add, update, remove").queue();
            return;
        }

        if(mode.equals("add")) {
            addRole(event);
            return;
        }

        if(mode.equals("update")) {
            updateRoles(event);
            return;
        }

        channel.sendMessage("Mode " + mode + " not found").queue();
    }

    public void addRole(CommandContext event){
        Emote emote = event.getMessage().getEmotes().get(0);
        Role role = event.getMessage().getMentionedRoles().get(0);
        TextChannel channel = event.getChannel();

        addRolesEmotes(event.getGuild().getIdLong(), role.getIdLong(), emote.getIdLong());
        channel.sendMessage("Added Role " + role.getName() + " to the list with emote " + emote.getAsMention()).queue();
        if (DatabaseManager.INSTANCE.getRoleChannel(event.getGuild().getIdLong()) != 0L) {
            RoleHandler.updateMessage(event);
        }
    }

    public void updateRoles(CommandContext event) {
        List<Emote> emotes = event.getMessage().getEmotes();
        List<Role> roles = event.getMessage().getMentionedRoles();
        TextChannel channel = event.getChannel();

        Role role = roles.get(0);
        Emote emote = emotes.get(0);

        updateRolesEmotes(event.getGuild().getIdLong(), role.getIdLong(), emote.getIdLong());
        channel.sendMessage("Updated role " + role.getName() + " On the list with emote " + emote.getAsMention()).queue();
        if (DatabaseManager.INSTANCE.getRoleChannel(event.getGuild().getIdLong()) != 0L) {
            RoleHandler.updateMessage(event);
        }
    }

    public void removeRole(CommandContext event) {
        Role role = event.getMessage().getMentionedRoles().get(0);
        TextChannel channel = event.getChannel();

        removeRolesEmotes(event.getGuild().getIdLong(), role.getIdLong());
        channel.sendMessage("Removed role " + role.getName() + " from the list").queue();
        if (DatabaseManager.INSTANCE.getRoleChannel(event.getGuild().getIdLong()) != 0L) {
            RoleHandler.updateMessage(event);
        }
    }

    public void removeRolesEmotes(Long guildId, Long roleId) {
        DatabaseManager.INSTANCE.removeRole(guildId, roleId);
    }

    public void updateRolesEmotes(Long guildId, Long roleId, Long emoteId) {
        DatabaseManager.INSTANCE.updateRoles(guildId, roleId,  emoteId);
    }

    public void addRolesEmotes(Long guildId, Long roleId, Long emoteId) {
        DatabaseManager.INSTANCE.addRolesEmote(guildId, roleId,  emoteId);
    }

    @Override
    public String getName() {
        return "setroles";
    }

    @Override
    public String getHelp(CommandContext event) {
        return "Zet de rollen voor de reaction roles in de database\n" +
                "gebruik:  !" + getName() + " <mode> <role> [emote]";
    }

    @Override
    public String getCat() {
        return "admin";
    }


}
