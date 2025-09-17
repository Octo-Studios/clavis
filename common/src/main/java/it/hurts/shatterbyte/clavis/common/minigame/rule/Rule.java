package it.hurts.shatterbyte.clavis.common.minigame.rule;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import it.hurts.shatterbyte.clavis.common.client.screen.widget.AbstractMinigameWidget;
import it.hurts.shatterbyte.clavis.common.client.screen.widget.GearMechanismWidget;
import it.hurts.shatterbyte.clavis.common.client.screen.widget.MirrorWidget;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.util.Cast;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
public class Rule<T extends AbstractMinigameWidget<?>> {
    private static final Map<ResourceLocation, Rule<?>> REGISTRY = new HashMap<>();
    private static final Multimap<Class<? extends AbstractMinigameWidget<?>>, Rule<?>> BY_CLASS = LinkedHashMultimap.create();

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

    public static <W extends AbstractMinigameWidget<?>> Collection<Rule<W>> getRegisteredRules(Class<W> clazz) {
        return Cast.cast(BY_CLASS.asMap().get(clazz));
    }

    public static <W extends AbstractMinigameWidget<?>> Rule<W> getRegisteredRule(ResourceLocation id, Class<W> clazz) {
        return Cast.cast(REGISTRY.get(id));
    }

    public static void registerAll() {
        OverworldRules.FAKE_PIN.register(GearMechanismWidget.class);
        OverworldRules.MOOD_SWINGS.register(GearMechanismWidget.class);
        OverworldRules.ROTATE_GEAR.register(GearMechanismWidget.class);
        OverworldRules.FULL_THROTTLE.register(GearMechanismWidget.class);
        OverworldRules.SELF_DESTRUCTION.register(GearMechanismWidget.class);
        EndRules.ROTATE.register(MirrorWidget.class);
        EndRules.SWAP.register(MirrorWidget.class);
        EndRules.SHOCKWAVE.register(MirrorWidget.class);
    }
}
