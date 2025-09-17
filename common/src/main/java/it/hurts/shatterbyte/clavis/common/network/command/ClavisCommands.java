package it.hurts.shatterbyte.clavis.common.network.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import it.hurts.shatterbyte.clavis.common.LockManager;
import it.hurts.shatterbyte.clavis.common.data.Box;
import it.hurts.shatterbyte.clavis.common.data.Lock;
import it.hurts.shatterbyte.clavis.common.data.LootUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClavisCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, net.minecraft.commands.Commands.CommandSelection commandSelection) {
        dispatcher.register(Commands.literal("clavis").requires(source -> source.hasPermission(2))
                .then(Commands.literal("calculate")
                        .then(Commands.argument("blockpos", BlockPosArgument.blockPos())
                                .then(Commands.argument("iterations", IntegerArgumentType.integer(0, 100))
                                        .executes(context -> {
                                            ServerLevel level = context.getSource().getLevel();
                                            BlockPos pos = context.getArgument("blockpos", WorldCoordinates.class).getBlockPos(context.getSource());
                                            int iterations = context.getArgument("iterations", Integer.class);
                                            BlockEntity blockEntity = level.getBlockEntity(pos);
                                            if (!(blockEntity instanceof RandomizableContainer rbe)) {
                                                context.getSource().sendFailure(Component.literal("Not a randomizable container!"));
                                                return 1;
                                            }

                                            float difficulty = (float) LootUtils.calculateDifficulty(context.getSource().getLevel(), pos, rbe, iterations, true, context.getSource());
                                            Component component = Component.literal("Difficulty: ").append(Component.literal(String.format("%.1f", difficulty * 100) + "%").withColor(LootUtils.getColorForDifficulty(difficulty)));
                                            context.getSource().sendSystemMessage(component);
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                )
                .then(Commands.literal("lock")
                        .then(Commands.literal("add")
                                .then(Commands.argument("type", ResourceLocationArgument.id())
                                        .then(Commands.argument("difficulty", FloatArgumentType.floatArg(0f))
                                                .then(Commands.argument("seed", LongArgumentType.longArg())
                                                        .then(Commands.argument("perplayer", BoolArgumentType.bool())
                                                                .then(Commands.argument("blockpos", BlockPosArgument.blockPos())
                                                                        .executes(context -> {
                                                                            Lock lock = new Lock(
                                                                                    UUID.randomUUID(),
                                                                                    new Box(context.getArgument("blockpos", WorldCoordinates.class).getBlockPos(context.getSource())),
                                                                                    context.getArgument("difficulty", Float.class),
                                                                                    context.getArgument("seed", Long.class),
                                                                                    context.getArgument("perplayer", Boolean.class),
                                                                                    new ArrayList<>(),
                                                                                    context.getArgument("type", ResourceLocation.class)
                                                                            );

                                                                            LockManager.addLock(context.getSource().getLevel(), lock);
                                                                            return Command.SINGLE_SUCCESS;
                                                                        })
                                                                )
                                                                .then(Commands.argument("blockpos2", BlockPosArgument.blockPos())
                                                                        .executes(context -> {
                                                                            Lock lock = new Lock(
                                                                                    UUID.randomUUID(),
                                                                                    new Box(
                                                                                            context.getArgument("blockpos", WorldCoordinates.class).getBlockPos(context.getSource()),
                                                                                            context.getArgument("blockpos2", WorldCoordinates.class).getBlockPos(context.getSource())
                                                                                    ),
                                                                                    context.getArgument("difficulty", Float.class),
                                                                                    context.getArgument("seed", Long.class),
                                                                                    context.getArgument("perplayer", Boolean.class),
                                                                                    new ArrayList<>(),
                                                                                    context.getArgument("type", ResourceLocation.class)
                                                                            );

                                                                            LockManager.addLock(context.getSource().getLevel(), lock);
                                                                            return Command.SINGLE_SUCCESS;
                                                                        })
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("blockpos", BlockPosArgument.blockPos())
                                        .executes(context -> {
                                            BlockPos pos = context.getArgument("blockpos", WorldCoordinates.class).getBlockPos(context.getSource());
                                            List<Lock> locks = LockManager.getLocksAt(context.getSource().getLevel(), null, pos);
                                            if (locks.isEmpty()) {
                                                context.getSource().sendFailure(Component.literal("No locks have been found at this position. Aborting..."));
                                                return 1;
                                            }

                                            LockManager.removeLock(context.getSource().getLevel(), locks.getFirst());
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(Commands.literal("get")
                                .then(Commands.argument("blockpos", BlockPosArgument.blockPos())
                                        .executes(context -> {
                                            BlockPos pos = context.getArgument("blockpos", WorldCoordinates.class).getBlockPos(context.getSource());
                                            List<Lock> locks = LockManager.getLocksAt(context.getSource().getLevel(), null, pos);
                                            if (locks.isEmpty()) {
                                                context.getSource().sendFailure(Component.literal("No locks have been found at this position. Aborting..."));
                                                return 1;
                                            }

                                            MutableComponent component = Component.literal("Locks at " + pos.toShortString() + ":").append("\n\n");

                                            for (Lock lock : locks) {
                                                component.append(Component.literal(lock.getUuid().toString() + ":").withStyle(ChatFormatting.GOLD)).append("\n");
                                                component.append("    ").append(Component.literal("Type: ").withStyle(ChatFormatting.GRAY)).append(lock.getType(context.getSource().getLevel()).toString()).append("\n");
                                                component.append("    ").append(Component.literal("Difficulty: ").withStyle(ChatFormatting.GRAY)).append(String.format("%.2f", lock.getDifficulty())).append("\n");
                                                component.append("    ").append(Component.literal("Seed: ").withStyle(ChatFormatting.GRAY)).append(String.valueOf(lock.getSeed())).append("\n");
                                                component.append("    ").append(Component.literal("Per-player: ").withStyle(ChatFormatting.GRAY)).append(String.valueOf(lock.isPerPlayer())).append("\n");
                                            }

                                            context.getSource().sendSystemMessage(component);
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(Commands.literal("unlock")
                                .then(Commands.argument("blockpos", BlockPosArgument.blockPos())
                                        .then(Commands.argument("quality", FloatArgumentType.floatArg(0f, 10f))
                                                .executes(context -> {
                                                    ServerPlayer player = context.getSource().getPlayer();
                                                    if (player == null) {
                                                        context.getSource().sendFailure(Component.literal("Should be run by a player. Aborting..."));
                                                        return 1;
                                                    }

                                                    BlockPos pos = context.getArgument("blockpos", WorldCoordinates.class).getBlockPos(context.getSource());
                                                    List<Lock> locks = LockManager.getLocksAt(context.getSource().getLevel(), player, pos);
                                                    if (locks.isEmpty()) {
                                                        context.getSource().sendFailure(Component.literal("No locks have been found at this position. Aborting..."));
                                                        return 1;
                                                    }

                                                    LootUtils.unlockWithQuality(
                                                            context.getSource().getLevel(),
                                                            player,
                                                            pos,
                                                            locks.getFirst(),
                                                            context.getArgument("quality", Float.class)
                                                    );

                                                    return Command.SINGLE_SUCCESS;
                                                })
                                        )
                                )
                        )
                )
        );
    }
}
