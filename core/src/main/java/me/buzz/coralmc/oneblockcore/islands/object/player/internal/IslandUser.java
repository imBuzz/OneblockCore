package me.buzz.coralmc.oneblockcore.islands.object.player.internal;

import com.google.common.collect.Maps;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.buzz.coralmc.oneblockcore.islands.object.enums.IslandPermissions;
import me.buzz.coralmc.oneblockcore.islands.object.enums.IslandRank;

import java.util.Map;

@Data
@NoArgsConstructor
public class IslandUser {

    private String playerName;
    private long joinedDate;
    private IslandRank islandRank;

    private Map<IslandPermissions, Boolean> permissions;

    public IslandUser(String creator, long currentTimeMillis, IslandRank leader) {
        playerName = creator;
        joinedDate = currentTimeMillis;
        islandRank = leader;

        permissions = Maps.newEnumMap(IslandPermissions.class);

        for (int inheritRank = islandRank.getInheritRank(); inheritRank >= 0; inheritRank--) {
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
        this.islandRank = islandRank;

        for (int inheritRank = islandRank.getInheritRank() + 1; inheritRank >= 0; inheritRank--) {
            IslandRank rank = IslandRank.values()[inheritRank];

            for (IslandPermissions islandPermission : rank.getIslandPermissions()) {
                permissions.put(islandPermission, true);
            }
        }
    }

    public void load() {
        for (int inheritRank = islandRank.getInheritRank(); inheritRank >= 0; inheritRank--) {
            IslandRank rank = IslandRank.values()[inheritRank];

            for (IslandPermissions islandPermission : rank.getIslandPermissions()) {
                if (!permissions.containsKey(islandPermission))
                    permissions.put(islandPermission, true);
            }
        }
    }

}
