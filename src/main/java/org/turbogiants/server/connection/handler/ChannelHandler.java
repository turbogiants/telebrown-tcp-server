package net.browny.server.connection.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import net.browny.common.packet.InPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


public class ChannelHandler extends SimpleChannelInboundHandler<InPacket> {

    private static final Logger LOGGER = LogManager.getRootLogger();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        if (e instanceof IOException) {
            LOGGER.info("Client(" + ctx.channel().remoteAddress().toString().split(":")[0].substring(1) + ") forcibly closed the application.");
        } else {
            LOGGER.error("Client(" + ctx.channel().remoteAddress().toString().split(":")[0].substring(1) + ") " + e.getLocalizedMessage());
        }
        ctx.close(); //we should close the client it if it makes a problem
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, InPacket inPacket) {

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE ||
                    e.state() == IdleState.WRITER_IDLE ||
                    e.state() == IdleState.ALL_IDLE) {
                LOGGER.info("Client(" + ctx.channel().remoteAddress().toString().split(":")[0].substring(1) + ") will be disconnected due to inactivity.");
                ctx.close();
            }
        }
    }
}
