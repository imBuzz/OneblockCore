package me.buzz.coralmc.oneblockcore.server.redis.adapters;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@RequiredArgsConstructor @Getter
public class ItemPair {

    private final int slot;
    private final ItemStack item;

    public static List<ItemPair> from(Inventory inventory) {
        List<ItemPair> itemPairs = Lists.newArrayList();

        for (int i = 0; i < inventory.getContents().length; i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack == null || itemStack.getType() == Material.AIR) continue;
            itemPairs.add(new ItemPair(i, inventory.getItem(i)));
        }

        return itemPairs;
    }

}
