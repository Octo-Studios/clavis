package it.hurts.octostudios.clavis.common.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class ItemValues {
    public static final Map<Item, Double> VALUES = new HashMap<>();
    public static final Map<TagKey<Item>, Double> TAGS = new HashMap<>();
    public static final Function<Double, Function<ItemStack, Double>> DEFAULT_FUNCTION = value -> itemStack -> {
        double finalValue = value;

        for (ValueModifier modifier : ValueModifier.values()) {
            finalValue *= modifier.getModifier().apply(itemStack);
        }

        return finalValue * itemStack.getCount();
    };

    public static void addBasicValue(ResourceLocation rl, double value) {
        TAGS.put(tag(rl), value);
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
        addBasicValue(c("gems"), 12);
        addBasicValue(c("storage_blocks"), 24);
        addBasicValue(c("ores"), 6);
        addBasicValue(c("raw_materials"), 6);
        addBasicValue(c("rods"), 8);
        addBasicValue(c("ingots"), 8);
        addBasicValue(c("alloys"), 10);
        addBasicValue(c("circuits"), 10);
        addBasicValue(c("dusts"), 1);
        addBasicValue(c("foods/golden"), 16);
        addBasicValue(c("tools"), 2);
        addBasicValue(c("armors"), 2);
        addBasicValue(c("music_discs"), 8);

        //loadJson();
    }

//    private static void loadJson() {
//        InputStream stream = Clavis.class.getResourceAsStream("/internal/clavis/item_values.json");
//        if (stream == null) {
//            return;
//        }
//
//        JsonElement json = JsonParser.parseReader(new InputStreamReader(stream));
//        if (json == null || !json.isJsonObject()) {
//            return;
//        }
//
//        JsonObject obj = json.getAsJsonObject();
//        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
//            try {
//                ResourceLocation key = ResourceLocation.parse(entry.getKey());
//                double value = entry.getValue().getAsDouble();
//                Item item = BuiltInRegistries.ITEM.get(key);
//                if (item == null) {
//                    continue;
//                }
//
//                ItemValues.VALUES.put(item, value);
//            } catch (Exception e) {
//                System.err.println("Invalid entry: " + entry.getKey() + " -> " + entry.getValue());
//            }
//        }
//    }

    public static double getValue(ItemStack stack) {
        AtomicReference<Double> value = new AtomicReference<>(0d);
        stack.getTags().forEach(tagKey -> value.set(value.get() + TAGS.getOrDefault(tagKey, 0d)));

        if (value.get() <= 0d) {
            return DEFAULT_FUNCTION.apply(0.33d).apply(stack);
        }

        return DEFAULT_FUNCTION.apply(value.get()).apply(stack);
    }
}
