package com.hajmehrsam.iceprivatedrops.friend;

import com.hajmehrsam.iceprivatedrops.IcePrivateDrops;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class FriendManager {

    private final IcePrivateDrops plugin;
    private final Map<UUID, Set<UUID>> friendsMap = new HashMap<>();
    private final Map<UUID, FriendRequest> pendingRequests = new HashMap<>();
    private BukkitTask task;

    public FriendManager(IcePrivateDrops plugin) {
        this.plugin = plugin;
        startTask();
    }

    private void startTask() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 20L, 20L);
    }

    private void tick() {
        long now = System.currentTimeMillis();
        pendingRequests.entrySet().removeIf(entry -> {
            FriendRequest req = entry.getValue();
            if (req.isExpired()) {
                Player sender = Bukkit.getPlayer(req.getSender());
                Player target = Bukkit.getPlayer(req.getTarget());
                if (sender != null) sender.sendMessage(plugin.getMessageManager().get("friend-expired-sender", "target", target != null ? target.getName() : "Unknown"));
                if (target != null) target.sendMessage(plugin.getMessageManager().get("friend-expired-target", "sender", sender != null ? sender.getName() : "Unknown"));
                return true;
            }
            return false;
        });
    }

    public void invite(Player sender, Player target) {
        if (sender.getUniqueId().equals(target.getUniqueId())) {
            sender.sendMessage(plugin.getMessageManager().get("friend-self"));
            return;
        }
        if (areFriends(sender.getUniqueId(), target.getUniqueId())) {
            sender.sendMessage(plugin.getMessageManager().get("friend-already-friends"));
            return;
        }

        long expiry = System.currentTimeMillis() + 30000L;
        pendingRequests.put(target.getUniqueId(), new FriendRequest(sender.getUniqueId(), target.getUniqueId(), expiry));
        sender.sendMessage(plugin.getMessageManager().get("friend-invite-sent", "target", target.getName()));
        target.sendMessage(plugin.getMessageManager().get("friend-invite-received", "sender", sender.getName()));
    }

    public void accept(Player target) {
        FriendRequest req = pendingRequests.remove(target.getUniqueId());
        if (req == null) {
            target.sendMessage(plugin.getMessageManager().get("friend-no-request"));
            return;
        }
        addFriend(req.getSender(), req.getTarget());
        Player sender = Bukkit.getPlayer(req.getSender());
        target.sendMessage(plugin.getMessageManager().get("friend-accept", "target", sender != null ? sender.getName() : "Unknown"));
        if (sender != null) sender.sendMessage(plugin.getMessageManager().get("friend-accept-notify", "target", target.getName()));
    }

    public void deny(Player target) {
        FriendRequest req = pendingRequests.remove(target.getUniqueId());
        if (req == null) {
            target.sendMessage(plugin.getMessageManager().get("friend-no-request"));
            return;
        }
        Player sender = Bukkit.getPlayer(req.getSender());
        target.sendMessage(plugin.getMessageManager().get("friend-deny", "sender", sender != null ? sender.getName() : "Unknown"));
        if (sender != null) sender.sendMessage(plugin.getMessageManager().get("friend-request-denied", "target", target.getName()));
    }

    public void addFriend(UUID p1, UUID p2) {
        friendsMap.computeIfAbsent(p1, k -> new HashSet<>()).add(p2);
        friendsMap.computeIfAbsent(p2, k -> new HashSet<>()).add(p1);
    }

    public boolean areFriends(UUID p1, UUID p2) {
        return friendsMap.getOrDefault(p1, new HashSet<>()).contains(p2);
    }

    public Set<UUID> getFriends(UUID player) {
        return friendsMap.getOrDefault(player, new HashSet<>());
    }

    public void shutdown() {
        if (task != null) task.cancel();
        pendingRequests.clear();
        friendsMap.clear();
    }
}