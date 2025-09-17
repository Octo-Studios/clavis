package it.hurts.shatterbyte.clavis.common.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import it.hurts.shatterbyte.clavis.common.Clavis;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;

public class SoundEventRegistry {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Clavis.MOD_ID, Registries.SOUND_EVENT);

    public static final RegistrySupplier<SoundEvent> METEOR_SMASH = SOUNDS.register("meteor_smash", () -> SoundEvent.createVariableRangeEvent(Clavis.path("meteor_smash")));
    public static final RegistrySupplier<SoundEvent> METEOR_REGENERATE = SOUNDS.register("meteor_regenerate", () -> SoundEvent.createVariableRangeEvent(Clavis.path("meteor_regenerate")));
}
