package com.hajmehrsam.iceprivatedrops.util;

import com.hajmehrsam.iceprivatedrops.IcePrivateDrops;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {

    private final IcePrivateDrops plugin;
    private final int resourceId;
    private String latestVersion;

    public UpdateChecker(IcePrivateDrops plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void checkAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    latestVersion = reader.readLine();
                }
                connection.disconnect();

                if (latestVersion != null && !latestVersion.equalsIgnoreCase(plugin.getDescription().getVersion())) {
                    plugin.setUpdateAvailable(true, latestVersion);
                    plugin.getLogger().info("A new update is available! (v" + latestVersion + ")");
                    plugin.getLogger().info("Download it here: https://www.spigotmc.org/resources/" + resourceId + "/");
                } else {
                    plugin.getLogger().info("You are running the latest version of IcePrivateDrops.");
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Could not check for updates: " + e.getMessage());
            }
        });
    }
}