package it.hurts.octostudios.clavis.common.network;

import it.hurts.octostudios.clavis.common.network.packet.*;
import it.hurts.octostudios.octolib.module.network.OctolibNetwork;

public class PacketRegistry {
    public static void register() {
        OctolibNetwork.registerS2C(OpenLockpickingPacket.TYPE, OpenLockpickingPacket.STREAM_CODEC, OpenLockpickingPacket::handle);
        OctolibNetwork.registerC2S(FinishLockpickingPacket.TYPE, FinishLockpickingPacket.STREAM_CODEC, FinishLockpickingPacket::handle);
        OctolibNetwork.registerC2S(LockRequestPacket.TYPE, LockRequestPacket.STREAM_CODEC, LockRequestPacket::handle);
        OctolibNetwork.registerS2C(ReceiveLocksForRenderingPacket.TYPE, ReceiveLocksForRenderingPacket.STREAM_CODEC, ReceiveLocksForRenderingPacket::handle);
        OctolibNetwork.registerS2C(RemoveLockPacket.TYPE, RemoveLockPacket.STREAM_CODEC, RemoveLockPacket::handle);
    }
}
