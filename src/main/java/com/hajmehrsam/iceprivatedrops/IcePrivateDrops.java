package com.hajmehrsam.iceprivatedrops;

import com.hajmehrsam.iceprivatedrops.command.IcePrivateDropsCommand;
import com.hajmehrsam.iceprivatedrops.config.ConfigManager;
import com.hajmehrsam.iceprivatedrops.drop.DropManager;
import com.hajmehrsam.iceprivatedrops.friend.FriendManager;
import com.hajmehrsam.iceprivatedrops.hook.IcePrivateDropsPlaceholders;
import com.hajmehrsam.iceprivatedrops.listener.*;
import com.hajmehrsam.iceprivatedrops.message.MessageManager;
import com.hajmehrsam.iceprivatedrops.util.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class IcePrivateDrops extends JavaPlugin {

    private ConfigManager configManager;
    private MessageManager messageManager;
    private DropManager dropManager;
    private FriendManager friendManager;

    private boolean updateAvailable = false;
    private String latestVersion = "";

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml", false);
        saveResource("itemdrops.yml", false);

        this.configManager = new ConfigManager(this);
        this.messageManager = new MessageManager(this);
        this.friendManager = new FriendManager(this);
        this.dropManager = new DropManager(this);

        registerListeners();
        registerCommands();
        hookPlaceholderAPI();

        if (configManager.isCheckForUpdates()) {
            new UpdateChecker(this, 137354).checkAsync();
        }

        getLogger().info("IcePrivateDrops v" + getDescription().getVersion() + " enabled successfully.");
    }

    @Override
    public void onDisable() {
        if (dropManager != null) {
            dropManager.shutdown();
        }
        if (friendManager != null) {
            friendManager.shutdown();
        }
        getLogger().info("IcePrivateDrops disabled.");
    }

    private void registerListeners() {
        var pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerDropItemListener(this), this);
        pm.registerEvents(new PlayerDeathListener(this), this);
        pm.registerEvents(new BlockBreakListener(this), this);
        pm.registerEvents(new EntityDeathListener(this), this);
        pm.registerEvents(new ItemEventListener(this), this);
        pm.registerEvents(new PlayerJoinListener(this), this);
        pm.registerEvents(new EntitiesLoadListener(this), this);
    }

    private void registerCommands() {
        var cmd = getCommand("iceprivatedrops");
        if (cmd == null) {
            getLogger().severe("Command 'iceprivatedrops' is missing from plugin.yml!");
            return;
        }
        IcePrivateDropsCommand executor = new IcePrivateDropsCommand(this);
        cmd.setExecutor(executor);
        cmd.setTabCompleter(executor);
    }

    private void hookPlaceholderAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new IcePrivateDropsPlaceholders(this).register();
            getLogger().info("Hooked into PlaceholderAPI.");
        }
    }
    public void reloadAll() {
        reloadConfig();
        configManager.reload();
        messageManager.reload();
        dropManager.reload();
    }

    public void setUpdateAvailable(boolean available, String version) {
        this.updateAvailable = available;
        this.latestVersion = version;
    }

    public boolean isUpdateAvailable() { return updateAvailable; }
    public String getLatestVersion() { return latestVersion; }

    public ConfigManager getConfigManager() { return configManager; }
    public MessageManager getMessageManager() { return messageManager; }
    public DropManager getDropManager() { return dropManager; }
    public FriendManager getFriendManager() { return friendManager; }
}