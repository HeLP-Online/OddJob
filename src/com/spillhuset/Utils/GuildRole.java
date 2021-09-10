package com.spillhuset.Utils;

import com.spillhuset.Utils.Enum.Role;

public interface GuildRole {
    /**
     * Lowest Role needed in the Guild to perform
     * @return Role
     */
    Role getRole();

    /**
     * Must belong to a Guild
     * @return Boolean
     */
    boolean needGuild();

    /**
     * Can't belong to Guild
     * @return Boolean
     */
    boolean needNoGuild();
}
