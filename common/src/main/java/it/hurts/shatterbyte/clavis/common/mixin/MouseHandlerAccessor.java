package it.hurts.shatterbyte.clavis.common.mixin;

import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MouseHandler.class)
public interface MouseHandlerAccessor {
    @Accessor("xpos")
    double getXpos();

    @Accessor("xpos")
    void setXpos(double xpos);

    @Accessor("ypos")
    double getYpos();

    @Accessor("ypos")
    void setYpos(double ypos);

    @Invoker("onMove")
    void invokeOnMove(long window, double xpos, double ypos);
}