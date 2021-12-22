package me.buzz.coralmc.oneblockcore.players;

import com.google.common.collect.Lists;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.buzz.coralmc.oneblockcore.server.redis.adapters.ItemPair;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.List;

@Entity("player_datas")
@NoArgsConstructor
@AllArgsConstructor
public class PlayerData {

    @Id private String name;

    private List<ItemPair> inventory, enderchest;
    private double health;
    private int hungerLevel, expLevel;
    private float exp;
    private List<PotionEffect> potionEffects;

    public static PlayerData of(Player player) {
        return new PlayerData(player.getName(), ItemPair.from(player.getInventory()), ItemPair.from(player.getEnderChest()), player.getHealth(),
                player.getFoodLevel(), player.getLevel(), player.getExp(),
                Lists.newArrayList(player.getActivePotionEffects()));
    }

    public void load(Player player){
        player.getInventory().clear();
        player.getEnderChest().clear();

        if (inventory != null){
            for (int i = 0; i < inventory.size(); i++) {
                ItemPair invPair = inventory.get(i);
                player.getInventory().setItem(invPair.getSlot(), invPair.getItem());

                if (enderchest != null) {
                    if (i < enderchest.size()){
                        ItemPair enderPair = enderchest.get(i);
                        player.getEnderChest().setItem(enderPair.getSlot(), enderPair.getItem());
                    }
                }
            }
        }

        player.setHealth(health);
        player.setFoodLevel(hungerLevel);

        player.setLevel(expLevel);
        player.setExp(exp);

        for (PotionEffect activePotionEffect : player.getActivePotionEffects()) player.removePotionEffect(activePotionEffect.getType());
        if (potionEffects != null) {
            for (PotionEffect potionEffect : potionEffects) {
                if (potionEffect == null || potionEffect.getType() == null) continue;
                player.addPotionEffect(potionEffect);
            }
        }
    }
}
