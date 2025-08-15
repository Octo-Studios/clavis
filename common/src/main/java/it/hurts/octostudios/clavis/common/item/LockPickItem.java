package it.hurts.octostudios.clavis.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class LockPickItem extends Item {
    public LockPickItem() {
        super(new Item.Properties().arch$tab(CreativeModeTabs.TOOLS_AND_UTILITIES));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.clavis.lock_pick.tooltip").withStyle(ChatFormatting.GRAY));
    }
}
