package nl.dusmartijngames.bot.NLKingdom.commands.admin;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.dusmartijngames.bot.NLKingdom.RoleChannel;
import nl.dusmartijngames.bot.NLKingdom.database.DatabaseManager;
import nl.dusmartijngames.bot.NLKingdom.objects.CommandContext;
import nl.dusmartijngames.bot.NLKingdom.objects.ICommand;

import java.util.List;

public class RoleChannelCommand implements ICommand {
    @Override
    public void handle(CommandContext event) {
        List<TextChannel> channels = event.getMessage().getMentionedChannels();
        final TextChannel channel = event.getChannel();
        final Member member = event.getMember();

        if (!member.hasPermission(Permission.MANAGE_PERMISSIONS)) {
            channel.sendMessage("You must have the Manage Server permission to use his command").queue();
            return;
        }

        if (channels.isEmpty()) {
            channel.sendMessage("Missing args").queue();
            return;
        } else {
            try {
                TextChannel newChannel = channels.get(0);
                updateChannel(event.getGuild().getIdLong(), newChannel.getIdLong());

                channel.sendMessageFormat("Role Assignment channel has been set to %s", newChannel.getAsMention()).queue();

            } catch (Exception e) {
                e.printStackTrace();
                event.getJDA().getGuildById(612189253824151552L).getTextChannelById(771454723353870418L).sendMessage(e.toString());
            }
        }
    }

    public void updateChannel(Long guildId, Long newChannel) {
        RoleChannel.CHANNELS.put(guildId, newChannel);
        DatabaseManager.INSTANCE.setRoleChannel(guildId, newChannel);

    }

    @Override
    public String getName() {
        return "src";
    }

    @Override
    public String getHelp(CommandContext event) {
        return "Sets the channel for roleassignment to the channel you wish";
    }

    @Override
    public String getCat() {
        return "admin";
    }
}
