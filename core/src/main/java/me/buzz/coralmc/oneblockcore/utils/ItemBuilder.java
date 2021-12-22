package me.buzz.coralmc.oneblockcore.utils;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemBuilder {

    private ItemStack item;
    private ItemMeta meta;

    public ItemBuilder(Material material, int amount) {
        item = new ItemStack(material, amount);
        meta = item.getItemMeta();
    }

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(Material material, short damage) {
        item = new ItemStack(material, 1, damage);
        meta = item.getItemMeta();
    }

    public ItemBuilder(ItemStack items) {
        item = items;
        meta = item.getItemMeta();
    }

    public ItemBuilder(ItemStack items, int amount) {
        item = items;
        if (amount > 64 || amount < 0) amount = 64;
        item.setAmount(amount);
        meta = item.getItemMeta();
    }

    public String getName() {
        return meta.hasDisplayName() ? meta.getDisplayName() : Strings.translate("&f" + WordUtils.capitalize((item.getType().toString().toLowerCase()).replace(" ", "")));
    }

    public ItemBuilder setName(String name) {
        meta.setDisplayName(Strings.translate(name));
        return this;
    }

    public ItemBuilder setDurability(int damage) {
        item.setDurability((short) damage);
        return this;
    }

    public List<String> getLore() {
        return meta.getLore();
    }

    public Map<Enchantment, Integer> getEnchantments() {
        Map<Enchantment, Integer> values = Maps.newHashMap();
        values.putAll(meta.getEnchants());
        return values;
    }

    public ItemBuilder addLore(List<String> lores) {
        List<String> newLore = meta.getLore();
        newLore.addAll(lores);

        meta.setLore(newLore);

        return this;
    }

    public ItemBuilder setFlags(ItemFlag... flags) {
        for (ItemFlag flag : flags)
            meta.addItemFlags(flag);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment ench, int level) {
        meta.addEnchant(ench, level, true);
        return this;
    }

    public ItemBuilder setSkull(String value) {
        SkullMeta meta = (SkullMeta) this.meta;
        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("textures", value));
        Field profileField;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        this.meta = meta;
        return this;
    }

    public ItemBuilder setPlayerSkull(String playerName) {
        SkullMeta meta = (SkullMeta) this.meta;
        meta.setOwner(playerName);
        this.meta = meta;
        return this;
    }

    public ItemBuilder addLoreLines(String... lines) {
        List<String> lore = new ArrayList<>();
        if (meta.hasLore()) lore = new ArrayList<>(meta.getLore());
        for (String line : lines) {
            if (line.equalsIgnoreCase("%empty%")) continue;
            lore.add(Strings.translate(line));
        }

        meta.setLore(lore);
        return this;
    }

    public ItemBuilder removeLoreLines(List<String> lines) {
        List<String> lore = new ArrayList<>();
        if (meta.hasLore()) lore = new ArrayList<>(meta.getLore());

        for (String s : meta.getLore()) {
            for (String line : lines) {
                if (s.replaceAll("§", "&").equalsIgnoreCase(line)) lore.remove(s);
            }
        }

        meta.setLore(lore);
        return this;
    }

    public ItemBuilder removeLoreLines(String... lines) {
        List<String> lore = new ArrayList<>();
        if (meta.hasLore()) lore = new ArrayList<>(meta.getLore());

        for (String s : meta.getLore()) {
            for (String line : lines) {
                if (s.replaceAll("§", "&").equalsIgnoreCase(line)) lore.remove(s);
            }
        }

        meta.setLore(lore);
        return this;
    }

    public ItemBuilder spawner(EntityType entityType) {
        BlockStateMeta blockMeta = (BlockStateMeta) meta;
        BlockState blockState = blockMeta.getBlockState();
        CreatureSpawner spawner = (CreatureSpawner) blockState;

        spawner.setSpawnedType(entityType);
        blockMeta.setBlockState(spawner);

        item.setItemMeta(blockMeta);
        meta = item.getItemMeta();
        return this;
    }

    public ItemBuilder setUnbreakable(boolean state) {
        meta.spigot().setUnbreakable(state);
        return this;
    }

    public ItemBuilder setLeatherColor(int red, int green, int blue) {
        LeatherArmorMeta im = (LeatherArmorMeta) meta;
        im.setColor(Color.fromRGB(red, green, blue));
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }


}
