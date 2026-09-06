package com.hajmehrsam.iceprivatedrops.message;

import com.hajmehrsam.iceprivatedrops.IcePrivateDrops;
import com.hajmehrsam.iceprivatedrops.util.ColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MessageManager {

    private final IcePrivateDrops plugin;
    private FileConfiguration messages;
    private Component prefix;

    public MessageManager(IcePrivateDrops plugin) {
        this.plugin = plugin;
        load();
    }

    public void reload() {
        load();
    }

    private void load() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        messages = YamlConfiguration.loadConfiguration(file);

        InputStream defaults = plugin.getResource("messages.yml");
        if (defaults != null) {
            YamlConfiguration def = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaults, StandardCharsets.UTF_8));
            messages.setDefaults(def);
        }

        prefix = ColorUtil.toComponent(messages.getString("prefix", ""));
    }

    public Component get(String path, String... replacements) {
        if (!messages.contains(path)) {
            return Component.empty();
        }
        String msg = messages.getString(path, "");
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            msg = msg.replace("%" + replacements[i] + "%", replacements[i + 1]);
        }
        Component message = ColorUtil.toComponent(msg);
        return prefix.append(message);
    }

    public Component getPrefix() {
        return prefix;
    }
}