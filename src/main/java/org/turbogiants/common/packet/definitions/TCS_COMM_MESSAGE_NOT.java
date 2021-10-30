package org.turbogiants.common.packet.definitions;

import org.turbogiants.common.MessageInfo;
import org.turbogiants.common.packet.InPacket;
import org.turbogiants.common.packet.OutPacket;
import org.turbogiants.common.packet.PacketEnum;


public class TCS_COMM_MESSAGE_NOT {
    protected MessageInfo messageInfo;

    public void setMessageInfo(MessageInfo messageInfo){
        this.messageInfo = messageInfo;
    }

    public MessageInfo getMessageInfo(){
        return messageInfo;
    }

    public OutPacket serialize(PacketEnum header) {
        OutPacket outPacket = new OutPacket(header);
        outPacket.encodeString(messageInfo.getDestID());
        outPacket.encodeString(messageInfo.getOwnerID());
        outPacket.encodeLong(messageInfo.getUnixTime());
        outPacket.encodeString(messageInfo.getStrSha256());

        // As server won't have the test keys it will only get it by byteArray
        outPacket.encodeArr(messageInfo.getByteArrMessage());

        return outPacket;
    }

    public void deserialize(InPacket inPacket) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setDestID(inPacket.decodeString());
        messageInfo.setOwnerID(inPacket.decodeString());
        messageInfo.setUnixTime(inPacket.decodeLong());
        messageInfo.setStrSha256(inPacket.decodeString());

        // As server won't have the test keys it will only get it by byteArray
        messageInfo.setByteArrMessage(inPacket.decodeArr());

        this.messageInfo = messageInfo;
    }
}
