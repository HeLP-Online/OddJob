package com.spillhuset.Utils;

import com.spillhuset.Utils.Enum.Role;

public interface GuildRole {
    Role getRole();
    boolean needGuild();
    boolean needNoGuild();
}
