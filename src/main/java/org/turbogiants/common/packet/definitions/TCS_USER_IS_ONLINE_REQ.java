package org.turbogiants.common.packet.definitions;

import org.turbogiants.common.packet.InPacket;
import org.turbogiants.common.packet.OutPacket;
import org.turbogiants.common.packet.PacketEnum;

public class TCS_USER_IS_ONLINE_REQ {
    private String userID;

    public TCS_USER_IS_ONLINE_REQ(){
        userID = "0XDEADC0DE";
    }

    public void setUserID(String id){
        userID = id;
    }

    public String getUserID(){
        return userID;
    }

    public OutPacket serialize(PacketEnum header){
        OutPacket outPacket = new OutPacket(header);
        outPacket.encodeString(userID);
        return outPacket;
    }

    public void deserialize(InPacket inPacket){
        this.userID = inPacket.decodeString();
    }
}
