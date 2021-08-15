package org.turbogiants.common.packet.definition.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.turbogiants.common.packet.InPacket;
import org.turbogiants.common.packet.OutPacket;
import org.turbogiants.common.packet.PacketEnum;
import org.turbogiants.common.user.User;
import org.turbogiants.common.user.UserDef;

public class Comm {

    private static final Logger LOGGER = LogManager.getRootLogger();

    public static OutPacket Handler_TCS_COMM_MESSAGE_REQ(User user, InPacket inPacket) {
        OutPacket oPacket_ACK = new OutPacket(PacketEnum.TCS_COMM_MESSAGE_ACK);
        int destID = inPacket.decodeInt();
        String message = inPacket.decodeString();
        User destUser = User.getUserByID(destID);
        if(destUser == null){
            oPacket_ACK.encodeInt(-1); // unable to send message atm

            //todo: store to db
        } else {
            oPacket_ACK.encodeInt(0); // 0 = ok

            OutPacket oPacket_NOT = new OutPacket(PacketEnum.TCS_COMM_MESSAGE_NOT);
            oPacket_NOT.encodeInt(user.getUserDef().getUID());
            oPacket_NOT.encodeString(message);
            destUser.write(oPacket_NOT);
        }
        return oPacket_ACK;
    }

}
