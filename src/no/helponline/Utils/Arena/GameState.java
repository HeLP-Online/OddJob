package no.helponline.Utils.Arena;

public enum GameState {
    WAITING, COOLDOWN, INGAME, DEATHMATCH, RESET;

    public boolean isIngame() {
        return this == INGAME || this == DEATHMATCH;
    }
}
