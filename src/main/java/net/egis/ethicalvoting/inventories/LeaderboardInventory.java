package net.egis.ethicalvoting.inventories;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XItemStack;
import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import net.egis.ethicalvoting.EthicalVoting;
import net.egis.ethicalvoting.data.player.EthicalProfile;
import net.egis.ethicalvoting.lists.PagedList;
import net.egis.ethicalvoting.utils.Translate;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Skull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nonnull;
import java.util.List;

public class LeaderboardInventory extends Gui {
    private int page = 0;
    public LeaderboardInventory(@Nonnull Player player, @Nonnull String id, String title, int rows, int page) {
        super(player, id, title, rows);
        this.page = page;
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        EthicalVoting plugin = EthicalVoting.getSelf();

        List<Integer> slots = plugin.getConfig().getIntegerList("leaderboard.gui.leaderboard_spots");

        for (Integer i : slots) {
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta = SkullUtils.setSkullBase64(
                    meta,
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODlhOTk1OTI4MDkwZDg0MmQ0YWZkYjIyOTZmZmUyNGYyZTk0NDI3MjIwNWNlYmE4NDhlZTQwNDZlMDFmMzE2OCJ9fX0=",
                    "89a995928090d842d4afdb2296ffe24f2e944272205ceba848ee4046e01f3168"
            );
            meta.setDisplayName(Translate.translate(player, plugin.getConfig().getString("leaderboard.gui.entry_empty.name")));
            meta.setLore(Translate.translate(player, plugin.getConfig().getStringList("leaderboard.gui.entry_empty.lore")));
            skull.setItemMeta(meta);
            Icon icon = new Icon(skull);
            addItem(icon, i);
        }

        PagedList pList = EthicalVoting.getSelf().getProfiles().getPaged();
        List<?> list = pList.getPage(page, slots.size());
        for (Object o : list) {
            System.out.println(o);
            EthicalProfile profile = (EthicalProfile) o;
            ItemStack skull = SkullUtils.getSkull(profile.getUuid());
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            String name = Translate.translate(player, plugin.getConfig().getString("leaderboard.gui.entry.name"));
            name = name.replace("%player%", profile.getLastUsername());
            name = name.replace("%votes%", String.valueOf(profile.getVotes()));
            name = name.replace("%position%", String.valueOf((list.indexOf(o) + 1) * (page + 1)));
            meta.setDisplayName(name);
            List<String> lore = Translate.translate(player, plugin.getConfig().getStringList("leaderboard.gui.entry.lore"));
            lore.replaceAll(s -> s.replace("%player%", profile.getLastUsername()));
            lore.replaceAll(s -> s.replace("%votes%", String.valueOf(profile.getVotes())));
            lore.replaceAll(s -> s.replace("%position%", String.valueOf((list.indexOf(o) + 1) * (page + 1))));
            meta.setLore(lore);
            meta.setLore(lore);
            skull.setItemMeta(meta);
            Icon icon = new Icon(skull);
            addItem(icon, slots.get(list.indexOf(o)));
        }

        // Other items
        ConfigurationSection otherItems = plugin.getConfig().getConfigurationSection("leaderboard.gui.items");
        for (String key : otherItems.getKeys(false)) {
            ConfigurationSection item = otherItems.getConfigurationSection(key);
            ItemStack i = new ItemStack(Material.valueOf(item.getString("material")));
            ItemMeta m = i.getItemMeta();
            String name = Translate.translate(player, item.getString("name"));
            name = name.replace("%page%", String.valueOf(page + 1));
            name = name.replace("%max_pages%", String.valueOf(pList.getPages(slots.size())));
            m.setDisplayName(name);
            List<String> lore = Translate.translate(player, item.getStringList("lore"));
            lore.replaceAll(s -> s.replace("%page%", String.valueOf(page + 1)));
            lore.replaceAll(s -> s.replace("%max_pages%", String.valueOf(pList.getPages(slots.size()))));
            m.setLore(lore);
            i.setItemMeta(m);
            if (item.contains("amount", false)) {
                i.setAmount(item.getInt("amount"));
            }
            if (item.contains("action", false)) {
                String action = item.getString("action");
                if (action.equalsIgnoreCase("next_page")) {
                    int totalPages = pList.getPages(slots.size());
                    Icon icon = new Icon(i);
                    if (!(page + 1 >= totalPages)) {
                        icon.onClick((e) -> {
                            LeaderboardInventory leaderboardInventory = new LeaderboardInventory(player, "leaderboard", "Leaderboard", 3, page + 1);
                            leaderboardInventory.open();
                        });
                    }
                    if (item.contains("slot", false)) {
                        addItem(icon, item.getInt("slot"));
                    } else {
                        addItem(icon);
                    }
                } else if (action.equalsIgnoreCase("previous_page")) {
                    Icon icon = new Icon(i);
                    if (!(page - 1 < 0)) {
                        icon.onClick((e) -> {
                            LeaderboardInventory leaderboardInventory = new LeaderboardInventory(player, "leaderboard", "Leaderboard", 3, page - 1);
                            leaderboardInventory.open();
                        });
                    }

                    if (item.contains("slot", false)) {
                        addItem(icon, item.getInt("slot"));
                    } else {
                        addItem(icon);
                    }
                }
            } else {
                if (item.contains("slot", false)) {
                    addItem(new Icon(i), item.getInt("slot"));
                } else {
                    addItem(new Icon(i));
                }
            }
        }
    }
}
