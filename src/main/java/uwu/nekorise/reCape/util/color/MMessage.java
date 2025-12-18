package uwu.nekorise.reCape.util.color;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.List;
import java.util.stream.Collectors;

public class MMessage
{
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    /**
     * Applies color and formatting to the given text.
     *
     * @param text The text with MiniMessage format, e.g., "<#77b1de>Colored Text!"
     * @return Component formatted text for use in Minecraft.
     */
    public static Component applyColor(String text) {
        return miniMessage.deserialize(text);
    }

    /**
     * Applies color and formatting to the given text with ampersand (&) format support.
     *
     * @param text The text with ampersand format, e.g., "&aColored Text!"
     * @return Component formatted text for use in Minecraft.
     */
    public static Component applyColorWithAmpersand(String text) {
        return miniMessage.deserialize(HEX.applyColor(text));
    }

    /**
     * Applies color and formatting to a list of strings.
     *
     * @param texts A list of texts with MiniMessage format, e.g., ["<#77b1de>Text1", "<#34a853>Text2"]
     * @return A list of formatted Components for use in Minecraft.
     */
    public static List<Component> applyListColor(List<String> texts) {
        return texts.stream()
                .map(MMessage::applyColor)
                .collect(Collectors.toList());
    }

    public static String getText(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }
}
