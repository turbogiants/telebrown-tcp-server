package org.turbogiants.server.connection.definition;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.turbogiants.common.packet.InPacket;
import org.turbogiants.common.packet.OutPacket;
import org.turbogiants.common.packet.PacketEnum;
import org.turbogiants.server.user.User;
import org.turbogiants.server.user.UserDef;

public class CUser {

    private static final Logger LOGGER = LogManager.getRootLogger();

    // todo: get uid from database instead of user defined id input
    public static OutPacket Handler_TCS_USER_SET_ID_REQ(User user, InPacket inPacket) {
        int uid = inPacket.decodeInt();
        if(uid <= 0){
            LOGGER.error("Client(" + user.getIP() + ") SetID to " + uid);
            return null;
        }
        boolean isExists = User.isUserIDExists(uid);
        if(isExists){
            LOGGER.error("Client(" + user.getIP() + ") SetID to " + uid + " but already exists");
            return null;
        }
        OutPacket outPacket = new OutPacket(PacketEnum.TCS_USER_SET_ID_ACK);
        outPacket.encodeByte(0); // ok

        UserDef userDef = new UserDef();
        userDef.setUID(uid);
        user.setUserDef(userDef);


        LOGGER.info("Client(" + user.getIP() + ") SetID to " + uid);
        return outPacket;
    }
}
