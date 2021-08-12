package net.browny.common.packet.definition.client;

import net.browny.common.packet.OutPacket;
import net.browny.common.packet.PacketEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Handshake {
    private static final Logger LOGGER = LogManager.getRootLogger();

    public static OutPacket Handler_TCS_HANDSHAKE_REQ(){
        OutPacket oPacket = new OutPacket(PacketEnum.TCS_HANDSHAKE_REQ);
        oPacket.encodeString("Handshake Init()");
        return oPacket;
    }

}
