package com.hajmehrsam.iceprivatedrops.drop;

import com.hajmehrsam.iceprivatedrops.IcePrivateDrops;
import com.hajmehrsam.iceprivatedrops.util.ColorUtil;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class DropManager {

    private static final String TEAM_NAME = "ice_privatedrops_glow";
    private static final String FRIEND_TEAM_NAME = "ice_privatedrops_friend_glow";
    private static final long TICK_INTERVAL_TICKS = 10L; // 0.5 seconds

    private final IcePrivateDrops plugin;
    private final Map<UUID, PrivateDrop> drops = new HashMap<>();
    private final NamespacedKey itemKey;
    private final NamespacedKey holoKey;
    
    private Team glowTeam;
    private Team friendGlowTeam;
    private BukkitTask task;

    public DropManager(IcePrivateDrops plugin) {
        this.plugin = plugin;
        this.itemKey = new NamespacedKey(plugin, "ice_drop_owner");
        this.holoKey = new NamespacedKey(plugin, "ice_holo_item");
        setupTeams();
        startTask();
    }

    private void setupTeams() {
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        if (sb.getTeam(TEAM_NAME) != null) sb.getTeam(TEAM_NAME).unregister();
        if (sb.getTeam(FRIEND_TEAM_NAME) != null) sb.getTeam(FRIEND_TEAM_NAME).unregister();

        glowTeam = sb.registerNewTeam(TEAM_NAME);
        glowTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        glowTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);

        friendGlowTeam = sb.registerNewTeam(FRIEND_TEAM_NAME);
        friendGlowTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        friendGlowTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);

        updateGlowColor();
    }

    public void updateGlowColor() {
        if (glowTeam != null) glowTeam.setColor(plugin.getConfigManager().getGlowColor());
        if (friendGlowTeam != null) friendGlowTeam.setColor(plugin.getConfigManager().getFriendGlowColor());
    }

    private void startTask() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, TICK_INTERVAL_TICKS, TICK_INTERVAL_TICKS);
    }

    private void tick() {
        Iterator<Map.Entry<UUID, PrivateDrop>> it = drops.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, PrivateDrop> entry = it.next();
            PrivateDrop drop = entry.getValue();

            if (drop.isExpired()) {
                makePublicInternal(drop);
                it.remove();
            } else {
                updateHologram(drop);
            }
        }
    }

    public void registerPrivateDrop(@NotNull Item item, @NotNull UUID owner) {
        if (plugin.getConfigManager().isWorldIgnored(item.getWorld().getName())) return;

        long expiry = System.currentTimeMillis() + (plugin.getConfigManager().getPrivateTime() * 1000L);
        
        Set<UUID> viewers = new HashSet<>();
        viewers.add(owner);
        viewers.addAll(plugin.getFriendManager().getFriends(owner));

        String formattedName = getFormattedItemName(item.getItemStack());
        List<ArmorStand> holograms = spawnHologram(item.getLocation().clone().add(0, 0.8, 0), viewers, item);

        PrivateDrop drop = new PrivateDrop(
                item.getUniqueId(), 
                holograms.get(0).getUniqueId(), 
                holograms.get(1).getUniqueId(), 
                owner, viewers, expiry, formattedName
        );
        
        drops.put(item.getUniqueId(), drop);
        
        // Mark the item with PDC so we can identify it if it unloads/reloads
        item.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, owner.toString());
        
        makePrivate(item, viewers);
        updateHologram(drop);
        
        playSoundToViewers(viewers, item.getLocation(), 
                plugin.getConfigManager().getDropSound(), 
                plugin.getConfigManager().getDropVolume(), 
                plugin.getConfigManager().getDropPitch());
    }

    private String getFormattedItemName(ItemStack stack) {
        String material = stack.getType().name();
        String color = plugin.getConfigManager().getItemColor(material);
        
        String itemName;
        if (stack.hasItemMeta()) {
            ItemMeta meta = stack.getItemMeta();
            if (meta.hasDisplayName()) {
                itemName = LegacyComponentSerializer.legacyAmpersand().serialize(meta.displayName());
            } else {
                itemName = color + capitalize(material.toLowerCase().replace('_', ' '));
            }
        } else {
            itemName = color + capitalize(material.toLowerCase().replace('_', ' '));
        }
        return itemName;
    }

    private List<ArmorStand> spawnHologram(Location loc, Set<UUID> viewers, Item item) {
        List<ArmorStand> holos = new ArrayList<>();
        
        ArmorStand holo2 = (ArmorStand) loc.getWorld().spawnEntity(loc, org.bukkit.entity.EntityType.ARMOR_STAND);
        ArmorStand holo1 = (ArmorStand) loc.getWorld().spawnEntity(loc.clone().add(0, 0.25, 0), org.bukkit.entity.EntityType.ARMOR_STAND);
        
        for (ArmorStand holo : new ArmorStand[]{holo1, holo2}) {
            holo.setVisible(false);
            holo.setMarker(true);
            holo.setSmall(true);
            holo.setGravity(false);
            holo.setCustomNameVisible(true);
            // Tag holograms with the Item's UUID so we can clean them up if orphaned
            holo.getPersistentDataContainer().set(holoKey, PersistentDataType.STRING, item.getUniqueId().toString());
            
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!viewers.contains(p.getUniqueId()) && !p.hasPermission("iceprivatedrops.bypass")) {
                    p.hideEntity(plugin, holo);
                }
            }
            holos.add(holo);
        }
        return holos;
    }

    private void updateHologram(PrivateDrop drop) {
        Optional<Item> itemOpt = drop.getItem();
        if (itemOpt.isEmpty()) return; // Chunk is unloaded, wait for reload
        
        Item item = itemOpt.get();
        Optional<ArmorStand> holo1Opt = drop.getHolo1();
        Optional<ArmorStand> holo2Opt = drop.getHolo2();
        if (holo1Opt.isEmpty() || holo2Opt.isEmpty()) return; // Chunk is unloaded
        
        ArmorStand holo1 = holo1Opt.get();
        ArmorStand holo2 = holo2Opt.get();
        
        Location itemLoc = item.getLocation();
        holo2.teleportAsync(itemLoc.clone().add(0, 0.8, 0));
        holo1.teleportAsync(itemLoc.clone().add(0, 1.05, 0));
        
        long remaining = (drop.getExpiryTimestamp() - System.currentTimeMillis()) / 1000;
        
        String line1Text = plugin.getConfigManager().getHologramLine1()
                .replace("%item%", drop.getFormattedItemName())
                .replace("%time%", String.valueOf(remaining));
        
        String line2Text = plugin.getConfigManager().getHologramLine2()
                .replace("%item%", drop.getFormattedItemName())
                .replace("%time%", String.valueOf(remaining));
        
        holo1.customName(ColorUtil.toComponent(line1Text));
        holo2.customName(ColorUtil.toComponent(line2Text));
    }

    public boolean isPrivate(@NotNull UUID itemUUID) { return drops.containsKey(itemUUID); }

    public PrivateDrop getPrivateDrop(@NotNull UUID itemUUID) { return drops.get(itemUUID); }

    public boolean isViewer(@NotNull UUID itemUUID, @NotNull UUID playerUUID) {
        PrivateDrop drop = drops.get(itemUUID);
        return drop != null && drop.getViewers().contains(playerUUID);
    }

    public void remove(@NotNull UUID itemUUID) {
        PrivateDrop removed = drops.remove(itemUUID);
        if (removed != null) cleanupDrop(removed, false);
    }

    public int countActive() { return drops.size(); }

    public int countActiveFor(@NotNull UUID owner) {
        int count = 0;
        for (PrivateDrop drop : drops.values()) if (drop.getOwnerUUID().equals(owner)) count++;
        return count;
    }

    public void handlePlayerJoin(@NotNull Player player) {
        boolean bypass = player.hasPermission("iceprivatedrops.bypass");
        UUID playerUUID = player.getUniqueId();
        for (PrivateDrop drop : drops.values()) {
            if (drop.getViewers().contains(playerUUID) || bypass) continue;
            drop.getItem().ifPresent(item -> player.hideEntity(plugin, item));
            drop.getHolo1().ifPresent(holo -> player.hideEntity(plugin, holo));
            drop.getHolo2().ifPresent(holo -> player.hideEntity(plugin, holo));
        }
    }

    private void makePrivate(@NotNull Item item, @NotNull Set<UUID> viewers) {
        boolean hasFriends = viewers.size() > 1;
        if (plugin.getConfigManager().isEnableGlow()) {
            item.setGlowing(true);
            Team team = hasFriends ? friendGlowTeam : glowTeam;
            try { team.addEntry(item.getUniqueId().toString()); } catch (IllegalStateException ignored) {}
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (viewers.contains(p.getUniqueId()) || p.hasPermission("iceprivatedrops.bypass")) continue;
            p.hideEntity(plugin, item);
        }
    }

    private void makePublicInternal(@NotNull PrivateDrop drop) {
        Optional<Item> itemOpt = drop.getItem();
        if (itemOpt.isPresent()) {
            Item item = itemOpt.get();
            item.setGlowing(false);
            item.getPersistentDataContainer().remove(itemKey);
            for (Player p : Bukkit.getOnlinePlayers()) p.showEntity(plugin, item);
            
            try {
                Sound sound = Sound.valueOf(plugin.getConfigManager().getPublicSound());
                item.getWorld().playSound(item.getLocation(), sound, plugin.getConfigManager().getPublicVolume(), plugin.getConfigManager().getPublicPitch());
            } catch (IllegalArgumentException ignored) {}

            if (plugin.getConfigManager().isEnableParticles()) {
                item.getWorld().spawnParticle(Particle.END_ROD, item.getLocation().clone().add(0, 0.3, 0), 6, 0.2, 0.2, 0.2, 0.02);
            }
        }

        cleanupDrop(drop, true);

        Player owner = Bukkit.getPlayer(drop.getOwnerUUID());
        if (owner != null) owner.sendMessage(plugin.getMessageManager().get("item-public"));
    }

    private void cleanupDrop(@NotNull PrivateDrop drop, boolean becamePublic) {
        Team team = drop.getViewers().size() > 1 ? friendGlowTeam : glowTeam;
        if (team != null) {
            try { team.removeEntry(drop.getItemUUID().toString()); } catch (Exception ignored) {}
        }
        
        drop.getItem().ifPresent(item -> {
            item.setGlowing(false);
            item.getPersistentDataContainer().remove(itemKey);
            if (becamePublic) {
                for (Player p : Bukkit.getOnlinePlayers()) p.showEntity(plugin, item);
            }
        });
        
        drop.getHolo1().ifPresent(holo -> holo.remove());
        drop.getHolo2().ifPresent(holo -> holo.remove());
    }

    private void playSoundToViewers(Set<UUID> viewers, Location loc, String soundName, float volume, float pitch) {
        try {
            Sound sound = Sound.valueOf(soundName);
            for (UUID uuid : viewers) {
                Player p = Bukkit.getPlayer(uuid);
                if (p != null) p.playSound(loc, sound, volume, pitch);
            }
        } catch (IllegalArgumentException ignored) {}
    }

    public void playPickupSound(Set<UUID> viewers, Location loc) {
        playSoundToViewers(viewers, loc, 
                plugin.getConfigManager().getPickupSound(), 
                plugin.getConfigManager().getPickupVolume(), 
                plugin.getConfigManager().getPickupPitch());
    }

    private String capitalize(String str) {
        String[] words = str.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) continue;
            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    public NamespacedKey getItemKey() { return itemKey; }
    public NamespacedKey getHoloKey() { return holoKey; }
    public Team getGlowTeam() { return glowTeam; }
    public Team getFriendGlowTeam() { return friendGlowTeam; }

    public void reload() { updateGlowColor(); }

    public void shutdown() {
        if (task != null) task.cancel();
        for (PrivateDrop drop : drops.values()) {
            drop.getItem().ifPresent(item -> {
                item.setGlowing(false);
                item.getPersistentDataContainer().remove(itemKey);
                for (Player p : Bukkit.getOnlinePlayers()) p.showEntity(plugin, item);
            });
            drop.getHolo1().ifPresent(holo -> holo.remove());
            drop.getHolo2().ifPresent(holo -> holo.remove());
        }
        drops.clear();
        if (glowTeam != null) { try { glowTeam.unregister(); } catch (Exception ignored) {} glowTeam = null; }
        if (friendGlowTeam != null) { try { friendGlowTeam.unregister(); } catch (Exception ignored) {} friendGlowTeam = null; }
    }
}