package net.egis.ethicalvoting.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.List;

public class PAPIUtils {

    public String implementPAPIPlaceholders(Player a, String s) {
        s = PlaceholderAPI.setPlaceholders(a, s);
        return s;
    }

    public List<String> implementPAPIPlaceholders(Player a, List<String> s) {
        s.replaceAll(text -> PlaceholderAPI.setPlaceholders(a, text));
        return s;
    }

}
