package org.turbogiants.common.packet.definitions;

import org.turbogiants.common.packet.InPacket;
import org.turbogiants.common.packet.OutPacket;
import org.turbogiants.common.packet.PacketEnum;

import java.util.Arrays;

public class TCS_HANDSHAKE_REQ {

    private final String[] secret_server = {
            "愛されなくても君がいる",
            "ピノキオピー",
            "初音ミク"
    };

    private String[] secret_client;

    public boolean isKeyMatch(){
        return Arrays.equals(secret_server, secret_client);
    }

    public OutPacket serialize(PacketEnum header){
        OutPacket outPacket = new OutPacket(header);
        outPacket.encodeString(secret_server[0]);
        outPacket.encodeString(secret_server[1]);
        outPacket.encodeString(secret_server[2]);
        return outPacket;
    }

    public void deserialize(InPacket inPacket){
        secret_client = new String[3];
        secret_client[0] = inPacket.decodeString();
        secret_client[1] = inPacket.decodeString();
        secret_client[2] = inPacket.decodeString();
    }


}
