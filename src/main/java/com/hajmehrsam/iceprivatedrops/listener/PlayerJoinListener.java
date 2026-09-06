package com.hajmehrsam.iceprivatedrops.listener;

import com.hajmehrsam.iceprivatedrops.IcePrivateDrops;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final IcePrivateDrops plugin;

    public PlayerJoinListener(IcePrivateDrops plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        plugin.getDropManager().handlePlayerJoin(event.getPlayer());

        // Notify admins of updates
        if (plugin.isUpdateAvailable() && event.getPlayer().hasPermission("iceprivatedrops.admin")) {
            event.getPlayer().sendMessage(plugin.getMessageManager().get("update-available", "version", plugin.getLatestVersion()));
        }
    }
}