package nl.dusmartijngames.bot.NLKingdom.database;

import java.util.List;

public interface DatabaseManager {
    DatabaseManager INSTANCE = new SQLiteDataSource();

    long getRoleChannel(long guildId);
    long getRoleMessage(long guildId);
    void setRoleMessage(long guildId, long messageId);
    void setRoleChannel(long guildId, long channelId);
    List<Long> getRoles(long guildId);
    void addRolesEmote(long guildId, long roleId, long emoteId);
    long getEmote(long guildId, long roleId);
    void updateRoles(long guildId, long roleId, long emoteId);
    void removeRole(long guildId, long roleId);
    long getRoleFromEmote(long guildId, long emoteId);
    long getSupportTicket(long guildId, long channelId);
    void setSupportTicket(long guildId, long ownerId, long channelId);
    boolean init();
    boolean isTicket(long guildId, long channelId);
    long getSupportOwner(long guildId, long channelId);
    void setTicketActive(long guildId, long channelId, boolean active, String transcript);
    int getTicketId(long guildId, long channelId);
    void setSupportMessage(long guildId, long messageId);
    long getSupportMessage(long guildId);
    String getPrefix(long guildId);
    void setPrefix(long guildId, String newPrefix);
    String getTranscript(int ticketId);
    boolean isActiveTicker(int ticketId);

}
