package me.buzz.coralmc.oneblockcore.islands.object.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UpgradeType {

    ISLAND_SIZE(3);

    private final int maxLevel;


}
