package org.turbogiants.client.connection.definition;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.turbogiants.common.packet.InPacket;
import org.turbogiants.common.packet.OutPacket;
import org.turbogiants.common.packet.PacketEnum;

public class Comm {

    private static final Logger LOGGER = LogManager.getRootLogger();

    public static OutPacket Handler_TCS_COMM_MESSAGE_REQ(int destID, String message) {
        OutPacket outPacket = new OutPacket(PacketEnum.TCS_COMM_MESSAGE_REQ);
        outPacket.encodeInt(destID);
        outPacket.encodeString(message);
        return outPacket;
    }

    public static void Handler_TCS_COMM_MESSAGE_ACK(InPacket inPacket) {
        byte result = inPacket.decodeByte();
        if (result == -1){
            LOGGER.info("Can't send message atm");
        } else if (result == 0){
            LOGGER.info("Message sent");
        }
    }

    public static void Handler_TCS_COMM_MESSAGE_NOT(InPacket inPacket) {
        int uid = inPacket.decodeInt();
        String message = inPacket.decodeString();
        LOGGER.info("Message from " + uid + " : " + message);
    }
}
