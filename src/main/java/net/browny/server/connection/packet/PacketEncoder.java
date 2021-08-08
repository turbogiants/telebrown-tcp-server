package net.browny.server.connection.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.browny.server.client.NettyClient;
import net.browny.server.connection.crypto.AESCrypto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.GeneralSecurityException;

public final class PacketEncoder extends MessageToByteEncoder<Packet> {

    private static final Logger LOGGER = LogManager.getRootLogger();

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet outPacket, ByteBuf byteBuf) {
        byte[] data = outPacket.getData();
        NettyClient nettyClient = channelHandlerContext.channel().attr(NettyClient.CLIENT_KEY).get();
        AESCrypto aesCrypto = channelHandlerContext.channel().attr(NettyClient.CRYPTO_KEY).get();

        if (nettyClient != null) {
            try {
                byte[] iv = AESCrypto.generateIV();
                nettyClient.acquireEncoderState();
                aesCrypto.encrypt(data, iv);
                nettyClient.setServerIV(iv);

                byteBuf.writeBytes(iv);
                byteBuf.writeInt(outPacket.getLength());
                byteBuf.writeBytes(data);
            } catch (GeneralSecurityException e) {
                LOGGER.error(e.getStackTrace());
            } finally{
                nettyClient.releaseEncodeState();
            }
        } else {
            LOGGER.debug("[PacketEncoder] | Plain sending " + outPacket);
            byteBuf.writeBytes(data);
        }
    }
}
