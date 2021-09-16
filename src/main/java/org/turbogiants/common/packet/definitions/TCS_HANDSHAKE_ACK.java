package org.turbogiants.common.packet.definitions;

import org.turbogiants.common.packet.InPacket;
import org.turbogiants.common.packet.OutPacket;
import org.turbogiants.common.packet.PacketEnum;

public class TCS_HANDSHAKE_ACK {

    public enum Status{
        HANDSHAKE_FAILED(-1),
        HANDSHAKE_SUCCESS(0),
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

    public void setiOK(Status op){
        iOK = op.getStatusID();
    }

    public int getiOK(){
        return iOK;
    }

    public OutPacket serialize(PacketEnum header){
        OutPacket outPacket = new OutPacket(header);
        outPacket.encodeInt(iOK);
        return outPacket;
    }

    public void deserialize(InPacket inPacket){
        iOK = inPacket.decodeInt();
    }

}
