package it.hurts.octostudios.clavis.common;

import dev.architectury.platform.Platform;

import java.lang.reflect.InvocationTargetException;

public class LootrCompat {
    public static LootrCompatProxy COMPAT;

    public static void init() {
        try {
            if (Platform.getOptionalMod("lootr").isPresent()) {
                COMPAT = (LootrCompatProxy) Class.forName("it.hurts.octostudios.clavis.common.ActualLootrCompat").getDeclaredConstructor().newInstance();
            } else {
                COMPAT = new DummyLootrCompat();
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException ignored) {

        }
    }
}