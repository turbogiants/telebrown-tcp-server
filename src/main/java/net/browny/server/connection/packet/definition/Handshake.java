package net.browny.server.connection.packet.definition;

import net.browny.server.connection.packet.OutPacket;

public class Handshake {

    public static OutPacket initializeCommunication(byte[] sIV, byte[] rIV){
        OutPacket oPacket = new OutPacket();

        oPacket.encodeArr(sIV);
        oPacket.encodeArr(rIV);

        return oPacket;
    }

}
