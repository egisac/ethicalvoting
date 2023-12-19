package net.egis.ethicalvoting.inventories;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XItemStack;
import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import net.egis.ethicalvoting.EthicalVoting;
import net.egis.ethicalvoting.data.player.EthicalProfile;
import net.egis.ethicalvoting.lists.PagedList;
import org.bukkit.Material;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardInventory extends Gui {
    public LeaderboardInventory(@Nonnull Player player, @Nonnull String id, String title, int rows) {
        super(player, id, title, rows);
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        List<Integer> slots = new ArrayList<>();
        slots.add(10);
        slots.add(11);
        slots.add(12);
        slots.add(13);
        slots.add(14);
        slots.add(15);
        slots.add(16);
        for (Integer i : slots) {
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta = SkullUtils.setSkullBase64(meta,
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODlhOTk1OTI4MDkwZDg0MmQ0YWZkYjIyOTZmZmUyNGYyZTk0NDI3MjIwNWNlYmE4NDhlZTQwNDZlMDFmMzE2OCJ9fX0=",
                    "89a995928090d842d4afdb2296ffe24f2e944272205ceba848ee4046e01f3168"
            );
            skull.setItemMeta(meta);
            Icon icon = new Icon(skull);
            addItem(icon, i);
        }

        PagedList pList = EthicalVoting.getSelf().getProfiles().getPaged();
        List<?> list = pList.getPage(0, 7);
        for (Object o : list) {
            System.out.println(o);
            EthicalProfile profile = (EthicalProfile) o;
            ItemStack skull = SkullUtils.getSkull(profile.getUuid());
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setDisplayName("§e" + profile.getLastUsername());
            List<String> lore = new ArrayList<>();
            lore.add("§7Votes: §e" + profile.getVotes());
            meta.setLore(lore);
            skull.setItemMeta(meta);
            Icon icon = new Icon(skull);
            addItem(icon, slots.get(list.indexOf(o)));
        }
    }
}
