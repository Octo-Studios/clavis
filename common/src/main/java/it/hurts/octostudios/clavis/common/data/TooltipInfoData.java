package it.hurts.octostudios.clavis.common.data;

import lombok.Getter;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

@Getter
public class TooltipInfoData {
    List<FormattedCharSequence> summary;
    List<FormattedCharSequence> description;
    int summaryLength;
    int descriptionLength;

    public TooltipInfoData(Font font, String infoPath, int maxLength) {
        summary = font.split(Component.translatable("ui.clavis."+infoPath+".summary"), maxLength);
        description = font.split(Component.translatable("ui.clavis."+infoPath+".description"), maxLength);

        summaryLength = summary.stream().mapToInt(font::width).max().getAsInt();
        descriptionLength = description.stream().mapToInt(font::width).max().getAsInt();
    }
}
