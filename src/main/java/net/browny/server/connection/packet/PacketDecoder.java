package net.browny.server.connection.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.browny.server.client.NettyClient;
import net.browny.server.connection.crypto.AESCrypto;
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
        NettyClient nettyClient = channelHandlerContext.channel().attr(NettyClient.CLIENT_KEY).get();
        AESCrypto aesCrypto = channelHandlerContext.channel().attr(NettyClient.CRYPTO_KEY).get();
        if (nettyClient != null) {
            if (nettyClient.getStoredLength() == -1) {
                if (in.readableBytes() >= 16) {
                    ByteBuf iv = in.readBytes(16);
                    int length = in.readInt();
                    if (nettyClient.checkClientIV(iv.array())) {
                        LOGGER.error(String.format("[PacketDecoder] | Incorrect IV! Dropping client %s.", nettyClient.getIP()));
                        nettyClient.close();
                        return;
                    }
                    nettyClient.setStoredLength(length);
                } else {
                    return;
                }
            }
            if (in.readableBytes() >= nettyClient.getStoredLength()) {
                byte[] dec = new byte[nettyClient.getStoredLength()];
                in.readBytes(dec);
                nettyClient.setStoredLength(-1);

                try {
                    byte[] iv = nettyClient.getClientIV();
                    dec = aesCrypto.decrypt(dec, new IvParameterSpec(iv));
                    nettyClient.setClientIV(AESCrypto.generateIV());
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
                InPacket inPacket = new InPacket(dec);
                out.add(inPacket);
            }
        }
    }
}
