package it.hurts.octostudios.clavis.common.minigame.rule;

import it.hurts.octostudios.clavis.common.client.screen.widget.GearMechanismWidget;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
public class Rule<T extends AbstractWidget> {
    ResourceLocation id;

    BiConsumer<T, Boolean> onClick;
    BiConsumer<T, Long> everyTick;
    Consumer<T> onCreate;

    public Rule(ResourceLocation id) {
        this.id = id;
    }

    public Rule<T> withOnClick(BiConsumer<T, Boolean> onClick) {
        this.onClick = onClick;
        return this;
    }

    public Rule<T> withEveryTick(BiConsumer<T, Long> everyTick) {
        this.everyTick = everyTick;
        return this;
    }

    public Rule<T> withOnCreate(Consumer<T> onCreate) {
        this.onCreate = onCreate;
        return this;
    }
}
