package org.turbogiants.common.packet.definitions;

import org.turbogiants.common.MessageInfo;
import org.turbogiants.common.packet.InPacket;
import org.turbogiants.common.packet.OutPacket;
import org.turbogiants.common.packet.PacketEnum;


public class TCS_COMM_MESSAGE_ACK extends TCS_COMM_MESSAGE_NOT{

    public enum Status{
        MESSAGE_SENT_FAILED(-1),
        MESSAGE_SENT_SUCCESS(0),
        ;

        private final int statusID;

        Status(int statusID) {
            this.statusID = statusID;
        }

        public static Status getStatusByID(int statusID) {
            for (Status status : Status.values()) {
                if (status.getStatusID() == statusID) {
                    return status;
                }
            }
            return null;
        }

        public int getStatusID() {
            return statusID;
        }
    }

    private int iOK;

    public void setOK(Status op){
        iOK = op.getStatusID();
    }

    public int getOK(){
        return iOK;
    }

    @Override
    public OutPacket serialize(PacketEnum header){
        OutPacket outPacket = new OutPacket(header);
        outPacket.encodeInt(iOK);
        outPacket.encodeString(messageInfo.getDestID());
        outPacket.encodeString(messageInfo.getOwnerID());
        outPacket.encodeLong(messageInfo.getUnixTime());
        outPacket.encodeString(messageInfo.getStrSha256());

        // As server won't have the test keys it will only get it by byteArray
        outPacket.encodeArr(messageInfo.getByteArrMessage());
        return outPacket;
    }

    @Override
    public void deserialize(InPacket inPacket){
        iOK = inPacket.decodeInt();
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
