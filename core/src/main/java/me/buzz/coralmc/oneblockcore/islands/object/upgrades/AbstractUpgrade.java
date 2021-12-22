package me.buzz.coralmc.oneblockcore.islands.object.upgrades;

import lombok.Getter;
import me.buzz.coralmc.oneblockcore.islands.object.Island;
import me.buzz.coralmc.oneblockcore.islands.object.enums.UpgradeType;

public abstract class AbstractUpgrade {

    protected transient final int maxLevel;
    protected transient final UpgradeType type;
    @Getter
    protected int level = 0;
    protected transient int[] moneyCost;

    public AbstractUpgrade(UpgradeType type) {
        this.type = type;
        this.maxLevel = type.getMaxLevel();
    }

    public void increment() {
        level++;
        if (level > maxLevel) level = maxLevel;
    }


    public abstract void load();

    public abstract void compute(Island island);

    public boolean isMax() {
        return maxLevel == level;
    }


}
