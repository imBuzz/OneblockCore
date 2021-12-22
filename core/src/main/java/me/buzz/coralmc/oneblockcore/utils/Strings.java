package me.buzz.coralmc.oneblockcore.utils;

import org.bukkit.ChatColor;

public class Strings {

    public static String translate(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
