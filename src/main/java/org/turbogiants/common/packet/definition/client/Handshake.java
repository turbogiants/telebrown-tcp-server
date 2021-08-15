package org.turbogiants.common.packet.definition.client;

import org.turbogiants.common.packet.OutPacket;
import org.turbogiants.common.packet.PacketEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Handshake {
    private static final Logger LOGGER = LogManager.getRootLogger();

    public static OutPacket Handler_TCS_HANDSHAKE_NOT(){
        OutPacket oPacket = new OutPacket(PacketEnum.TCS_HANDSHAKE_REQ);
        oPacket.encodeString("愛されなくても君がいる");
        oPacket.encodeString("ピノキオピー");
        oPacket.encodeString("初音ミク");
        return oPacket;
    }

    public static void Handler_TCS_HANDSHAKE_ACK(){
        LOGGER.debug("Handshake Success!");
    }

}
