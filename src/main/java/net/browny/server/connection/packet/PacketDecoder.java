package net.browny.server.connection.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.browny.common.crypto.AESCrypto;
import net.browny.common.packet.InPacket;
import net.browny.common.user.NettyUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.spec.IvParameterSpec;
import java.security.GeneralSecurityException;
import java.util.List;


public class PacketDecoder extends ByteToMessageDecoder {

    private static final Logger LOGGER = LogManager.getRootLogger();

    private boolean readLength;
    private int length;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) {
        NettyUser nettyUser = channelHandlerContext.channel().attr(NettyUser.CLIENT_KEY).get();
        AESCrypto aesCrypto = channelHandlerContext.channel().attr(NettyUser.CRYPTO_KEY).get();
        if (nettyUser != null) {
            if (nettyUser.getStoredLength() == -1) {
                if (in.readableBytes() >= 16) {
                    ByteBuf iv = in.readBytes(16);
                    int length = in.readInt();
                    if (nettyUser.checkClientIV(iv.array())) {
                        LOGGER.error(String.format("[PacketDecoder] | Incorrect IV! Dropping client %s.", nettyUser.getIP()));
                        nettyUser.close();
                        return;
                    }
                    nettyUser.setStoredLength(length);
                } else {
                    return;
                }
            }
            if (in.readableBytes() >= nettyUser.getStoredLength()) {
                byte[] dec = new byte[nettyUser.getStoredLength()];
                in.readBytes(dec);
                nettyUser.setStoredLength(-1);

                try {
                    byte[] iv = nettyUser.getClientIV();
                    dec = aesCrypto.decrypt(dec, new IvParameterSpec(iv));
                    nettyUser.setClientIV(AESCrypto.generateIV());
                } catch (GeneralSecurityException e) {
                    LOGGER.error(e.getLocalizedMessage());
                }
                InPacket inPacket = new InPacket(dec);
                out.add(inPacket);
            }
        }
    }
}
