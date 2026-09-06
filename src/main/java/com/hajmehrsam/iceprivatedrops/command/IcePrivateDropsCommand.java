package com.hajmehrsam.iceprivatedrops.command;

import com.hajmehrsam.iceprivatedrops.IcePrivateDrops;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class IcePrivateDropsCommand implements CommandExecutor, TabCompleter {

    private static final String PERM_ADMIN = "iceprivatedrops.admin";

    private final IcePrivateDrops plugin;

    public IcePrivateDropsCommand(IcePrivateDrops plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            sender.sendMessage(plugin.getMessageManager().get("help"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                if (!sender.hasPermission(PERM_ADMIN)) {
                    sender.sendMessage(plugin.getMessageManager().get("no-permission"));
                    return true;
                }
                sender.sendMessage(plugin.getMessageManager().get("reload"));
                plugin.reloadAll();
                sender.sendMessage(plugin.getMessageManager().get("config-reloaded"));
                return true;
            }
            case "help" -> {
                sender.sendMessage(plugin.getMessageManager().get("help"));
                return true;
            }
            case "friend" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("This command is for players only.");
                    return true;
                }
                handleFriendCommand(player, args);
                return true;
            }
            default -> {
                sender.sendMessage(plugin.getMessageManager().get("unknown-command"));
                return true;
            }
        }
    }

    private void handleFriendCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(plugin.getMessageManager().get("friend-usage"));
            return;
        }

        switch (args[1].toLowerCase()) {
            case "invite" -> {
                if (args.length < 3) {
                    player.sendMessage(plugin.getMessageManager().get("friend-usage"));
                    return;
                }
                Player target = Bukkit.getPlayerExact(args[2]);
                if (target == null) {
                    player.sendMessage(plugin.getMessageManager().get("friend-not-online"));
                    return;
                }
                plugin.getFriendManager().invite(player, target);
            }
            case "accept" -> plugin.getFriendManager().accept(player);
            case "deny" -> plugin.getFriendManager().deny(player);
            default -> player.sendMessage(plugin.getMessageManager().get("friend-usage"));
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> base = new java.util.ArrayList<>(List.of("help"));
            if (sender.hasPermission(PERM_ADMIN)) base.add("reload");
            if (sender instanceof Player) base.add("friend");
            return base.stream().filter(s -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("friend")) {
            return Arrays.asList("invite", "accept", "deny").stream()
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("friend") && args[1].equalsIgnoreCase("invite")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}