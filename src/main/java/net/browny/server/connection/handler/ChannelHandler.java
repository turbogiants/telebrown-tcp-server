package net.browny.server.connection.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.browny.common.packet.InPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


public class SessionHandler extends SimpleChannelInboundHandler<InPacket> {

    private static final Logger LOGGER = LogManager.getRootLogger();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        if (e instanceof IOException) {
            LOGGER.info("Client forcibly closed the application.");
        } else {
            LOGGER.error(e.getStackTrace());
        }
        ctx.close(); //we should close the client it if it makes a problem
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, InPacket inPacket) {

    }
}
