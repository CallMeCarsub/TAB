package me.neznamy.tab.shared.hook;

import me.neznamy.tab.shared.chat.*;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Class for Adventure component conversion.
 */
public class AdventureHook {

    /** Array of all 32 possible decoration combinations for fast access */
    private static final EnumSet<TextDecoration>[] decorations = loadDecorations();

    /**
     * Loads decoration array.
     *
     * @return  Decoration array with all possible options
     */
    @SuppressWarnings("unchecked")
    private static EnumSet<TextDecoration>[] loadDecorations() {
        EnumSet<TextDecoration>[] decorations = new EnumSet[32];
        for (int i=0; i<32; i++) {
            EnumSet<TextDecoration> set = EnumSet.noneOf(TextDecoration.class);
            if ((i & 1) > 0) set.add(TextDecoration.BOLD);
            if ((i & 2) > 0) set.add(TextDecoration.ITALIC);
            if ((i & 4) > 0) set.add(TextDecoration.OBFUSCATED);
            if ((i & 8) > 0) set.add(TextDecoration.STRIKETHROUGH);
            if ((i & 16) > 0) set.add(TextDecoration.UNDERLINED);
            decorations[i] = set;
        }
        return decorations;
    }

    /**
     * Converts component to adventure component
     *
     * @param   component
     *          Component to convert
     * @return  Adventure component from this component.
     */
    @NotNull
    public static Component toAdventureComponent(@NotNull TabComponent component) {
        if (component instanceof AdventureComponent) return ((AdventureComponent) component).getComponent();
        if (component instanceof SimpleComponent) return Component.text(((SimpleComponent) component).getText());
        StructuredComponent iComponent = (StructuredComponent) component;
        ChatModifier modifier = iComponent.getModifier();

        Component adventureComponent = Component.text(
                iComponent.getText(),
                modifier.getColor() == null ? null : TextColor.color(modifier.getColor().getRgb()),
                decorations[modifier.getMagicCodeBitMask()]
        );

        if (modifier.getFont() != null) {
            adventureComponent = adventureComponent.font(Key.key(modifier.getFont()));
        }
        if (!iComponent.getExtra().isEmpty()) {
            List<Component> list = new ArrayList<>();
            for (StructuredComponent extra : iComponent.getExtra()) {
                list.add(toAdventureComponent(extra));
            }
            adventureComponent = adventureComponent.children(list);
        }
        return adventureComponent;
    }
}
