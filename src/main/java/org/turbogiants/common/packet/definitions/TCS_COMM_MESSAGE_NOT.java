package org.turbogiants.common.packet.definitions;

import org.turbogiants.common.MessageInfo;
import org.turbogiants.common.packet.InPacket;
import org.turbogiants.common.packet.OutPacket;
import org.turbogiants.common.packet.PacketEnum;

public class TCS_COMM_MESSAGE_NOT {
    private MessageInfo messageInfo;

    public void setMessageInfo(MessageInfo messageInfo){
        this.messageInfo = messageInfo;
    }

    public MessageInfo getMessageInfo(){
        return messageInfo;
    }

    public OutPacket serialize(PacketEnum header){
        OutPacket outPacket = new OutPacket(header);
        outPacket.encodeInt(messageInfo.getDestID());
        outPacket.encodeInt(messageInfo.getOwnerID());
        outPacket.encodeLong(messageInfo.getUnixTime());
        outPacket.encodeString(messageInfo.getMessage());
        return outPacket;
    }

    public void deserialize(InPacket inPacket){
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setDestID(inPacket.decodeInt());
        messageInfo.setOwnerID(inPacket.decodeInt());
        messageInfo.setUnixTime(inPacket.decodeLong());
        messageInfo.setMessage(inPacket.decodeString());
        this.messageInfo = messageInfo;
    }
}
