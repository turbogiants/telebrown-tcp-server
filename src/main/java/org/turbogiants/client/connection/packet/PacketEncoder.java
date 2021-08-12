package net.browny.client.connection.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.browny.common.crypto.AESCrypto;
import net.browny.common.packet.Packet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.GeneralSecurityException;

import static net.browny.client.Client.getAESCryptoInstance;

public final class PacketEncoder extends MessageToByteEncoder<Packet> {
    private static final Logger LOGGER = LogManager.getRootLogger();

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet outPacket, ByteBuf byteBuf) {
        AESCrypto aesCrypto = getAESCryptoInstance();
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
