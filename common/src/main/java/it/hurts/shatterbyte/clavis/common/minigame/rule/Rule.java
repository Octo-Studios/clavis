package it.hurts.shatterbyte.clavis.common.minigame.rule;

import it.hurts.shatterbyte.clavis.common.client.ClientMinigameTypeRegistry;
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
    private static final Map<ResourceLocation, Map<ResourceLocation, Rule<?>>> REGISTRY = new HashMap<>();

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

    public String getLanguageKey(ResourceLocation minigameType) {
        return "rule."+id.getNamespace()+"."+minigameType.getPath()+"."+id.getPath();
    }

    public String getLanguageKey(ResourceLocation minigameType, String key) {
        return this.getLanguageKey(minigameType)+"."+key;
    }

    protected Rule<T> register(Class<T> clazz) {
        ResourceLocation minigameType = ClientMinigameTypeRegistry.getId(clazz);

        REGISTRY.putIfAbsent(minigameType, new HashMap<>());
        REGISTRY.get(minigameType).put(this.id, this);
        return this;
    }

    public static <W extends AbstractMinigameWidget<?>> Collection<Rule<W>> getRegisteredRules(Class<W> clazz) {
        ResourceLocation minigameType = ClientMinigameTypeRegistry.getId(clazz);

        return Cast.cast(REGISTRY.get(minigameType).values());
    }

    public static <W extends AbstractMinigameWidget<?>> Rule<W> getRegisteredRule(ResourceLocation id) {
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
        EndRules.FAKE.register(MirrorWidget.class);
        EndRules.HOT_METEOR.register(MirrorWidget.class);
    }
}
