package org.turbogiants.common.packet.definition.client;

import org.turbogiants.common.packet.OutPacket;
import org.turbogiants.common.packet.PacketEnum;

public class User {

    public static OutPacket Handler_TCS_USER_SET_ID_REQ(int iID){
        OutPacket outPacket = new OutPacket(PacketEnum.TCS_USER_SET_ID_REQ);
        outPacket.encodeInt(iID);
        return outPacket;
    }
}
