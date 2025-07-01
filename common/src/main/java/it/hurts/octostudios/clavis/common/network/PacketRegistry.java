package it.hurts.octostudios.clavis.common.network;

import it.hurts.octostudios.clavis.common.network.packet.FinishLockpickingPacket;
import it.hurts.octostudios.clavis.common.network.packet.OpenLockpickingPacket;
import it.hurts.octostudios.octolib.module.network.OctolibNetwork;

public class PacketRegistry {
    public static void register() {
        OctolibNetwork.registerS2C(OpenLockpickingPacket.TYPE, OpenLockpickingPacket.STREAM_CODEC, OpenLockpickingPacket::handle);
        OctolibNetwork.registerC2S(FinishLockpickingPacket.TYPE, FinishLockpickingPacket.STREAM_CODEC, FinishLockpickingPacket::handle);
    }
}
