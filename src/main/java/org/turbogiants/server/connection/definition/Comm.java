package org.turbogiants.server.connection.definition;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.turbogiants.common.packet.InPacket;
import org.turbogiants.common.packet.OutPacket;
import org.turbogiants.common.packet.PacketEnum;
import org.turbogiants.server.user.NettyUser;

public class Comm {

    private static final Logger LOGGER = LogManager.getRootLogger();

    public static OutPacket Handler_TCS_COMM_MESSAGE_REQ(NettyUser nettyUser, InPacket inPacket) {
        OutPacket oPacket_ACK = new OutPacket(PacketEnum.TCS_COMM_MESSAGE_ACK);
        int destID = inPacket.decodeInt();
        String message = inPacket.decodeString();
        NettyUser destUser = NettyUser.getUserByID(destID);
        if(destUser == null){
            oPacket_ACK.encodeInt(-1); // unable to send message atm

            //todo: store to db
        } else {
            oPacket_ACK.encodeInt(0); // 0 = ok

            OutPacket oPacket_NOT = new OutPacket(PacketEnum.TCS_COMM_MESSAGE_NOT);
            oPacket_NOT.encodeInt(nettyUser.getUserDef().getUID());
            oPacket_NOT.encodeString(message);
            destUser.write(oPacket_NOT);
        }
        return oPacket_ACK;
    }

}
