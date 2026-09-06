package com.hajmehrsam.iceprivatedrops.drop;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class PrivateDrop {

    private final UUID itemUUID;
    private final UUID holo1UUID;
    private final UUID holo2UUID;
    private final UUID ownerUUID;
    private final Set<UUID> viewers;
    private final long expiryTimestamp;
    private final String formattedItemName;

    public PrivateDrop(UUID itemUUID, UUID holo1UUID, UUID holo2UUID, UUID ownerUUID, Set<UUID> viewers, long expiryTimestamp, String formattedItemName) {
        this.itemUUID = itemUUID;
        this.holo1UUID = holo1UUID;
        this.holo2UUID = holo2UUID;
        this.ownerUUID = ownerUUID;
        this.viewers = viewers;
        this.expiryTimestamp = expiryTimestamp;
        this.formattedItemName = formattedItemName;
    }

    public UUID getItemUUID() { return itemUUID; }
    public UUID getOwnerUUID() { return ownerUUID; }
    public Set<UUID> getViewers() { return viewers; }
    public long getExpiryTimestamp() { return expiryTimestamp; }
    public String getFormattedItemName() { return formattedItemName; }

    public Optional<Item> getItem() {
        if (itemUUID == null) return Optional.empty();
        var entity = Bukkit.getEntity(itemUUID);
        if (entity instanceof Item item && !item.isDead() && item.isValid()) {
            return Optional.of(item);
        }
        return Optional.empty();
    }

    public Optional<ArmorStand> getHolo1() {
        if (holo1UUID == null) return Optional.empty();
        var entity = Bukkit.getEntity(holo1UUID);
        if (entity instanceof ArmorStand holo && !holo.isDead() && holo.isValid()) {
            return Optional.of(holo);
        }
        return Optional.empty();
    }

    public Optional<ArmorStand> getHolo2() {
        if (holo2UUID == null) return Optional.empty();
        var entity = Bukkit.getEntity(holo2UUID);
        if (entity instanceof ArmorStand holo && !holo.isDead() && holo.isValid()) {
            return Optional.of(holo);
        }
        return Optional.empty();
    }

    public boolean isExpired() { return System.currentTimeMillis() >= expiryTimestamp; }
}