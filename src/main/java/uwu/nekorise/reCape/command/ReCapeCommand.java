package uwu.nekorise.reCape.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import uwu.nekorise.reCape.config.ConfigManager;
import uwu.nekorise.reCape.config.LanguageDataStorage;
import uwu.nekorise.reCape.recourcepack.ResourcepackBuilder;
import uwu.nekorise.reCape.util.color.MMessage;

public class ReCapeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {

        if (args.length < 1) {
            sendUsage(sender);
            return true;
        }

        switch (args[0]) {
            case "repack" -> {
                if (!sender.hasPermission("recape.repack")) {
                    sender.sendMessage(MMessage.applyColor(LanguageDataStorage.getNoPermission()));
                    return false;
                }
                repack(sender);
            }
            case "reload" -> {
                if (!sender.hasPermission("recape.repack")) {
                    sender.sendMessage(MMessage.applyColor(LanguageDataStorage.getNoPermission()));
                    return false;
                }
                reload(sender);
            }
            default -> {
                sendUsage(sender);
                return true;
            }
        }
        return false;
    }

    private void repack(CommandSender sender) {
        ResourcepackBuilder rpBuilder = new ResourcepackBuilder();
        rpBuilder.buildAsync(sender);
    }

    private void reload(CommandSender sender){
        ConfigManager.loadConfig();
        sender.sendMessage(MMessage.applyColor(LanguageDataStorage.getReCapeReloadSucc()));
    }

    private void sendUsage(CommandSender sender){
        sender.sendMessage(MMessage.applyColor(LanguageDataStorage.getReCapeUsage()));
    }
}
