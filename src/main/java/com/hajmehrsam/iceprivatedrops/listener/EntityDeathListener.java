package com.hajmehrsam.iceprivatedrops.listener;

import com.hajmehrsam.iceprivatedrops.IcePrivateDrops;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EntityDeathListener implements Listener {

    private final IcePrivateDrops plugin;

    public EntityDeathListener(IcePrivateDrops plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (!plugin.getConfigManager().isAffectMobDrop()) return;

        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) return; // handled by PlayerDeathListener
        if (plugin.getConfigManager().isWorldIgnored(entity.getWorld().getName())) return;

        Player killer = entity.getKiller();
        if (killer == null) return; // no owner

        List<ItemStack> drops = new ArrayList<>(event.getDrops());
        if (drops.isEmpty()) return;

        event.getDrops().clear();

        final Location loc = entity.getLocation().clone();
        final UUID owner = killer.getUniqueId();

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