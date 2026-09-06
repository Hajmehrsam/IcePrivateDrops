package com.hajmehrsam.iceprivatedrops.listener;

import com.hajmehrsam.iceprivatedrops.IcePrivateDrops;
import com.hajmehrsam.iceprivatedrops.drop.PrivateDrop;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Team;

import java.util.UUID;

public class EntitiesLoadListener implements Listener {

    private final IcePrivateDrops plugin;

    public EntitiesLoadListener(IcePrivateDrops plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntitiesLoad(EntitiesLoadEvent event) {
        for (Entity entity : event.getEntities()) {
            if (entity instanceof ArmorStand holo) {
                if (holo.getPersistentDataContainer().has(plugin.getDropManager().getHoloKey(), PersistentDataType.STRING)) {
                    String itemUUIDStr = holo.getPersistentDataContainer().get(plugin.getDropManager().getHoloKey(), PersistentDataType.STRING);
                    UUID itemUUID = UUID.fromString(itemUUIDStr);
                    
                    if (plugin.getDropManager().isPrivate(itemUUID)) {
                        PrivateDrop drop = plugin.getDropManager().getPrivateDrop(itemUUID);
                        if (drop != null) {
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (!drop.getViewers().contains(p.getUniqueId()) && !p.hasPermission("iceprivatedrops.bypass")) {
                                    p.hideEntity(plugin, holo);
                                }
                            }
                        }
                    } else {
                        holo.remove();
                    }
                }
            } else if (entity instanceof Item item) {
                if (item.getPersistentDataContainer().has(plugin.getDropManager().getItemKey(), PersistentDataType.STRING)) {
                    UUID itemUUID = item.getUniqueId();
                    if (plugin.getDropManager().isPrivate(itemUUID)) {
                        PrivateDrop drop = plugin.getDropManager().getPrivateDrop(itemUUID);
                        if (drop != null) {
                            item.setGlowing(true);
                            Team team = drop.getViewers().size() > 1 ? plugin.getDropManager().getFriendGlowTeam() : plugin.getDropManager().getGlowTeam();
                            try { team.addEntry(itemUUID.toString()); } catch (Exception ignored) {}
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (!drop.getViewers().contains(p.getUniqueId()) && !p.hasPermission("iceprivatedrops.bypass")) {
                                    p.hideEntity(plugin, item);
                                }
                            }
                        }
                    } else {
                        item.setGlowing(false);
                        item.getPersistentDataContainer().remove(plugin.getDropManager().getItemKey());
                        for (Player p : Bukkit.getOnlinePlayers()) p.showEntity(plugin, item);
                    }
                }
            }
        }
    }
}