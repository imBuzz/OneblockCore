package me.buzz.coralmc.oneblockcore.islands.object.upgrades.impl;

import me.buzz.coralmc.oneblockcore.islands.object.Island;
import me.buzz.coralmc.oneblockcore.islands.object.enums.UpgradeType;
import me.buzz.coralmc.oneblockcore.islands.object.upgrades.AbstractUpgrade;

public class SizeUpgrade extends AbstractUpgrade {

    private final static int[] sizes = new int[]{100, 150, 200};

    public SizeUpgrade() {
        super(UpgradeType.ISLAND_SIZE);
    }

    @Override
    public void load() {

    }

    @Override
    public void compute(Island island) {
        if (isMax()) return;
        island.setCurrentRange(sizes[level - 1]);
    }

}
