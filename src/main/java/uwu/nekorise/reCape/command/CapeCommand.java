package uwu.nekorise.reCape.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;
import uwu.nekorise.reCape.ReCape;
import uwu.nekorise.reCape.config.LanguageDataStorage;
import uwu.nekorise.reCape.config.MainDataStorage;
import uwu.nekorise.reCape.database.CapesDatabase;
import uwu.nekorise.reCape.entity.CapeEntity;
import uwu.nekorise.reCape.mannequin.MannequinRegistry;
import uwu.nekorise.reCape.mannequin.MannequinSession;
import uwu.nekorise.reCape.util.color.MMessage;

public class CapeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {

        if (args.length < 1) {
            sendUsage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "set" -> handleSet(sender, args);
            case "remove" -> handleRemove(sender, args);
            default -> sendUsage(sender);
        }
        return true;
    }

    private void handleSet(CommandSender sender, String[] args) {

        if (args.length < 2) {
            sender.sendMessage(MMessage.applyColor(LanguageDataStorage.getCapeSetUsage()));
            return;
        }

        String capeType = args[1].toLowerCase();
        Player target;

        if (args.length == 2) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(MMessage.applyColor(LanguageDataStorage.getOnlyForPlayers()));
                return;
            }
            if (!sender.hasPermission("recape.set")) {
                sender.sendMessage(MMessage.applyColor(LanguageDataStorage.getNoPermission()));
                return;
            }
            target = player;
        }

        else {
            if (!sender.hasPermission("recape.setother")) {
                sender.sendMessage(MMessage.applyColor(LanguageDataStorage.getNoPermission()));
                return;
            }
            target = Bukkit.getPlayerExact(args[2]);
            if (target == null) {
                sender.sendMessage(MMessage.applyColor(LanguageDataStorage.getPlayerNotFound()));
                return;
            }
        }

        if (!MainDataStorage.getCapes().contains(capeType)) {
            sender.sendMessage(MMessage.applyColor(LanguageDataStorage.getCapeNotFound()));
            return;
        }

        applyCapeSync(target, capeType);

        Bukkit.getScheduler().runTaskAsynchronously(ReCape.getInstance(), () -> {
            CapesDatabase database = ReCape.getInstance().getDatabase();
            database.saveOrUpdate(new CapeEntity(target.getName(), capeType));
        });

        sender.sendMessage(MMessage.applyColor(LanguageDataStorage.getCapeSetSucc().replace("%player%", target.getName())));
    }

    private void handleRemove(CommandSender sender, String[] args) {

        Player target;

        if (args.length == 1) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(MMessage.applyColor(LanguageDataStorage.getOnlyForPlayers()));
                return;
            }
            if (!sender.hasPermission("recape.remove")) {
                sender.sendMessage(MMessage.applyColor(LanguageDataStorage.getNoPermission()));
                return;
            }
            target = player;
        }
        else {
            if (!sender.hasPermission("recape.removeother")) {
                sender.sendMessage(MMessage.applyColor(LanguageDataStorage.getNoPermission()));
                return;
            }
            target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage(MMessage.applyColor(LanguageDataStorage.getPlayerNotFound()));
                return;
            }
        }

        if (!MannequinRegistry.isRegistered(target.getName())) {
            sender.sendMessage(MMessage.applyColor(LanguageDataStorage.getCapeRemoveFailed()));
            return;
        }

        MannequinRegistry.unregister(target.getName());
        Bukkit.getScheduler().runTaskAsynchronously(ReCape.getInstance(), () -> {
            CapesDatabase database = ReCape.getInstance().getDatabase();
            database.removeByUsername(target.getName());
        });

        sender.sendMessage(MMessage.applyColor(LanguageDataStorage.getCapeRemoveSucc().replace("%player%", target.getName())));
    }

    private void applyCapeSync(Player player, String capeType) {

        String ownerName = player.getName();

        if (MannequinRegistry.isRegistered(ownerName)) {
            MannequinRegistry.unregister(ownerName);
        }

        MannequinSession session = new MannequinSession(player, capeType);
        MannequinRegistry.register(session);
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(MMessage.applyColor(LanguageDataStorage.getCapeUsage()));
    }
}