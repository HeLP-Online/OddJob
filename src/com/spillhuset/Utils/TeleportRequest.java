package com.spillhuset.Utils;

import com.spillhuset.Utils.Enum.TeleportType;
import org.bukkit.entity.Player;

public class TeleportRequest {
    private final Player target;
    private final TeleportType type;
    private final Object destination;
    private final long timeout;
    private final boolean countdown;

    public TeleportRequest(Player target, TeleportType type, Object destination, long timeout, boolean countdown) {
        this.target = target;
        this.type = type;
        this.destination = destination;
        this.timeout = timeout;
        this.countdown = countdown;
    }

    public Player getTarget() {
        return target;
    }

    public TeleportType getType() {
        return type;
    }

    public Object getDestination() {
        return destination;
    }

    public long getTimeout() {
        return timeout;
    }

    public boolean isCountdown() {
        return countdown;
    }
}
