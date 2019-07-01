package no.helponline.Guilds;

public enum Role {
    guildMaster(99),
    admins(33),
    mods(22),
    members(11),
    all(0);

    private int level;

    Role(int level) {
        this.level = level;
    }

    public int level() {
        return this.level;
    }
}
