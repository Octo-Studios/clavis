package it.hurts.octostudios.clavis.common.data;

import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TooltipInfoData {
    List<FormattedCharSequence> summary;
    List<FormattedCharSequence> description;
    int summaryLength;
    int descriptionLength;

    public TooltipInfoData(Font font, String infoPath, int maxLength) {
        Component title = Component.translatable("ui.clavis."+infoPath+".summary").withStyle(ChatFormatting.BOLD);

        summary = new ArrayList<>(font.split(title, maxLength));
        summary.add(Component.empty().getVisualOrderText());
        summary.add(Component.translatable("ui.clavis.hold_shift").withStyle(ChatFormatting.ITALIC).getVisualOrderText());
        description = new ArrayList<>(font.split(Component.translatable("ui.clavis."+infoPath+".description"), maxLength));
        description.addFirst(Component.empty().getVisualOrderText());
        description.addFirst(title.getVisualOrderText());

        summaryLength = summary.stream().mapToInt(font::width).max().getAsInt();
        descriptionLength = description.stream().mapToInt(font::width).max().getAsInt();
    }
}
