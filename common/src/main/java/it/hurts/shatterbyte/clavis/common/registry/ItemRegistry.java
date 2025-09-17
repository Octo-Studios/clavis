package it.hurts.shatterbyte.clavis.common.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import it.hurts.shatterbyte.clavis.common.Clavis;
import it.hurts.shatterbyte.clavis.common.item.LockPickItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Clavis.MOD_ID, Registries.ITEM);
    public static final RegistrySupplier<Item> LOCK_PICK = ITEMS.register("lock_pick", LockPickItem::new);
}
