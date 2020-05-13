package no.helponline.Utils.Arena;

public enum Permission {
    ARENA("odd.arena"),

    CONFIG("odd.config"),

    START("odd.start"),

    GAME("odd.game"),
    LOBBY("odd.lobby"),

    LIST("odd.list"),

    JOIN("odd.join"),

    STATS("odd.stats"),

    SPECTATE("odd.spectate");

    private final String permission;

    private Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
