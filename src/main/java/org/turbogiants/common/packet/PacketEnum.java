package org.turbogiants.common.packet;

public enum PacketEnum {
    // Handshake
    TCS_HANDSHAKE_NOT(0),
    TCS_HANDSHAKE_REQ(1),
    TCS_HANDSHAKE_ACK(2),

    // Heartbeat
    TCS_HEARTBEAT_NOT(3),
    TCS_HEARTBEAT_REQ(4),
    TCS_HEARTBEAT_ACK(5),

    // User
    TCS_USER_SET_ID_REQ(6),
    TCS_USER_SET_ID_ACK(7),

    ;

    private final short packetID;

    PacketEnum(int packetID) {
        this.packetID = (short) packetID;
    }

    public static PacketEnum getHeaderByOP(int packetID) {
        for (PacketEnum serverEnum : PacketEnum.values()) {
            if (serverEnum.getPacketID() == packetID) {
                return serverEnum;
            }
        }
        return null;
    }

    public short getPacketID() {
        return packetID;
    }
}
