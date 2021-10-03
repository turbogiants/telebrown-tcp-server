package org.turbogiants.server.connection.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.turbogiants.common.packet.OutPacket;
import org.turbogiants.server.user.NettyUser;
import org.turbogiants.common.crypto.AESCrypto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.GeneralSecurityException;
import java.util.Arrays;

/**
 * Packet Decoder
 * Desc:
 * @author https://github.com/Raitou
 * @version 1.2
 * @since 1.0
 */
public final class PacketEncoder extends MessageToByteEncoder<OutPacket> {

    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Override method from MessageToByteEncoder please check netty.io documentation for more information
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, OutPacket outPacket, ByteBuf byteBuf) {
        byte[] data = outPacket.getData();
        NettyUser nettyUser = channelHandlerContext.channel().attr(NettyUser.CLIENT_KEY).get();
        AESCrypto aesCrypto = channelHandlerContext.channel().attr(NettyUser.CRYPTO_KEY).get();

        if (nettyUser != null) {
            try {
                byte[] sIV = AESCrypto.generateIV();
                byte[] cIV = AESCrypto.generateIV();
                nettyUser.acquireEncoderState();
                LOGGER.info("Encoder (Before Encryption): " + new String(data));
                data = aesCrypto.encrypt(data, sIV);
                LOGGER.info("Encoder (After Encryption): " + new String(data));
                nettyUser.setServerIV(sIV);
                nettyUser.setClientIV(cIV);

                byteBuf.writeBytes(sIV);
                byteBuf.writeBytes(cIV);
                byteBuf.writeInt(data.length);
                byteBuf.writeBytes(data);
            } catch (GeneralSecurityException e) {
                LOGGER.error(Arrays.toString(e.getStackTrace()));
            } finally {
                nettyUser.releaseEncodeState();
            }
        } else {
            LOGGER.debug("[PacketEncoder] | Plain sending " + outPacket);
            byteBuf.writeBytes(data);
        }
    }
}
