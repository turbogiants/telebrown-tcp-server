package org.turbogiants.common.packet.definition.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.turbogiants.common.packet.InPacket;
import org.turbogiants.common.packet.OutPacket;
import org.turbogiants.common.packet.PacketEnum;

public class CUser {

    private static final Logger LOGGER = LogManager.getRootLogger();

    public static OutPacket Handler_TCS_USER_SET_ID_REQ(org.turbogiants.common.user.User user, InPacket inPacket) {
        OutPacket outPacket = new OutPacket(PacketEnum.TCS_USER_SET_ID_ACK);
        LOGGER.debug("Client(" + user.getIP() + ") SetID to " + inPacket.decodeInt());
        outPacket.encodeByte(true);
        return outPacket;
    }
}
