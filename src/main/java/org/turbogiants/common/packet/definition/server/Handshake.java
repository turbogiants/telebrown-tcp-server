package org.turbogiants.common.packet.definition.server;


import org.turbogiants.common.crypto.AESCrypto;
import org.turbogiants.common.packet.OutPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.GeneralSecurityException;

public class Handshake {

    private static final Logger LOGGER = LogManager.getRootLogger();

    public static OutPacket Handler_TCS_HANDSHAKE_NOT(byte[] serverIV, byte[] clientIV){
        OutPacket ivPacket = new OutPacket();
        ivPacket.encodeArr(serverIV);
        ivPacket.encodeArr(clientIV);
        ivPacket.encodeIntBE(16); //even thou the size of short is 2 AES will pad it to 16

        //Encrypt Packet - This is just special case the rest of the packets will be automatically encrypted/decrypted
        AESCrypto aesCrypto = new AESCrypto();
        byte[] data = {0, 0}; // dummy opcode 0;
        try {
            data = aesCrypto.encrypt(data, serverIV);
        } catch (GeneralSecurityException e) {
            LOGGER.error(e.getLocalizedMessage());
        }

        ivPacket.encodeArr(data);
        return ivPacket;
    }
}
