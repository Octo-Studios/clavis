package it.hurts.octostudios.clavis.common.config;

import it.hurts.octostudios.octolib.module.config.annotation.Prop;
import it.hurts.octostudios.octolib.module.config.impl.OctoConfig;
import lombok.Data;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

@Data
public class Config implements OctoConfig {
    @Prop(comment = "Minigame type by dimension.")
    private Map<String, String> minigameType = new HashMap<>();

    @Prop(comment = "Starting quality of a minigame.", inlineComment = "0.0 - 10.0")
    private float startingQuality = 2f;

    @Prop(comment = "Penalty for losing HP.")
    private float qualityPenaltyPerHit = 0.2f;

    @Prop(comment = "Default base value for any item, that doesn't have any valuable tags.")
    private double defaultBaseItemValue = 0.33d;

    @Prop(comment = "A collection of valuable tags and its values. If an item doesn't have any of these tags, it will use the base item value above.")
    private Map<String, Double> valuableTags = new HashMap<>();

    @Prop(comment = "Additional value multipliers. Added on top of item value")
    private ModifiersConfig modifiers = new ModifiersConfig();

    @Prop(comment = "Linearly maps calculated item values within this range to a 0% – 100% difficulty scale.")
    private Range itemValueRange = new Range(0d, 224d);

    @Prop(comment = "Difficulty threshold for spawning locks. Locks with difficulty below this value will not spawn.")
    private double difficultyThreshold = 0.05d;

    @Prop(comment = "Clamps the difficulty to this value.")
    private double upperDifficultyClamp = 1.5d;

    @Prop(comment = "Multiplies every difficulty by this value before clamping, simple as that.")
    private double globalDifficultyMultiplier = 1.0d;

    @Prop(comment = "Multiplies the difficulty for the loot table by the value provided below (before clamping).")
    private Map<String, Double> lootTableMultiplier = new HashMap<>();
    
    @Prop(comment = "Determines whether or not losing the minigame unlocks the lock.")
    private boolean unlocksAfterLosing = true;

    @Prop(comment = "Disables rendering of locks for players.")
    private boolean disableLockRendering = false;

    public Config() {
        minigameType.put(Level.OVERWORLD.location().toString(), "clavis:gear");
        minigameType.put(Level.END.location().toString(), "clavis:mirror");

        lootTableMultiplier.put("minecraft:chests/example", 1.5d);

        valuableTags.put("c:ingots", 5d);
        valuableTags.put("c:gems", 8d);
        valuableTags.put("c:storage_blocks", 16d);
        valuableTags.put("c:ores", 4d);
        valuableTags.put("c:raw_materials", 4d);
        valuableTags.put("c:rods", 6d);
        valuableTags.put("c:alloys", 6d);
        valuableTags.put("c:circuits", 8d);
        valuableTags.put("c:dusts", 1d);
        valuableTags.put("c:foods/golden", 16d);
        valuableTags.put("c:tools", 2d);
        valuableTags.put("c:armors", 2d);
        valuableTags.put("c:music_discs", 8d);
    }
}
