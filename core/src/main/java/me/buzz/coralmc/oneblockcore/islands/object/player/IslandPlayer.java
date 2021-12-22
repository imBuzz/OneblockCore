package me.buzz.coralmc.oneblockcore.islands.object.player;

import com.google.common.collect.Maps;
import lombok.Data;
import me.buzz.coralmc.oneblockcore.islands.object.enums.IslandPermissions;
import me.buzz.coralmc.oneblockcore.islands.object.enums.IslandRank;

import java.util.Map;

@Data
public class IslandPlayer {

    private String playerName;
    private long joinedDate;
    private IslandRank rank;

    private Map<IslandPermissions, Boolean> permissions;

    public IslandPlayer(String player, long currentTimeMillis, IslandRank rankk) {
        playerName = player;
        joinedDate = currentTimeMillis;
        rank = rankk;

        permissions = Maps.newEnumMap(IslandPermissions.class);

        for (int inheritRank = rank.getInheritRank(); inheritRank >= 0; inheritRank--) {
            IslandRank rank = IslandRank.values()[inheritRank];

            for (IslandPermissions islandPermission : rank.getIslandPermissions()) {
                permissions.put(islandPermission, true);
            }
        }
    }

    public boolean hasPermission(IslandPermissions permission) {
        if (permissions.containsKey(permission))
            return permissions.get(permission);

        return false;

    }

    public void setIslandRank(IslandRank islandRank) {
        this.rank = islandRank;

        for (int inheritRank = islandRank.getInheritRank() + 1; inheritRank >= 0; inheritRank--) {
            IslandRank rank = IslandRank.values()[inheritRank];

            for (IslandPermissions islandPermission : rank.getIslandPermissions()) {
                permissions.put(islandPermission, true);
            }
        }
    }

    public void load() {
        for (int inheritRank = rank.getInheritRank(); inheritRank >= 0; inheritRank--) {
            IslandRank rank = IslandRank.values()[inheritRank];

            for (IslandPermissions islandPermission : rank.getIslandPermissions()) {
                if (!permissions.containsKey(islandPermission))
                    permissions.put(islandPermission, true);
            }
        }
    }

}
