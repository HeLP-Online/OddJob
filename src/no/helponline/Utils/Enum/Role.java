package no.helponline.Utils.Enum;

public enum Role {
    Master(99),
    Admins(33),
    Mods(22),
    Members(11),
    all(0);

    private int level;

    Role(int level) {
        this.level = level;
    }

    public int level() {
        return this.level;
    }
}
