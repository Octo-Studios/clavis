package it.hurts.octostudios.clavis.common.data;

import it.hurts.octostudios.clavis.common.Clavis;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2i;

import java.util.HashMap;

@Getter
public class MinigameStyleData {
    private static final HashMap<ResourceLocation, MinigameStyleData> REGISTRY = new HashMap<>();

    ResourceLocation id;
    int titleColor;
    int descriptionColor;
    Vector2i fillTextureSize;

    private MinigameStyleData(int titleColor, int descriptionColor, Vector2i fillTextureSize) {
        this.titleColor = titleColor;
        this.descriptionColor = descriptionColor;
        this.fillTextureSize = fillTextureSize;
    }

    public static final MinigameStyleData GEAR = new MinigameStyleData(0xffd7e3f2, 0xeed1ad, new Vector2i(156, 8))
            .register(Clavis.path("gear"));
    public static final MinigameStyleData MIRROR = new MinigameStyleData(0xffffe9ff, 0xff33322a, new Vector2i(156, 16))
            .register(Clavis.path("mirror"));

    private MinigameStyleData register(ResourceLocation id) {
        this.id = id;
        REGISTRY.put(id, this);
        return this;
    }

    public static MinigameStyleData get(ResourceLocation id) {
        return REGISTRY.get(id);
    }
}
