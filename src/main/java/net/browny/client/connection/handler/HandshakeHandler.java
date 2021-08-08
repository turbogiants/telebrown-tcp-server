package net.browny.client.connection.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.browny.common.packet.InPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class HandshakeHandler extends SimpleChannelInboundHandler<InPacket> {

    private static final Logger LOGGER = LogManager.getRootLogger();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        LOGGER.error(e.getLocalizedMessage());
        ctx.close(); //disconnect to server if we have a problem
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, InPacket inPacket) {
        byte[] serverIV = inPacket.decodeArr(16);
        byte[] clientIV = inPacket.decodeArr(16);
        LOGGER.info("serverIV: " + Arrays.toString(serverIV));
        LOGGER.info("clientIV: " + Arrays.toString(clientIV));

    }
}
