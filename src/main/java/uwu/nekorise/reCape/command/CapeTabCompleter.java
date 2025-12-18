package uwu.nekorise.reCape.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uwu.nekorise.reCape.config.MainDataStorage;

import java.util.List;

public class CapeTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        List<String> firstArguments = List.of("set", "remove");
        List<String> capeTypes = MainDataStorage.getCapes();

        switch (args.length) {
            case 1 -> {
                return firstArguments;
            }
            case 2 -> {
                if (args[0].equalsIgnoreCase("set")) {
                    return capeTypes;
                }
            }
            default -> { return null; }
        }
        return null;
    }
}
