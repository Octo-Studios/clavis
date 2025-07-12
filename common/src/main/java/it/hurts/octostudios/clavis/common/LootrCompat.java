package it.hurts.octostudios.clavis.common;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.RandomizableContainer;
import noobanidus.mods.lootr.common.api.data.ILootrInfoProvider;
import noobanidus.mods.lootr.common.api.data.LootFiller;

import java.lang.reflect.Method;

public class LootrCompat {
    private static Class<?> lootrBlockEntityClass = null;
    private static Method getInventory = null;

    public static void init() {
        try {
            lootrBlockEntityClass = Class.forName("noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity");
            Class<?> lootrApi = Class.forName("noobanidus.mods.lootr.common.api.LootrAPI");
            getInventory = lootrApi.getMethod("getInventory", ILootrInfoProvider.class, ServerPlayer.class, LootFiller.class);
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {

        }
    }

    public static boolean isLootrBlockEntity(RandomizableContainer be) {
        if (lootrBlockEntityClass == null) {
            return false;
        }

        return lootrBlockEntityClass.isInstance(be);
    }
}