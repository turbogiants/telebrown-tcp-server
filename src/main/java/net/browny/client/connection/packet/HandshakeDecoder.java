package net.browny.client.connection.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.browny.common.packet.InPacket;

import java.util.List;

public class HandshakeDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) {
        if (in.readableBytes() >= 32) {
            byte[] dec = new byte[32];
            in.readBytes(dec);
            InPacket inPacket = new InPacket(dec);
            out.add(inPacket);
        }
    }
}
