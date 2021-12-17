package me.buzz.coralmc.oneblockcore.common.structures;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class BukkitSerialization {

    public static String itemStackToBase64(ItemStack item) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeObject(item);
            dataOutput.close();

            return Base64.getEncoder().encodeToString(outputStream.toByteArray());

        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }
    public static ItemStack itemStackFromBase64(String data) throws IOException {
        try {

            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            dataInput.close();

            return (ItemStack) dataInput.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    public static String serializePotionEffect(PotionEffect effect){
        return effect.getType().getName() + ":" + effect.getDuration() + ":" + effect.getAmplifier();
    }
    public static PotionEffect deserializePotionEffect(String effect){
        String[] split = effect.split(":");
        return new PotionEffect(PotionEffectType.getByName(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    }


}

