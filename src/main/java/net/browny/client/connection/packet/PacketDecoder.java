package net.browny.client.connection.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.browny.common.crypto.AESCrypto;
import net.browny.common.packet.InPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import static net.browny.client.Client.getAESCryptoInstance;

public class PacketDecoder extends ByteToMessageDecoder {

    private static final Logger LOGGER = LogManager.getRootLogger();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) {
        /*
        *  1 (0) ~ 16 (15) = serverIV
        *  17 (16) ~ 32 (31) = clientIV
        *  33 ~ 36 = packetLength
        *  37 ~ = packetID and data
        * */
        if (in.readableBytes() >= 32) {
            //set IV
            byte[] serverIV = new byte[16];
            byte[] clientIV = new byte[16];
            int packetLength = 0;
            in.readBytes(serverIV);
            in.readBytes(clientIV);
            AESCrypto aesCrypto = getAESCryptoInstance();
            aesCrypto.setServertIV(serverIV);
            aesCrypto.setClientIV(clientIV);
            //packet_len
            if (in.readableBytes() >= 4) {
                packetLength = in.readInt();
                //packet_id
                if (in.readableBytes() >= packetLength) {
                    byte[] data = new byte[packetLength];
                    in.readBytes(data);
                    try {
                        data = aesCrypto.decrypt(data, aesCrypto.getServerIV());
                        InPacket inPacket = new InPacket(data);
                        out.add(inPacket); // send to channel handler
                    } catch (GeneralSecurityException e) {
                        LOGGER.error(e.getLocalizedMessage());
                    }
                }
            }
        }
    }
}
