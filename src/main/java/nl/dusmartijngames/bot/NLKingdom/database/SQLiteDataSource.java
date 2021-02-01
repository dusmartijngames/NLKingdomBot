package nl.dusmartijngames.bot.NLKingdom.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import nl.dusmartijngames.bot.NLKingdom.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SQLiteDataSource implements DatabaseManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteDataSource.class);
    private final HikariDataSource ds;

    public SQLiteDataSource() {
//        try {
//            final File dbFile = new File("database.db");
//
//            if (!dbFile.exists()) {
//                if (dbFile.createNewFile()) {
//                    LOGGER.info("Created database file");
//                } else {
//                    LOGGER.info("Could not create database file");
//                }
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//
//        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(Config.get("db_url"));
        config.setConnectionTestQuery("SELECT 1");
        config.setUsername(Config.get("db_username"));
        config.setPassword(Config.get("db_password"));
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);

        try (final Connection connection = getConnection()){
                final Statement statement = connection.createStatement();
            final String defaultPrefix = Config.get("prefix");

            // language=MySQL
            statement.execute("CREATE TABLE IF NOT EXISTS roles_settings (" +
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT," +
                    "guild_id VARCHAR(20) NOT NULL, "+
                    "roles VARCHAR(255) NULL," +
                    "emotes VARCHAR(255) NULL"+
                    ")");
            // language=MySQL
            statement.execute("CREATE TABLE IF NOT EXISTS guild_settings (" +
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT," +
                    "guild_id VARCHAR(20) NOT NULL," +
                    "prefix VARCHAR(255) NOT NULL DEFAULT '" + defaultPrefix + "'," +
                    "roleChannel MEDIUMTEXT NULL," +
                    "verifyChannel MEDIUMTEXT NULL," +
                    "supportMessage varchar(20) NULL," +
                    "roleMessage MEDIUMTEXT NULL" +
                    ");");

            // language=MySQL
            statement.execute("CREATE TABLE IF NOT EXISTS support_tickets(" +
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT," +
                    "guild_id VARCHAR(20) NOT NULL, " +
                    "owner_id varchar(20) NOT NULL," +
                    "channel_id varchar(20) NOT NULL," +
                    "status BOOL NOT NULL," +
                    "transcript_file varchar(2048) NULL);");

            System.out.println("Table initialised");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getPrefix(long guildId) {
        try (Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection
                // language=MySQL
                .prepareStatement("SELECT prefix FROM guild_settings WHERE guild_id = ?");

            preparedStatement.setString(1, String.valueOf(guildId));

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("prefix");
                }
            }

            try (final PreparedStatement insertStatement = getConnection()
                    // language=MySQL
                    .prepareStatement("INSERT INTO guild_settings(guild_id) VALUES(?)")) {

                insertStatement.setString(1, String.valueOf(guildId));

                insertStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Config.get("prefix");
    }


    @Override
    public void setPrefix(long guildId, String newPrefix) {
        try (Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection
                // language=MySQL
                .prepareStatement("UPDATE guild_settings SET prefix = ? WHERE guild_id = ?");

            //
            preparedStatement.setString(1, newPrefix);
            preparedStatement.setString(2, String.valueOf(guildId));

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getRoleChannel(long guildId) {
        try (Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection
             // language=MySQL
             .prepareStatement("SELECT roleChannel FROM guild_settings WHERE guild_id = ?");
            preparedStatement.setString(1, String.valueOf(guildId));

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("roleChannel");
                }
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    @Override
    public long getRoleMessage(long guildId) {
        try (Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection
                // language=MySQL
                .prepareStatement("SELECT roleMessage FROM guild_settings WHERE guild_id = ?");
            preparedStatement.setString(1, String.valueOf(guildId));

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("roleMessage");
                }
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return 0;
    }

    @Override
    public void setRoleMessage(long guildId, long messageId) {
        try (Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection
                // language=MySQL
                .prepareStatement("UPDATE guild_settings SET roleMessage = ? where guild_id = ?");

            preparedStatement.setLong(1, messageId);
            preparedStatement.setString(2, String.valueOf(guildId));

            preparedStatement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public long getSupportMessage(long guildId) {
        try (Connection connection = getConnection()) {
                final PreparedStatement preparedStatement = connection
                // language=MySQL
                .prepareStatement("SELECT supportMessage FROM guild_settings WHERE guild_id = ?");
            preparedStatement.setString(1, String.valueOf(guildId));

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("supportMessage");
                }
            }

            try (final PreparedStatement insertStatement = getConnection()
                    // language=MySQL
                    .prepareStatement("INSERT INTO guild_settings(guild_id) VALUES(?)")) {

                insertStatement.setString(1, String.valueOf(guildId));

                insertStatement.execute();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;

    }

    @Override
    public void setSupportMessage(long guildId, long messageId) {
        try (Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection
                // language=MySQL
                .prepareStatement("UPDATE guild_settings SET supportMessage = ? where guild_id = ?");

            preparedStatement.setLong(1, messageId);
            preparedStatement.setString(2, String.valueOf(guildId));

            preparedStatement.executeUpdate();

            try (final PreparedStatement insertStatement = getConnection()
                    // language=MySQL
                    .prepareStatement("INSERT INTO guild_settings(guild_id) VALUES(?)")) {

                insertStatement.setString(1, String.valueOf(guildId));

                insertStatement.execute();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void setRoleChannel(long guildId, long channelId) {
        try (Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection
            // language=MySQL
             .prepareStatement("UPDATE guild_settings SET roleChannel = ? where guild_id = ?");

            preparedStatement.setLong(1, channelId);
            preparedStatement.setString(2, String.valueOf(guildId));

            preparedStatement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public List<Long> getRoles(long guildId) {
        try (Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection
             // language=MySQL
             .prepareStatement("SELECT roles FROM roles_settings WHERE guild_id = ?");
            preparedStatement.setString(1, String.valueOf(guildId));

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Long> ids = new java.util.ArrayList<>(Collections.emptyList());
                while (resultSet.next()) {
                    ids.add(resultSet.getLong("roles"));
                }

                System.out.println(ids);
                return ids;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Arrays.asList(0L);
    }

    @Override
    public long getRoleFromEmote(long guildId, long emoteId) {
        try (Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection
                // language=MySQL
                .prepareStatement("SELECT roles FROM roles_settings WHERE guild_id = ? AND emotes = ?");
            preparedStatement.setString(1, String.valueOf(guildId));
            preparedStatement.setLong(2, emoteId);

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println(resultSet.getLong("roles"));
                    return resultSet.getLong("roles");
                }

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return 0;
    }

    @Override
    public void addRolesEmote(long guildId, long roleId, long emoteId) {
        try (Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection
                // language=MySQL
                .prepareStatement("INSERT INTO roles_settings (guild_id, roles, emotes) VALUE (?, ?, ?)");

            preparedStatement.setLong(2, roleId);
            preparedStatement.setLong(3, emoteId);
            preparedStatement.setLong(1, guildId);

            preparedStatement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public long getEmote(long guildId, long roleId) {
        try (Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection
                // language=MySQL
                .prepareStatement("SELECT emotes FROM roles_settings WHERE guild_id = ? AND roles = ?");
            preparedStatement.setString(1, String.valueOf(guildId));
            preparedStatement.setLong(2, roleId);

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("emotes");
                }
            }

            try (final PreparedStatement insertStatement = getConnection()
                    // language=MySQL
                    .prepareStatement("INSERT INTO roles_settings(guild_id) VALUES(?)")) {

                insertStatement.setString(1, String.valueOf(guildId));

                insertStatement.execute();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    @Override
    public void updateRoles(long guildId, long roleId, long emoteId) {
        try (Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection
                // language=MySQL
                .prepareStatement("UPDATE roles_settings SET emotes = ? where guild_id = ? AND roles = ?");

            preparedStatement.setLong(3, roleId);
            preparedStatement.setLong(1, emoteId);
            preparedStatement.setLong(2, guildId);

            preparedStatement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void removeRole(long guildId, long roleId) {
            try (Connection connection = getConnection()) {
                final PreparedStatement preparedStatement = connection
                // language=MySQL
                .prepareStatement("DELETE FROM roles_settings WHERE guild_id = ? AND roles = ?");

            preparedStatement.setLong(1, guildId);
            preparedStatement.setLong(2, roleId);

            preparedStatement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public long getSupportTicket(long guildId, long channelId) {
//        try (final PreparedStatement preparedStatement = getConnection()
//                // language=MySQL
//                .prepareStatement("SELECT emotes FROM roles_settings WHERE guild_id = ? AND roles = ?")) {
//            preparedStatement.setString(1, String.valueOf(guildId));
//            preparedStatement.setLong(2, roleId);
//
//            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
//                if (resultSet.next()) {
//                    return resultSet.getLong("emotes");
//                }
//            }
//
//            try (final PreparedStatement insertStatement = getConnection()
//                    // language=MySQL
//                    .prepareStatement("INSERT INTO roles_settings(guild_id) VALUES(?)")) {
//
//                insertStatement.setString(1, String.valueOf(guildId));
//
//                insertStatement.execute();
//            }
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//        return 0;

        return 0;
    }

    @Override
    public void setSupportTicket(long guildId, long ownerId, long channelId) {
        try (Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection
                // language=MySQL
                .prepareStatement("INSERT INTO support_tickets (guild_id, owner_id, channel_id, status, transcript_file) VALUE (?, ?, ?, ?, ?)");

            preparedStatement.setLong(1, guildId);
            preparedStatement.setLong(2, ownerId);
            preparedStatement.setLong( 3, channelId);
            preparedStatement.setBoolean(4, true);
            preparedStatement.setString(5, null);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean init() {
        try (Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection
                // language=MySQL
                .prepareStatement("SELECT * FROM guild_settings");

            preparedStatement.executeQuery();
            return true;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isTicket(long guildId, long channelId) {
        try (Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection
                // language=MySQL
                .prepareStatement("SELECT * FROM support_tickets where guild_id = ? and channel_id = ?");

            preparedStatement.setLong(1, guildId);
            preparedStatement.setLong(2, channelId);
            final ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public long getSupportOwner(long guildId, long channelId) {
        try (Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection
                // language=MySQL
                .prepareStatement("SELECT * FROM support_tickets where guild_id = ? and channel_id = ?");

            preparedStatement.setLong(1, guildId);
            preparedStatement.setLong(2, channelId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("owner_id");
                }
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getTicketId(long guildId, long channelId) {
        try (Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection
                // language=MySQL
                .prepareStatement("SELECT * FROM support_tickets where guild_id = ? and channel_id = ?");

            preparedStatement.setLong(1, guildId);
            preparedStatement.setLong(2, channelId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    @Override
    public void setTicketActive(long guildId, long channelId, boolean active, String transcript) {
        try (Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection
                // language=MySQL
                .prepareStatement("UPDATE support_tickets SET status = ? WHERE guild_id = ? AND channel_id = ?");

            try (final PreparedStatement statement = connection
                // language=MySQL
                .prepareStatement("UPDATE support_tickets SET transcript_file = ? WHERE guild_id = ? AND channel_id = ?")) {

                statement.setString(1, transcript);
                statement.setLong(2, guildId);
                statement.setLong(3, channelId);

                statement.executeUpdate();
            }

            preparedStatement.setBoolean(1, active);
            preparedStatement.setLong(2, guildId);
            preparedStatement.setLong(3, channelId);

            preparedStatement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public String getTranscript(int ticketId) {
        try (Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection
                // language=MySQL
                .prepareStatement("SELECT transcript_file FROM support_tickets where id = ? ");

            preparedStatement.setInt(1, ticketId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("transcript_file");
                }
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean isActiveTicker(int ticketId) {
        try (Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection
                    // language=MySQL
                    .prepareStatement("SELECT status FROM support_tickets where id = ? ");

            preparedStatement.setInt(1, ticketId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                }
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return false;
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }


}