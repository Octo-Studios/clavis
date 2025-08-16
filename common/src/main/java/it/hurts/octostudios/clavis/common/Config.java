package it.hurts.octostudios.clavis.common;

import it.hurts.octostudios.octolib.module.config.annotation.Prop;
import it.hurts.octostudios.octolib.module.config.impl.OctoConfig;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Config implements OctoConfig {
    @Prop(comment = "Starting quality of a minigame.", inlineComment = "0.0 - 10.0")
    private float startingQuality = 2f;

    @Prop(comment = "Penalty for losing HP.")
    private float qualityPenaltyPerHit = 0.2f;

    @Prop(comment = "Default base value for any item, that doesn't have any valuable tags.")
    private double defaultBaseItemValue = 0.33d;

    @Prop(comment = "A collection of valuable tags and its values. If an item doesn't have any of these tags, it will use the base item value above.")
    private Map<String, Double> valuableTags = new HashMap<>();

    public Config() {
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
