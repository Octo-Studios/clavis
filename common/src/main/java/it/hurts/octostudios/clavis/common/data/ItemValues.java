package it.hurts.octostudios.clavis.common.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.hurts.octostudios.clavis.common.Clavis;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ItemValues {
    public static final Map<Item, Double> VALUES = new HashMap<>();
    //public static final Map<ResourceKey<LootTable>, Float> DIFFICULTY_CACHE = new HashMap<>();

    public static final Map<TagKey<Item>, Function<ItemStack, Double>> TAGS = new HashMap<>();
    public static final Function<Double, Function<ItemStack, Double>> DEFAULT_FUNCTION = value -> itemStack -> {
        double finalValue = value;

        for (ValueModifier modifier : ValueModifier.values()) {
            finalValue *= modifier.getModifier().apply(itemStack);
        }

        return finalValue * itemStack.getCount();
    };

    public static void addBasicValue(ResourceLocation rl, double value) {
        TAGS.put(tag(rl), DEFAULT_FUNCTION.apply(value));
    }

    public static TagKey<Item> tag(ResourceLocation tag) {
        return TagKey.create(Registries.ITEM, tag);
    }

    public static TagKey<Item> tag(String path) {
        return TagKey.create(Registries.ITEM, c(path));
    }

    public static ResourceLocation c(String path) {
        return ResourceLocation.fromNamespaceAndPath("c", path);
    }

    public static void register() {
        addBasicValue(c("gems"), 6);
        addBasicValue(c("storage_blocks"), 16);
        addBasicValue(c("ores"), 3);
        addBasicValue(c("ingots"), 4);
        addBasicValue(c("dusts"), 2);
        addBasicValue(c("crops"), 4);
        addBasicValue(c("foods/golden"), 16);
        addBasicValue(c("tools"), 8);
        addBasicValue(c("armors"), 8);
        addBasicValue(c("music_discs"), 24);

        InputStream stream = Clavis.class.getResourceAsStream("/internal/clavis/item_values.json");
        if (stream == null) {
            return;
        }

        JsonElement json = JsonParser.parseReader(new InputStreamReader(stream));
        if (json == null || !json.isJsonObject()) {
            return;
        }

        JsonObject obj = json.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            try {
                ResourceLocation key = ResourceLocation.parse(entry.getKey());
                double value = entry.getValue().getAsDouble();
                Item item = BuiltInRegistries.ITEM.get(key);
                if (item == null) {
                    continue;
                }

                ItemValues.VALUES.put(item, value);
            } catch (Exception e) {
                System.err.println("Invalid entry: " + entry.getKey() + " -> " + entry.getValue());
            }
        }
    }

    public static float getValue(ItemStack stack) {
//        AtomicInteger value = new AtomicInteger();
//
//        ItemValues.TAGS.forEach((itemTagKey, function) -> {
//            if (stack.getTags().anyMatch(tag -> tag.equals(itemTagKey))) {
//                value.set(Math.max(value.get(), function.apply(stack)));
//            }
//        });
//
//        if (value.get() <= 0) {
//            return DEFAULT_FUNCTION.apply(1).apply(stack);
//        }

        return (float) Math.max(DEFAULT_FUNCTION.apply(VALUES.getOrDefault(stack.getItem(), 1d)).apply(stack), 1);
    }
}
