package com.hajmehrsam.iceprivatedrops.listener;

import com.hajmehrsam.iceprivatedrops.IcePrivateDrops;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropItemListener implements Listener {

    private final IcePrivateDrops plugin;

    public PlayerDropItemListener(IcePrivateDrops plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        var player = event.getPlayer();
        if (plugin.getConfigManager().isWorldIgnored(player.getWorld().getName())) {
            return;
        }

        InventoryType topType = player.getOpenInventory().getTopInventory().getType();
        boolean containerOpen = topType != InventoryType.CRAFTING;

        boolean enabled = containerOpen
                ? plugin.getConfigManager().isAffectContainerDrop()
                : plugin.getConfigManager().isAffectPlayerDrop();

        if (!enabled) return;

        plugin.getDropManager().registerPrivateDrop(
                event.getItemDrop(), player.getUniqueId());
    }
}