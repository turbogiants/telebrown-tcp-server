package org.turbogiants.server.connection.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.turbogiants.common.packet.Packet;
import org.turbogiants.common.user.NettyUser;
import org.turbogiants.common.crypto.AESCrypto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.GeneralSecurityException;

public final class PacketEncoder extends MessageToByteEncoder<Packet> {

    private static final Logger LOGGER = LogManager.getRootLogger();

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet outPacket, ByteBuf byteBuf) {
        byte[] data = outPacket.getData();
        NettyUser nettyUser = channelHandlerContext.channel().attr(NettyUser.CLIENT_KEY).get();
        AESCrypto aesCrypto = channelHandlerContext.channel().attr(NettyUser.CRYPTO_KEY).get();

        if (nettyUser != null) {
            try {
                byte[] iv = AESCrypto.generateIV();
                nettyUser.acquireEncoderState();
                data = aesCrypto.encrypt(data, iv);
                nettyUser.setServerIV(iv);

                byteBuf.writeBytes(iv);
                byteBuf.writeInt(outPacket.getLength());
                byteBuf.writeBytes(data);
            } catch (GeneralSecurityException e) {
                LOGGER.error(e.getLocalizedMessage());
            } finally{
                nettyUser.releaseEncodeState();
            }
        } else {
            LOGGER.debug("[PacketEncoder] | Plain sending " + outPacket);
            byteBuf.writeBytes(data);
        }
    }
}
