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
        outPacket.encodeInt(messageInfo.getiDestID());
        outPacket.encodeInt(messageInfo.getiOwnerID());
        outPacket.encodeLong(messageInfo.getlUnixTime());
        outPacket.encodeString(messageInfo.getStrMessage());
        return outPacket;
    }

    public void deserialize(InPacket inPacket){
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setiDestID(inPacket.decodeInt());
        messageInfo.setiOwnerID(inPacket.decodeInt());
        messageInfo.setlUnixTime(inPacket.decodeLong());
        messageInfo.setStrMessage(inPacket.decodeString());
        this.messageInfo = messageInfo;
    }
}
