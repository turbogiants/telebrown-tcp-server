package org.turbogiants.client.connection.definition;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.turbogiants.common.packet.InPacket;
import org.turbogiants.common.packet.OutPacket;
import org.turbogiants.common.packet.PacketEnum;

public class CUser {
    private static final Logger LOGGER = LogManager.getRootLogger();

    public static OutPacket Handler_TCS_USER_SET_ID_REQ(int iID) {
        OutPacket outPacket = new OutPacket(PacketEnum.TCS_USER_SET_ID_REQ);
        outPacket.encodeInt(iID);
        return outPacket;
    }

    public static void Handler_TCS_USER_SET_ID_ACK(InPacket inPacket) {
        byte b = inPacket.decodeByte();
        LOGGER.info("SetID Result:" + b);
    }
}
