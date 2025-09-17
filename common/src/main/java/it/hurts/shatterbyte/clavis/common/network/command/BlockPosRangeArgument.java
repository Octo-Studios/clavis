package it.hurts.shatterbyte.clavis.common.network.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BlockPosRangeArgument implements ArgumentType<List<WorldCoordinates>> {
    public static BlockPosRangeArgument range() {
        return new BlockPosRangeArgument();
    }

    @Override
    public List<WorldCoordinates> parse(StringReader reader) throws CommandSyntaxException {
        List<WorldCoordinates> result = new ArrayList<>();

        // parse first coordinate (required)
        WorldCoordinates wc1 = (WorldCoordinates) BlockPosArgument.blockPos().parse(reader);
        result.add(wc1);

        // attempt to parse optional second coordinate.
        // if parsing fails, rewind the reader to the position after the first coordinate
        // so later arguments (difficulty/seed/...) parse correctly.
        int afterFirst = reader.getCursor();
        if (reader.canRead() && reader.peek() == ' ') {
            try {
                WorldCoordinates wc2 = (WorldCoordinates) BlockPosArgument.blockPos().parse(reader);
                result.add(wc2);
            } catch (CommandSyntaxException ex) {
                // not a second blockpos — reset cursor and proceed as single-pos
                reader.setCursor(afterFirst);
            }
        }

        return result;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String input = builder.getInput();
        StringReader reader = new StringReader(input);

        try {
            // try to parse a full first BlockPos from the beginning of the input
            BlockPosArgument.blockPos().parse(reader);
            int afterFirst = reader.getCursor();

            // if the current token starts at or before the end of the first pos,
            // we're completing the first pos -> delegate to BlockPosArgument with the original builder
            if (builder.getStart() <= afterFirst) {
                return BlockPosArgument.blockPos().listSuggestions(context, builder);
            }

            // otherwise the cursor is in the second-pos token -> create a builder starting at that token
            int start = afterFirst;
            // advance past whitespace so the suggestions builder starts at the second token
            while (start < input.length() && Character.isWhitespace(input.charAt(start))) start++;

            SuggestionsBuilder secondBuilder = new SuggestionsBuilder(input, start);
            return BlockPosArgument.blockPos().listSuggestions(context, secondBuilder);
        } catch (CommandSyntaxException ex) {
            // couldn't parse a complete first pos -> we're still typing the first pos
            return BlockPosArgument.blockPos().listSuggestions(context, builder);
        }
    }

    @SuppressWarnings("unchecked")
    public static List<WorldCoordinates> getPositions(CommandContext<CommandSourceStack> ctx, String name) {
        return (List<WorldCoordinates>) ctx.getArgument(name, List.class);
    }

    /** helper to convert WorldCoordinates -> BlockPos in current world context */
    public static List<BlockPos> resolve(CommandContext<CommandSourceStack> ctx, String name) {
        List<WorldCoordinates> coords = getPositions(ctx, name);
        List<BlockPos> result = new ArrayList<>();
        for (WorldCoordinates wc : coords) {
            result.add(wc.getBlockPos(ctx.getSource()));
        }
        return result;
    }
}