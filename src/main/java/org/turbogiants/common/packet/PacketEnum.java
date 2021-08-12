package org.turbogiants.common.packet;

public enum PacketEnum {
    TCS_HANDSHAKE_NOT(0),
    TCS_HANDSHAKE_REQ(1),
    TCS_HANDSHAKE_ACK(2),


    //Test Packet
    TCS_TEST_PACKET(1001),

    ;

    private short packetID;

    PacketEnum(int packetID) {
        this.packetID = (short) packetID;
    }

    public short getPacketID() {
        return packetID;
    }

    public static PacketEnum getHeaderByOP(int packetID) {
        for (PacketEnum serverEnum : PacketEnum.values()) {
            if (serverEnum.getPacketID() == packetID) {
                return serverEnum;
            }
        }
        return null;
    }
}
