package org.turbogiants.server.connection.definition;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.turbogiants.common.packet.InPacket;
import org.turbogiants.common.packet.OutPacket;
import org.turbogiants.common.packet.PacketEnum;
import org.turbogiants.server.user.User;


public class Handshake {

    private static final Logger LOGGER = LogManager.getRootLogger();

    public static OutPacket Handler_TCS_HANDSHAKE_NOT() {
        return new OutPacket(PacketEnum.TCS_HANDSHAKE_NOT);
    }

    public static OutPacket Handler_TCS_HANDSHAKE_REQ(User user, InPacket inPacket) {
        if (!"愛されなくても君がいる".equals(inPacket.decodeString()) &&
                !"ピノキオピー".equals(inPacket.decodeString()) &&
                !"初音ミク".equals(inPacket.decodeString())
        ) {
            LOGGER.error("Client(" + user.getIP() + "): Handshake Failed");
            return null;
        }

        OutPacket oPacket = new OutPacket(PacketEnum.TCS_HANDSHAKE_ACK);
        oPacket.encodeByte(0); // ok
        return oPacket;
    }
}
