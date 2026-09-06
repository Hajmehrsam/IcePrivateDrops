package com.hajmehrsam.iceprivatedrops.listener;

import com.hajmehrsam.iceprivatedrops.IcePrivateDrops;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerDeathListener implements Listener {

    private final IcePrivateDrops plugin;

    public PlayerDeathListener(IcePrivateDrops plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        if (!plugin.getConfigManager().isAffectDeathDrop()) return;

        Player player = event.getEntity();
        if (plugin.getConfigManager().isWorldIgnored(player.getWorld().getName())) return;
        if (event.getKeepInventory()) return;

        List<ItemStack> drops = new ArrayList<>(event.getDrops());
        if (drops.isEmpty()) return;

        event.getDrops().clear();

        final Location loc = player.getLocation().clone();
        final UUID owner = player.getUniqueId();

        new BukkitRunnable() {
            @Override
            public void run() {
                for (ItemStack stack : drops) {
                    if (stack == null || stack.getType().isAir()) continue;
                    Item item = loc.getWorld().dropItemNaturally(loc, stack);
                    plugin.getDropManager().registerPrivateDrop(item, owner);
                }
            }
        }.runTask(plugin);
    }
}