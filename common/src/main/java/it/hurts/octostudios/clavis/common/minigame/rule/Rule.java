package it.hurts.octostudios.clavis.common.minigame.rule;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import it.hurts.octostudios.clavis.common.client.screen.widget.AbstractMinigameWidget;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
public class Rule<T extends AbstractMinigameWidget<?>> {
    private static final Map<ResourceLocation, Rule<?>> REGISTRY = new HashMap<>();
    private static final Multimap<Class<? extends AbstractMinigameWidget<?>>, Rule<?>> BY_CLASS = HashMultimap.create();

    ResourceLocation id;

    BiConsumer<T, Boolean> onClick;
    BiConsumer<T, Long> everyTick;
    Consumer<T> onCreate;

    public Rule(ResourceLocation id) {
        this.id = id;
    }

    protected Rule<T> withOnClick(BiConsumer<T, Boolean> onClick) {
        this.onClick = onClick;
        return this;
    }

    protected Rule<T> withEveryTick(BiConsumer<T, Long> everyTick) {
        this.everyTick = everyTick;
        return this;
    }

    protected Rule<T> withOnCreate(Consumer<T> onCreate) {
        this.onCreate = onCreate;
        return this;
    }

    protected Rule<T> register(Class<T> clazz) {
        REGISTRY.put(this.id, this);
        BY_CLASS.put(clazz, this);
        return this;
    }

    public static <W extends AbstractMinigameWidget<?>> Collection<Rule<?>> getRegisteredRules(Class<W> clazz) {
        return BY_CLASS.asMap().get(clazz);
    }
}
