package net.browny.server.connection.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.browny.server.connection.packet.InPacket;


public class SessionHandler extends SimpleChannelInboundHandler<InPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, InPacket inPacket) throws Exception {

    }
}
