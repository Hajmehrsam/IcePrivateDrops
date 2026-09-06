package com.hajmehrsam.iceprivatedrops.listener;

import com.hajmehrsam.iceprivatedrops.IcePrivateDrops;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;

import java.util.UUID;

public class BlockBreakListener implements Listener {

    private final IcePrivateDrops plugin;

    public BlockBreakListener(IcePrivateDrops plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockDrop(BlockDropItemEvent event) {
        if (!plugin.getConfigManager().isAffectBlockBreakDrop()) return;
        if (plugin.getConfigManager().isWorldIgnored(event.getBlock().getWorld().getName())) return;
        if (event.getPlayer() == null) return;

        UUID owner = event.getPlayer().getUniqueId();
        for (Item item : event.getItems()) {
            plugin.getDropManager().registerPrivateDrop(item, owner);
        }
    }
}