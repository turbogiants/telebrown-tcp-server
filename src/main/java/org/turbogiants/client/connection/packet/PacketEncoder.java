package org.turbogiants.client.connection.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.turbogiants.common.crypto.AESCrypto;
import org.turbogiants.common.packet.Packet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.turbogiants.client.Client;

import java.security.GeneralSecurityException;

public final class PacketEncoder extends MessageToByteEncoder<Packet> {
    private static final Logger LOGGER = LogManager.getRootLogger();

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet outPacket, ByteBuf byteBuf) {
        AESCrypto aesCrypto = Client.getAESCryptoInstance();
        byte[] iv = aesCrypto.getClientIV();
        byte[] data = outPacket.getData();
        try {
            byteBuf.writeBytes(iv);
            byteBuf.writeInt(outPacket.getLength());
            data = aesCrypto.encrypt(data, iv);
            byteBuf.writeBytes(data);
        } catch (GeneralSecurityException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
    }
}
