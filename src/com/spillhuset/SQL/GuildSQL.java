package com.spillhuset.SQL;

import com.spillhuset.Managers.MySQLManager;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Role;
import com.spillhuset.Utils.Enum.Zone;
import com.spillhuset.Utils.Guild;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class GuildSQL extends MySQLManager {
    public static HashMap<UUID, Guild> loadGuilds() {
        HashMap<UUID, Guild> guilds = new HashMap<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds` WHERE `server` = ?");
            preparedStatement.setString(1, OddJob.getInstance().getServerId().toString());
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                OddJob.getInstance().log("Nope--------------------------------");
                HashMap<UUID, Role> members = new HashMap<>();
                UUID guildUUID = UUID.fromString(resultSet.getString("uuid"));
                preparedStatementsec = connection.prepareStatement("SELECT * FROM `mine_guilds_members` WHERE `uuid` = ?");
                preparedStatementsec.setString(1, resultSet.getString("uuid"));
                resultSetsec = preparedStatementsec.executeQuery();

                while (resultSetsec.next()) {
                    members.put(UUID.fromString(resultSetsec.getString("player")), Role.valueOf(resultSetsec.getString("role")));
                }
                guilds.put(guildUUID, new Guild(
                        guildUUID,
                        resultSet.getString("name"),
                        Zone.valueOf(resultSet.getString("zone")),
                        resultSet.getInt("invited_only") == 1,
                        resultSet.getInt("friendly_fire") == 1,
                        resultSet.getInt("open") == 1,
                        Role.valueOf(resultSet.getString("permission_invite")),
                        Role.valueOf(resultSet.getString("permission_kick")),
                        members
                ));

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return guilds;
    }

    public static void saveGuilds(HashMap<UUID, Guild> guilds) {
        for (UUID guildUUID : guilds.keySet()) {
            Guild guild = OddJob.getInstance().getGuildManager().getGuild(guildUUID);
            String name = guild.getName();
            Zone zone = guild.getZone();
            boolean invitedOnly = guild.getInvitedOnly();
            boolean friendlyFire = guild.getFriendlyFire();
            boolean open = guild.getOpen();
            Role permissionInviteRole = guild.getPermissionInvite();
            Role permissionKickRole = guild.getPermissionKick();
            HashMap<UUID, Role> members = guild.getMembers();

            try {
                connect();
                preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_guilds` WHERE `uuid` = ? AND `server` = ?");
                preparedStatement.setString(1, guildUUID.toString());
                preparedStatement.setString(2, OddJob.getInstance().getServerId().toString());
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    preparedStatement = connection.prepareStatement("UPDATE `mine_guilds` SET `name` = ?,`zone` = ?,`invited_only` = ?,`friendly_fire` = ?,`permission_invite` = ?,`permission_kick` = ?,`open` = ? WHERE `uuid` = ?");
                    preparedStatement.setString(1, name);
                    preparedStatement.setString(2, zone.name());
                    preparedStatement.setInt(3, invitedOnly ? 1 : 0);
                    preparedStatement.setInt(4, friendlyFire ? 1 : 0);
                    preparedStatement.setString(5, permissionInviteRole.name());
                    preparedStatement.setString(6, permissionKickRole.name());
                    preparedStatement.setInt(7, open ? 1 : 0);
                    preparedStatement.setString(8, guildUUID.toString());
                    preparedStatement.executeUpdate();
                } else {
                    preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds` (`uuid`,`name`,`zone`,`invited_only`,`friendly_fire`,`permission_invite`,`permission_kick`,`open`,`server`) VALUES (?,?,?,?,?,?,?,?,?)");
                    preparedStatement.setString(1, guildUUID.toString());
                    preparedStatement.setString(2, name);
                    preparedStatement.setString(3, zone.name());
                    preparedStatement.setInt(4, invitedOnly ? 1 : 0);
                    preparedStatement.setInt(5, friendlyFire ? 1 : 0);
                    preparedStatement.setString(6, permissionInviteRole.name());
                    preparedStatement.setString(7, permissionKickRole.name());
                    preparedStatement.setInt(8, open ? 1 : 0);
                    preparedStatement.setString(9, OddJob.getInstance().getServerId().toString());
                    preparedStatement.execute();
                }

                for (UUID uuid : members.keySet()) {
                    preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds_members` WHERE `player` = ?");
                    preparedStatement.setString(1, uuid.toString());
                    resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        if (!UUID.fromString(resultSet.getString("uuid")).equals(guildUUID) || !Role.valueOf(resultSet.getString("role")).equals(members.get(uuid))) {
                            preparedStatement = connection.prepareStatement("UPDATE `mine_guilds_members` SET `uuid` = ?, `role` = ? WHERE `player` = ?");
                            preparedStatement.setString(1, guildUUID.toString());
                            preparedStatement.setString(2, members.get(uuid).name());
                            preparedStatement.setString(3, uuid.toString());
                            preparedStatement.executeUpdate();
                        }
                    } else {
                        preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds_members` (`uuid`,`player`,`role`) VALUES (?,?,?)");
                        preparedStatement.setString(1, guildUUID.toString());
                        preparedStatement.setString(2, uuid.toString());
                        preparedStatement.setString(3, members.get(uuid).name());
                        preparedStatement.execute();
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                close();
            }
        }
    }
}
