package it.hurts.octostudios.clavis.common;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import dev.architectury.event.events.common.*;
import it.hurts.octostudios.clavis.common.data.Box;
import it.hurts.octostudios.clavis.common.data.ItemValues;
import it.hurts.octostudios.clavis.common.data.Lock;
import it.hurts.octostudios.clavis.common.network.LockInteractionBlockers;
import it.hurts.octostudios.clavis.common.network.PacketRegistry;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.UUID;

public class Clavis {
    public static final String MODID = "clavis";

    public static void init() {
        PacketRegistry.register();
        ItemValues.register();

        LootrCompat.init();

        LifecycleEvent.SERVER_LEVEL_LOAD.register(LockManager::load);
        LifecycleEvent.SERVER_LEVEL_SAVE.register(LockManager::save);

        BlockEvent.BREAK.register(LockInteractionBlockers::onBreak);
        ExplosionEvent.DETONATE.register(LockInteractionBlockers::onBlow);
        InteractionEvent.RIGHT_CLICK_BLOCK.register(LockInteractionBlockers::onInteract);
        InteractionEvent.LEFT_CLICK_BLOCK.register(LockInteractionBlockers::cancelInteraction);
        CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) -> {
            dispatcher.register(Commands.literal("clavis")
                    .then(Commands.literal("lock")
                            .then(Commands.literal("add")
                                    .then(Commands.argument("blockpos", BlockPosArgument.blockPos())
                                            .then(Commands.argument("difficulty", FloatArgumentType.floatArg(0f))
                                                    .then(Commands.argument("seed", LongArgumentType.longArg())
                                                            .executes(context -> {
                                                                Lock lock = new Lock(
                                                                        UUID.randomUUID(),
                                                                        new Box(context.getArgument("blockpos", WorldCoordinates.class).getBlockPos(context.getSource())),
                                                                        context.getArgument("difficulty", Float.class),
                                                                        context.getArgument("seed", Long.class), true
                                                                );

                                                                LockManager.addLock(context.getSource().getLevel(), lock);
                                                                return Command.SINGLE_SUCCESS;
                                                            })
                                                    )
                                            )
                                            .then(Commands.argument("blockpos2", BlockPosArgument.blockPos())
                                                    .then(Commands.argument("difficulty", FloatArgumentType.floatArg(0f))
                                                            .then(Commands.argument("seed", LongArgumentType.longArg())
                                                                    .executes(context -> {
                                                                        Lock lock = new Lock(
                                                                                UUID.randomUUID(),
                                                                                new Box(
                                                                                        context.getArgument("blockpos", WorldCoordinates.class).getBlockPos(context.getSource()),
                                                                                        context.getArgument("blockpos2", WorldCoordinates.class).getBlockPos(context.getSource())
                                                                                ),
                                                                                context.getArgument("difficulty", Float.class),
                                                                                context.getArgument("seed", Long.class), false
                                                                        );

                                                                        LockManager.addLock(context.getSource().getLevel(), lock);
                                                                        return Command.SINGLE_SUCCESS;
                                                                    })
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
                    )
            );
        });
    }

    public static ResourceLocation path(String path) {
        return ResourceLocation.fromNamespaceAndPath(Clavis.MODID, path);
    }
}