package org.turbogiants.common.packet.definitions;

import org.turbogiants.common.packet.InPacket;
import org.turbogiants.common.packet.OutPacket;
import org.turbogiants.common.packet.PacketEnum;

public class TCS_USER_IS_ONLINE_REQ {
    private int userID;

    public TCS_USER_IS_ONLINE_REQ(){
        userID = 0;
    }

    public void setUserID(int id){
        userID = id;
    }

    public int getUserID(){
        return userID;
    }

    public OutPacket serialize(PacketEnum header){
        OutPacket outPacket = new OutPacket(header);
        outPacket.encodeInt(userID);
        return outPacket;
    }

    public void deserialize(InPacket inPacket){
        this.userID = inPacket.decodeInt();
    }
}
