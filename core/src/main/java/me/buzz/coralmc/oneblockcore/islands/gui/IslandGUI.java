package me.buzz.coralmc.oneblockcore.islands.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.RequiredArgsConstructor;
import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.islands.object.Island;
import me.buzz.coralmc.oneblockcore.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

@RequiredArgsConstructor
public class IslandGUI implements InventoryProvider {
    private final OneblockCore core = OneblockCore.get();

    private final Island island;
    private final boolean clickable;

    public static SmartInventory getInventory(Island island, boolean clickable) {
        return SmartInventory.builder()
                .provider(new IslandGUI(island, clickable))
                .size(3, 9)
                .manager(OneblockCore.get().getInventoryManager())
                .title(ChatColor.DARK_GRAY + "Menu Isola")
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        createIslandStats(contents);
        createMembers(player, contents);
        createUpgrades(player, contents);
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }

    private void createIslandStats(InventoryContents contents) {
        ItemBuilder itemBuilder = new ItemBuilder(Material.SKULL_ITEM);
        itemBuilder.setSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmZkMjUzYzRjNmQ2NmVkNjY5NGJlYzgxOGFhYzFiZTc1OTRhM2RkOGU1OTQzOGQwMWNiNzY3MzdmOTU5In19fQ==");
        itemBuilder.setName("&b&lStatistiche Isola");
        itemBuilder.addLoreLines(
                "",
                "&b&lMembri: &f" + island.getPlayers().size() + " / ",
                "&b&lFase: &f",
                "",
                "&b&lRange Isola",
                "&f" + island.getCurrentRange() + "x" + island.getCurrentRange()
        );
        contents.set(0, 4, ClickableItem.empty(itemBuilder.build()));
    }

    private void createMembers(Player player, InventoryContents contents) {
        ItemBuilder itemBuilder = new ItemBuilder(Material.BOOK);
        itemBuilder.addEnchant(Enchantment.DIG_SPEED, 1);
        itemBuilder.setFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        itemBuilder.setName("&b&lMembri Isola");
        itemBuilder.addLoreLines(
                "&7Clicca per vedere e controllare",
                "&7tutti i membri dell'isola e i loro rank.",
                ""
        );
        if (island.getPlayers().size() > 1)
            itemBuilder.addLoreLines("&b&l" + island.getPlayers().size() + " Membri");
        else itemBuilder.addLoreLines("&b&l1 Membro");


        //contents.set(1, 3, ClickableItem.of(itemBuilder.build(), e -> Members.getInventory(IslandModule, loadedIsland, clickable).open(player)));
    }

    private void createUpgrades(Player player, InventoryContents contents) {
        ItemBuilder itemBuilder = new ItemBuilder(Material.HOPPER);
        itemBuilder.setFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        itemBuilder.setName("&b&lPotenziamenti");
        itemBuilder.addLoreLines(
                "&7Clicca per vedere e controllare",
                "&7tutti gli upgrades dell'isola."
        );

        contents.set(1, 2, ClickableItem.of(itemBuilder.build(), e -> {
            //Upgrades.getInventory(IslandModule, loadedIsland, clickable).open(player);
        }));
    }


}
