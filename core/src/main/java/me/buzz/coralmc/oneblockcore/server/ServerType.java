package me.buzz.coralmc.oneblockcore.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.buzz.coralmc.oneblockcore.game.Game;
import me.buzz.coralmc.oneblockcore.islands.IslandGame;
import me.buzz.coralmc.oneblockcore.lobby.LobbyGame;

import java.util.Locale;
import java.util.function.Supplier;

@RequiredArgsConstructor
public enum ServerType {

    LOBBY(LobbyGame::new),
    ISLANDS(IslandGame::new);

    @Getter private final Supplier<Game> game;

    @Override
    public String toString() {
        return super.toString().toLowerCase(Locale.ROOT);
    }
}
