package net.browny.server.connection.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import net.browny.server.client.NettyClient;
import net.browny.server.connection.crypto.AESCrypto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;


public class PacketDecoder extends ReplayingDecoder<Void> {

    private static final Logger LOGGER = LogManager.getRootLogger();

    private boolean readLength;
    private int length;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {


        NettyClient nettyClient = channelHandlerContext.channel().attr(NettyClient.CLIENT_KEY).get();
        AESCrypto aesCrypto = channelHandlerContext.channel().attr(NettyClient.CRYPTO_KEY).get();
        if (nettyClient != null) {
//            // todo: IV checking
//            if (nettyClient.getStoredLength() == -1) {
//                //todo: Replay Masking
//                LOGGER.info("[Packet Decoder] Testing");
//            }
            // retrieved example from https://netty.io/4.1/api/io/netty/handler/codec/ReplayingDecoder.html
            byte[] IV = nettyClient.getRecvIV();
            if (!readLength) {
                length = byteBuf.readInt();
                readLength = true;
                checkpoint();
            }
            if (readLength) {
                ByteBuf frame = byteBuf.readBytes(length);
                readLength = false;
                checkpoint();
                list.add(frame);
            }
        }
    }
}
