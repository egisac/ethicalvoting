package net.egis.ethicalvoting.listeners;

import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class FireworkDamageListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onFireworkDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Firework) {
            System.out.println("Firework damage event");
            if (((Firework) e.getDamager()).getFireworkMeta().hasDisplayName() && ((Firework) e.getDamager()).getFireworkMeta().getDisplayName().equalsIgnoreCase("ethdevfwk")) {
                System.out.println("Firework damage event cancelled");
                e.setCancelled(true);
            }
        }
    }

}
