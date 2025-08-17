package it.hurts.octostudios.clavis.common.config;

import it.hurts.octostudios.octolib.module.config.annotation.Prop;
import lombok.Data;
import net.minecraft.world.item.Rarity;

import java.util.HashMap;
import java.util.Map;

@Data
public class ModifiersConfig {
    @Prop(comment = "Added to the total multiplier per an enchantment level.")
    private double perEnchantmentLevel = 0.25;

    @Prop(comment = "Added to the total multiplier if the item's max stack size is less than 16.")
    private double lowStackSize = 1.25;

    @Prop(comment = "Added to the total multiplier if the item is equippable in curios or trinkets.")
    private double isCuriosOrTrinkets = 8d;

    @Prop(comment = "Added to the total multiplier if the item is of one of specified rarities.")
    private Map<Rarity, Double> rarity = new HashMap<>();

    public ModifiersConfig() {
        rarity.put(Rarity.UNCOMMON, 1.25);
        rarity.put(Rarity.RARE, 1.5);
        rarity.put(Rarity.EPIC, 2.0);
    }
}
