package com.hajmehrsam.iceprivatedrops.config;

import com.hajmehrsam.iceprivatedrops.IcePrivateDrops;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ConfigManager {

    private final IcePrivateDrops plugin;
    private FileConfiguration itemDropsConfig;

    private boolean checkForUpdates;
    private int privateTime;
    private boolean enableGlow;
    private ChatColor glowColor;
    private ChatColor friendGlowColor;
    
    private boolean affectPlayerDrop;
    private boolean affectDeathDrop;
    private boolean affectBlockBreakDrop;
    private boolean affectMobDrop;
    private boolean affectContainerDrop;
    
    private boolean enableParticles;
    private List<String> ignoredWorlds;

    private String dropSound; private float dropVolume; private float dropPitch;
    private String pickupSound; private float pickupVolume; private float pickupPitch;
    private String publicSound; private float publicVolume; private float publicPitch;

    private String hologramLine1;
    private String hologramLine2;
    private String defaultColor;

    public ConfigManager(IcePrivateDrops plugin) {
        this.plugin = plugin;
        load();
        loadItemDrops();
    }

    public void reload() {
        plugin.reloadConfig();
        load();
        loadItemDrops();
    }

    private void load() {
        FileConfiguration cfg = plugin.getConfig();
        
        checkForUpdates      = cfg.getBoolean("check-for-updates", true);
        
        privateTime          = cfg.getInt("private-time", 30);
        enableGlow           = cfg.getBoolean("enable-glow", true);
        glowColor            = parseColor(cfg.getString("glow-color", "AQUA"));
        friendGlowColor      = parseColor(cfg.getString("friend-glow-color", "GREEN"));
        
        affectPlayerDrop     = cfg.getBoolean("affect-player-drop", true);
        affectDeathDrop      = cfg.getBoolean("affect-death-drop", false);
        affectBlockBreakDrop = cfg.getBoolean("affect-block-break-drop", false);
        affectMobDrop        = cfg.getBoolean("affect-mob-drop", false);
        affectContainerDrop  = cfg.getBoolean("affect-container-drop", false);
        
        enableParticles      = cfg.getBoolean("enable-particles", true);
        ignoredWorlds        = cfg.getStringList("ignored-worlds");

        dropSound   = cfg.getString("sounds.drop-sound", "ENTITY_ITEM_PICKUP");
        dropVolume  = (float) cfg.getDouble("sounds.drop-volume", 0.6);
        dropPitch   = (float) cfg.getDouble("sounds.drop-pitch", 1.2);

        pickupSound = cfg.getString("sounds.pickup-sound", "BLOCK_NOTE_BLOCK_PLING");
        pickupVolume= (float) cfg.getDouble("sounds.pickup-volume", 0.8);
        pickupPitch = (float) cfg.getDouble("sounds.pickup-pitch", 1.0);

        publicSound = cfg.getString("sounds.public-sound", "ENTITY_EXPERIENCE_ORB_PICKUP");
        publicVolume= (float) cfg.getDouble("sounds.public-volume", 0.7);
        publicPitch = (float) cfg.getDouble("sounds.public-pitch", 1.2);
    }

    private void loadItemDrops() {
        File file = new File(plugin.getDataFolder(), "itemdrops.yml");
        if (!file.exists()) {
            plugin.saveResource("itemdrops.yml", false);
        }
        
        itemDropsConfig = YamlConfiguration.loadConfiguration(file);
        
        InputStream defaults = plugin.getResource("itemdrops.yml");
        if (defaults != null) {
            itemDropsConfig.setDefaults(YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaults, StandardCharsets.UTF_8)));
        }
        
        hologramLine1 = itemDropsConfig.getString("hologram-line-1", "%item%");
        hologramLine2 = itemDropsConfig.getString("hologram-line-2", "&aTime: %time%s");
        defaultColor  = itemDropsConfig.getString("default-color", "&f");
    }

    private ChatColor parseColor(String name) {
        if (name == null) return ChatColor.AQUA;
        try {
            return ChatColor.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException ex) {
            plugin.getLogger().warning("Invalid color '" + name + "', defaulting to AQUA.");
            return ChatColor.AQUA;
        }
    }

    public boolean isWorldIgnored(String worldName) {
        return ignoredWorlds.contains(worldName);
    }

    public String getItemColor(String material) {
        return itemDropsConfig.getString("item-colors." + material, defaultColor);
    }

    public boolean isCheckForUpdates()          { return checkForUpdates; }
    public int getPrivateTime()                  { return privateTime; }
    public boolean isEnableGlow()                { return enableGlow; }
    public ChatColor getGlowColor()              { return glowColor; }
    public ChatColor getFriendGlowColor()        { return friendGlowColor; }
    public boolean isAffectPlayerDrop()          { return affectPlayerDrop; }
    public boolean isAffectDeathDrop()           { return affectDeathDrop; }
    public boolean isAffectBlockBreakDrop()      { return affectBlockBreakDrop; }
    public boolean isAffectMobDrop()             { return affectMobDrop; }
    public boolean isAffectContainerDrop()       { return affectContainerDrop; }
    public boolean isEnableParticles()           { return enableParticles; }
    
    public String getHologramLine1()             { return hologramLine1; }
    public String getHologramLine2()             { return hologramLine2; }
    public String getDefaultColor()              { return defaultColor; }

    public String getDropSound()                 { return dropSound; }
    public float getDropVolume()                 { return dropVolume; }
    public float getDropPitch()                  { return dropPitch; }
    
    public String getPickupSound()               { return pickupSound; }
    public float getPickupVolume()               { return pickupVolume; }
    public float getPickupPitch()                { return pickupPitch; }
    
    public String getPublicSound()               { return publicSound; }
    public float getPublicVolume()               { return publicVolume; }
    public float getPublicPitch()                { return publicPitch; }
}