package org.turbogiants.common.packet.definitions;

import org.turbogiants.common.packet.InPacket;
import org.turbogiants.common.packet.OutPacket;
import org.turbogiants.common.packet.PacketEnum;

public class TCS_COMM_2_MESSAGE_REQ {
    private String iReceiverID;

    public void setReceiverID(String iReceiverID) {
        this.iReceiverID = iReceiverID;
    }

    public String getReceiverID(){
        return iReceiverID;
    }

    private int iOK;

    public OutPacket serialize(PacketEnum header){
        OutPacket outPacket = new OutPacket(header);
        outPacket.encodeInt(iOK);
        outPacket.encodeString(iReceiverID);
        return outPacket;
    }

    public void deserialize(InPacket inPacket){
        iOK = inPacket.decodeInt();
        iReceiverID = inPacket.decodeString();
    }

}
