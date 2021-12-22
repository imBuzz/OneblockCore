package me.buzz.coralmc.oneblockcore.islands.object.enums;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
@Getter
public enum IslandRank {

    MEMBER("Membro", Sets.newHashSet(
            IslandPermissions.PLACE_BLOCKS,
            IslandPermissions.BREAK_BLOCKS,
            IslandPermissions.OPEN_CHEST,
            IslandPermissions.INTERACT_WITH_DOOR,
            IslandPermissions.BREAK_CORE
    ), 0, 0),

    MODERATOR("Moderatore", Sets.newHashSet(
            IslandPermissions.KICK_VISITOR,
            IslandPermissions.BAN_PLAYERS,
            IslandPermissions.UNBAN_PLAYERS,
            IslandPermissions.CHANGE_CORE,
            IslandPermissions.MODIFY_SETTINGS,
            IslandPermissions.CHANGE_SPAWN,
            IslandPermissions.CHANGE_VISITATOR_SPAWN
    ), 0, 1),

    CO_LEADER("Co-Leader", Sets.newHashSet(
            IslandPermissions.KICK_PLAYER,
            IslandPermissions.INVITE_PLAYER,
            IslandPermissions.MODIFY_PERMISSIONS,
            IslandPermissions.PROMOTE_PLAYERS,
            IslandPermissions.DEMOTE_PLAYERS
    ), 1, 2),

    LEADER("Leader", Sets.newHashSet(), 2, 3);

    private final String name;
    private final Set<IslandPermissions> islandPermissions;
    private final int inheritRank;
    private final int index;

    public IslandRank getNextRank() {
        int newIndex = index + 1;
        if (newIndex >= IslandRank.values().length)
            return null;


        return IslandRank.values()[newIndex];
    }

    public IslandRank getPreviousRank() {
        int newIndex = index - 1;
        if (newIndex < 0)
            return null;

        return IslandRank.values()[newIndex];
    }


}
