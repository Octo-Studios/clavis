package it.hurts.octostudios.clavis.common;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.*;
import dev.architectury.networking.NetworkManager;
import it.hurts.octostudios.clavis.common.data.Box;
import it.hurts.octostudios.clavis.common.data.ClavisSavedData;
import it.hurts.octostudios.clavis.common.data.ItemValues;
import it.hurts.octostudios.clavis.common.data.Lock;
import it.hurts.octostudios.clavis.common.minigame.rule.Rule;
import it.hurts.octostudios.clavis.common.network.LockInteractionBlockers;
import it.hurts.octostudios.clavis.common.network.PacketRegistry;
import it.hurts.octostudios.clavis.common.network.packet.OpenLockpickingPacket;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class Clavis {
    public static final String MODID = "clavis";

    public static void init() {
        Rule.registerAll();
        PacketRegistry.register();
        ItemValues.register();

        BlockEvent.BREAK.register(LockInteractionBlockers::onBreak);
        ExplosionEvent.DETONATE.register(LockInteractionBlockers::onBlow);
        InteractionEvent.RIGHT_CLICK_BLOCK.register(LockInteractionBlockers::onInteract);
        InteractionEvent.LEFT_CLICK_BLOCK.register((player, hand, pos, face) -> {
            if (ClavisSavedData.isLocked(pos, player.level())) {
                return EventResult.interruptFalse();
            }

            return EventResult.pass();
        });
        CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) -> {
            dispatcher.register(Commands.literal("clavis")
                    .then(Commands.literal("lock")
                            .then(Commands.literal("add")
                                    .then(Commands.argument("blockpos", BlockPosArgument.blockPos())
                                            .then(Commands.argument("difficulty", FloatArgumentType.floatArg(0f))
                                                    .then(Commands.argument("seed", LongArgumentType.longArg())
                                                            .executes(context -> {
                                                                ClavisSavedData data = ClavisSavedData.get(context.getSource().getLevel());
                                                                data.addLock(new Lock(
                                                                        new Box(context.getArgument("blockpos", WorldCoordinates.class).getBlockPos(context.getSource())),
                                                                        context.getArgument("difficulty", Float.class),
                                                                        context.getArgument("seed", Long.class)
                                                                ), context.getSource().getLevel());

                                                                return Command.SINGLE_SUCCESS;
                                                            })
                                                    )
                                            )
                                            .then(Commands.argument("blockpos2", BlockPosArgument.blockPos())
                                                    .then(Commands.argument("difficulty", FloatArgumentType.floatArg(0f))
                                                            .then(Commands.argument("seed", LongArgumentType.longArg())
                                                                    .executes(context -> {
                                                                        ClavisSavedData data = ClavisSavedData.get(context.getSource().getLevel());
                                                                        data.addLock(new Lock(
                                                                                new Box(
                                                                                        context.getArgument("blockpos", WorldCoordinates.class).getBlockPos(context.getSource()),
                                                                                        context.getArgument("blockpos2", WorldCoordinates.class).getBlockPos(context.getSource())
                                                                                ),
                                                                                context.getArgument("difficulty", Float.class),
                                                                                context.getArgument("seed", Long.class)
                                                                        ), context.getSource().getLevel());

                                                                        return Command.SINGLE_SUCCESS;
                                                                    })
                                                            )
                                                    )
                                            )
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