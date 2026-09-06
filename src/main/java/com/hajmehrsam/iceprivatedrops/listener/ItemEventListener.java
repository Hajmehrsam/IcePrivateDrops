package com.hajmehrsam.iceprivatedrops.listener;

import com.hajmehrsam.iceprivatedrops.IcePrivateDrops;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ItemEventListener implements Listener {

    private final IcePrivateDrops plugin;

    public ItemEventListener(IcePrivateDrops plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDespawn(ItemDespawnEvent event) {
        plugin.getDropManager().remove(event.getEntity().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onMerge(ItemMergeEvent event) {
        UUID source = event.getEntity().getUniqueId();
        UUID target = event.getTarget().getUniqueId();
        if (plugin.getDropManager().isPrivate(source) || plugin.getDropManager().isPrivate(target)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityPickup(EntityPickupItemEvent event) {
        UUID itemUUID = event.getItem().getUniqueId();
        if (!plugin.getDropManager().isPrivate(itemUUID)) return;

        if (event.getEntity() instanceof Player player) {
            boolean isViewer = plugin.getDropManager().isViewer(itemUUID, player.getUniqueId());
            boolean bypass = player.hasPermission("iceprivatedrops.bypass");

            if (!isViewer && !bypass) {
                event.setCancelled(true);
                return;
            }

            // Play pickup sound to viewers
            Set<UUID> viewers = new HashSet<>();
            viewers.add(player.getUniqueId());
            plugin.getDropManager().playPickupSound(viewers, event.getItem().getLocation());
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityPickupCleanup(EntityPickupItemEvent event) {
        UUID itemUUID = event.getItem().getUniqueId();
        if (plugin.getDropManager().isPrivate(itemUUID)) {
            plugin.getDropManager().remove(itemUUID);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryPickup(InventoryPickupItemEvent event) {
        if (plugin.getDropManager().isPrivate(event.getItem().getUniqueId())) {
            event.setCancelled(true);
        }
    }
}