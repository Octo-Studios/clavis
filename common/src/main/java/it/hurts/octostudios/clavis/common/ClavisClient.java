package it.hurts.octostudios.clavis.common;

import net.minecraft.client.Minecraft;

public class ClavisClient {
    public static void init() {

    }

    public static double getDeltaTime() {
        return Minecraft.getInstance().getFrameTimeNs() / 1000000000d;
    }
}