package org.turbogiants.common.packet.definitions;

import org.turbogiants.common.MessageInfo;
import org.turbogiants.common.packet.InPacket;
import org.turbogiants.common.packet.OutPacket;
import org.turbogiants.common.packet.PacketEnum;

public class TCS_COMM_3_MESSAGE_REQ extends TCS_COMM_MESSAGE_NOT {
    @Override
    public OutPacket serialize(PacketEnum header) {
        OutPacket outPacket = new OutPacket(header);
        outPacket.encodeString(messageInfo.getDestID());
        outPacket.encodeString(messageInfo.getOwnerID());
        outPacket.encodeLong(messageInfo.getUnixTime());
        outPacket.encodeString(messageInfo.getStrSha256());
        return outPacket;
    }

    @Override
    public void deserialize(InPacket inPacket) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setDestID(inPacket.decodeString());
        messageInfo.setOwnerID(inPacket.decodeString());
        messageInfo.setUnixTime(inPacket.decodeLong());
        messageInfo.setStrSha256(inPacket.decodeString());
        this.messageInfo = messageInfo;
    }
}
