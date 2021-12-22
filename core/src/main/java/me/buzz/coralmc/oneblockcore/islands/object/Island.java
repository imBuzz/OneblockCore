package me.buzz.coralmc.oneblockcore.islands.object;

import com.google.common.collect.Maps;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.Getter;
import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.database.DataService;
import me.buzz.coralmc.oneblockcore.islands.object.enums.IslandPermissions;
import me.buzz.coralmc.oneblockcore.islands.object.enums.IslandPositionType;
import me.buzz.coralmc.oneblockcore.islands.object.enums.IslandRank;
import me.buzz.coralmc.oneblockcore.islands.object.enums.UpgradeType;
import me.buzz.coralmc.oneblockcore.islands.object.player.IslandPlayer;
import me.buzz.coralmc.oneblockcore.islands.object.player.internal.BannedIslandPlayer;
import me.buzz.coralmc.oneblockcore.islands.object.upgrades.AbstractUpgrade;
import me.buzz.coralmc.oneblockcore.players.User;
import me.buzz.coralmc.oneblockcore.server.redis.messages.ChatMessage;
import me.buzz.coralmc.oneblockcore.structures.WorldPosition;
import me.buzz.coralmc.oneblockcore.utils.Strings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Entity("islands")
public class Island {
    private transient final static int MAX_REDSTONE_VALUE = 1000;
    private final Map<IslandPositionType, WorldPosition> positions = Maps.newEnumMap(IslandPositionType.class);
    private final Map<UpgradeType, AbstractUpgrade> upgrades = Maps.newEnumMap(UpgradeType.class);
    @Getter
    private final Map<String, IslandPlayer> players = Maps.newHashMap();
    @Getter
    private final Map<String, BannedIslandPlayer> bannedPlayers = Maps.newHashMap();
    @Id
    @Getter
    private String uuid;
    private int redstoneValue = 0;
    @Getter
    private transient int currentRange = 50;

    public Island(Player player) {
        uuid = UUID.randomUUID().toString();
        uuid = uuid.substring(0, 5) + uuid.substring(8, 10);

        positions.put(IslandPositionType.SPAWN, new WorldPosition(DataService.ISLAND_WORLD_TAG + uuid, 0.5F, 51, 0.5F));
        positions.put(IslandPositionType.CORE, new WorldPosition(DataService.ISLAND_WORLD_TAG + uuid, 0.0F, 50, 0.0F));

        positions.put(IslandPositionType.CENTER, positions.get(IslandPositionType.CORE));

        positions.put(IslandPositionType.xEDGE, new WorldPosition(positions.get(IslandPositionType.CENTER).getWorld(),
                positions.get(IslandPositionType.CENTER).getX() + currentRange, 50 + currentRange, currentRange));

        positions.put(IslandPositionType.zEDGE, new WorldPosition(positions.get(IslandPositionType.CENTER).getWorld(),
                positions.get(IslandPositionType.CENTER).getX() - currentRange, 50 - currentRange, -currentRange));


        players.put(player.getName().toLowerCase(Locale.ROOT), new IslandPlayer(player.getName(), System.currentTimeMillis(), IslandRank.LEADER));
    }

    public void load() {
        for (AbstractUpgrade value : upgrades.values()) value.load();
    }

    public void join(String playerName, User user) {
        bannedPlayers.remove(playerName.toLowerCase());
        players.put(playerName.toLowerCase(Locale.ROOT), new IslandPlayer(playerName, System.currentTimeMillis(), IslandRank.MEMBER));
        user.setIslandUUID(uuid);

        sendMessage("");
        sendMessage("&b" + playerName + " è entrato nell'isola!", true);
        sendMessage("");
    }

    public void leave(Player player, User user) {
        players.remove(player.getName());
        user.setIslandUUID("");

        sendMessage("");
        sendMessage("&e" + player.getName() + " è uscito dall'isola!", true);
        sendMessage("");
    }

    public void kick(String playerName, Player whoKicked) {
        players.remove(playerName.toLowerCase());

        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            User user = OneblockCore.get().getGame().getUsers().get(playerName);
            if (user != null) {
                user.setIslandUUID("");
            }

            player.sendMessage("");
            player.sendMessage(Strings.translate("&cSei appena stato kickato dell'isola da: " + whoKicked.getName()));
            player.sendMessage("");

            //TODO: SEND TO ONEBLOCK LOBBY OR KICK FROM SERVER
        }

        sendMessage("&e" + playerName + " è stato kickato dall'isola da " + whoKicked.getName(), true);
    }

    public void promote(Player player, Player who) {
        IslandPlayer islandPlayer = players.get(player.getName().toLowerCase());

        IslandRank previousRank = islandPlayer.getRank();
        IslandRank newRank = previousRank.getNextRank();

        if (newRank == null || newRank == IslandRank.LEADER) {
            who.sendMessage(Strings.translate("&cNon puoi promuovere un player più di cosi!"));
            return;
        }

        islandPlayer.setIslandRank(newRank);
        sendMessage("&a" + player.getName() + " è stato promosso al ruolo di " + newRank.getName() + " da " + who.getName(), true);
    }

    public void demote(Player player, Player who) {
        IslandPlayer islandPlayer = players.get(player.getName().toLowerCase());

        IslandRank previousRank = islandPlayer.getRank();
        IslandRank newRank = islandPlayer.getRank().getPreviousRank();

        if (newRank == null) {
            who.sendMessage(Strings.translate("&cNon puoi de-promuovere un player più di cosi!"));
            return;
        }
        islandPlayer.setIslandRank(newRank);

        for (IslandPermissions islandPermission : previousRank.getIslandPermissions())
            islandPlayer.getPermissions().remove(islandPermission);

        sendMessage("&a" + player.getName() + " è stato de-promosso al ruolo di " + newRank.getName() + " da " + who.getName(), true);
    }

    //TODO: FARE IL DISBAND

    public boolean isInIsland(String player) {
        for (IslandPlayer value : players.values()) {
            if (value.getPlayerName().equalsIgnoreCase(player)) return true;
        }
        return false;
    }

    public boolean hasOtherPlayerOnline(String except) {
        for (String playerName : players.keySet()) {
            Player player = Bukkit.getPlayer(playerName);
            if (player != null && !player.getName().equalsIgnoreCase(except)) return true;
        }
        return false;
    }

    public boolean hasPermission(IslandPermissions islandPermissions, Player player) {
        return players.get(player.getName().toLowerCase()).hasPermission(islandPermissions);
    }

    public void sendMessage(String m) {
        sendMessage(m, false);
    }

    public void sendMessage(String m, boolean translate) {
        OneblockCore core = OneblockCore.get();
        core.getDictation().getCommandManager().executeCommand(new ChatMessage(true, uuid, translate ? Strings.translate(m) : m));
    }

    public String getLeader() {
        for (IslandPlayer value : players.values()) {
            if (value.getRank() == IslandRank.LEADER)
                return value.getPlayerName();
        }

        return "null";
    }

    public void setCurrentRange(int currentRange) {
        this.currentRange = currentRange;

        positions.put(IslandPositionType.SPAWN, new WorldPosition(DataService.ISLAND_WORLD_TAG + uuid, 0.5F, 51, 0.5F));
        positions.put(IslandPositionType.CORE, new WorldPosition(DataService.ISLAND_WORLD_TAG + uuid, 0.0F, 50, 0.0F));

        positions.put(IslandPositionType.CENTER, positions.get(IslandPositionType.CORE));

        positions.put(IslandPositionType.xEDGE, new WorldPosition(positions.get(IslandPositionType.CENTER).getWorld(),
                positions.get(IslandPositionType.CENTER).getX() + currentRange, 50 + currentRange, currentRange));

        positions.put(IslandPositionType.zEDGE, new WorldPosition(positions.get(IslandPositionType.CENTER).getWorld(),
                positions.get(IslandPositionType.CENTER).getX() - currentRange, 50 - currentRange, -currentRange));
    }
}
