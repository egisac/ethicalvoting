package net.egis.ethicalvoting.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class Translate {

    public static String translate(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> translate(List<String> ls) {
        ls.replaceAll(Translate::translate);
        return ls;
    }

    public static String translate(Player a, String s) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            PAPIUtils utils = new PAPIUtils();
            s = utils.implementPAPIPlaceholders(a, s);
        }
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> translate(Player a, List<String> ls) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            PAPIUtils utils = new PAPIUtils();
            ls = utils.implementPAPIPlaceholders(a, ls);
        }
        ls.replaceAll(Translate::translate);
        return ls;
    }

}
