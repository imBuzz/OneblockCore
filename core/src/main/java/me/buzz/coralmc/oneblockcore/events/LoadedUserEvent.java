package me.buzz.coralmc.oneblockcore.events;

import lombok.Getter;
import me.buzz.coralmc.oneblockcore.players.User;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LoadedUserEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    @Getter
    private final User user;

    public LoadedUserEvent(User user) {
        super(true);
        this.user = user;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}
