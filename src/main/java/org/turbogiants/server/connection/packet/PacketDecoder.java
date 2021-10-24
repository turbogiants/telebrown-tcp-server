package org.turbogiants.server.connection.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.turbogiants.common.crypto.AESCrypto;
import org.turbogiants.common.packet.InPacket;
import org.turbogiants.common.packet.Packet;
import org.turbogiants.server.user.NettyUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.turbogiants.server.user.UserDef;

import javax.crypto.spec.IvParameterSpec;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

/**
 * Packet Decoder
 * Desc:
 * @author https://github.com/Raitou
 * @version 1.2
 * @since 1.0
 */
public class PacketDecoder extends ByteToMessageDecoder {

    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Override method from ByteToMessageDecoder please check netty.io documentation for more information
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) {
        NettyUser nettyUser = channelHandlerContext.channel().attr(NettyUser.CLIENT_KEY).get();
        AESCrypto aesCrypto = channelHandlerContext.channel().attr(NettyUser.CRYPTO_KEY).get();
        if (nettyUser != null) {
            try{
                nettyUser.acquireDecoderState();
                boolean isSpam = false;
                byte[] clientIV = new byte[16];
                if (nettyUser.getStoredLength() == -1) {
                    if (in.readableBytes() >= 16) {
                        in.readBytes(clientIV);
                        int length = in.readInt();
                    if (nettyUser.checkClientIV(clientIV)) {
                        isSpam = true;
                        nettyUser.getUserDef().addSpamCnt();
                        LOGGER.warn(String.format("[PacketDecoder] | Incorrect IV! Warning Client %s (%d/%d).",
                                nettyUser.getIP(),
                                nettyUser.getUserDef().getSpamCnt(),
                                UserDef.SPAM_THRESHOLD));
                        if(nettyUser.getUserDef().isSpamThresholdMet()) {
                            LOGGER.error(String.format("[PacketDecoder] | Incorrect IV! Dropping client %s.", nettyUser.getIP()));
                            nettyUser.close();
                            return;
                        }
                    }
                        nettyUser.setStoredLength(length);
                    } else {
                        return;
                    }
                }
                if (in.readableBytes() >= nettyUser.getStoredLength()) {
                    byte[] data = new byte[nettyUser.getStoredLength()];
                    in.readBytes(data);
                    nettyUser.setStoredLength(-1);

                    if (isSpam) {
                    try {
                        data = aesCrypto.decrypt(data, new IvParameterSpec(clientIV));
                    } catch (GeneralSecurityException e) {
                        LOGGER.error(Arrays.toString(e.getStackTrace()));
                    }
                    byte[] dataSpam = new byte[]{
                            0x0B, 0x00 //Spam Packet ID
                    };
                    InPacket inPacket = new InPacket(data);
                    out.add(inPacket);
                    InPacket inPacketSpam = new InPacket(dataSpam);
                    out.add(inPacketSpam);
                    } else {
                    try {
                        LOGGER.info("Decoder (Before Decryption): " + Packet.readableByteArray(data));
                        data = aesCrypto.decrypt(data, new IvParameterSpec(nettyUser.getClientIV()));
                    } catch (GeneralSecurityException e) {
                        LOGGER.error(Arrays.toString(e.getStackTrace()));
                    }
                        if(nettyUser.getUserDef() != null)
                            nettyUser.getUserDef().reduceSpamCnt();
                        InPacket inPacket = new InPacket(data);
                        inPacket.setHeader(inPacket.decodeShort());
                        LOGGER.info("Decoder (After Decryption): " + inPacket);
                        out.add(inPacket);
                    }
                }
            } finally {
                nettyUser.releaseDecoderState();
            }

        }
    }
}
