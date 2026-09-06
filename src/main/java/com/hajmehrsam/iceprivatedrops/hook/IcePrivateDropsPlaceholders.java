package com.hajmehrsam.iceprivatedrops.hook;

import com.hajmehrsam.iceprivatedrops.IcePrivateDrops;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
public class IcePrivateDropsPlaceholders extends PlaceholderExpansion {

    private final IcePrivateDrops plugin;

    public IcePrivateDropsPlaceholders(IcePrivateDrops plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "iceprivatedrops";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Hajmehrsam";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("total_active")) {
            return String.valueOf(plugin.getDropManager().countActive());
        }
        if (params.equalsIgnoreCase("active_drops")) {
            if (player == null) return "0";
            return String.valueOf(plugin.getDropManager().countActiveFor(player.getUniqueId()));
        }
        return null;
    }
}