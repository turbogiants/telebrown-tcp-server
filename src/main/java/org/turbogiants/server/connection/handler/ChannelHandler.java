package org.turbogiants.server.connection.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import org.turbogiants.common.packet.InPacket;
import org.turbogiants.common.packet.OutPacket;
import org.turbogiants.common.packet.PacketEnum;
import org.turbogiants.common.packet.definition.server.CUser;
import org.turbogiants.common.packet.definition.server.Handshake;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.turbogiants.common.packet.definition.server.Heartbeat;
import org.turbogiants.common.user.User;

import java.io.IOException;

import static org.turbogiants.common.user.NettyUser.CLIENT_KEY;


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
    protected void channelRead0(ChannelHandlerContext ctx, InPacket inPacket) {
        User user = (User) ctx.channel().attr(CLIENT_KEY).get();
        short opcode = inPacket.decodeShort();
        PacketEnum packetEnum = PacketEnum.getHeaderByOP(opcode);
        if (packetEnum == null){
            LOGGER.error("Invalid Packet ID : " + opcode + " - Client(" + ctx.channel().remoteAddress().toString().split(":")[0].substring(1) + ")");
            ctx.close();
            return;
        }

        switch(opcode){
            case 1: //TCS_HANDSHAKE_REQ
            {
                OutPacket oPacket = Handshake.Handler_TCS_HANDSHAKE_REQ(user, inPacket);
                if(oPacket == null)
                    user.close(); // handshake failed
                else {
                    user.write(oPacket);
                    user.write(Heartbeat.Handler_TCS_HEARTBEAT_NOT()); // start doing heartbeat
                }
                break;
            }
            case 4: //TCS_HEARTBEAT_REQ
            {
                user.write(Heartbeat.Handler_TCS_HEARTBEAT_REQ());
                break;
            }
            case 6: //Handler_TCS_USER_SET_ID_REQ
            {
                user.write(CUser.Handler_TCS_USER_SET_ID_REQ(user, inPacket));
                break;
            }
            default:
                LOGGER.error("Invalid Packet ID : " + opcode + " - Client(" + ctx.channel().remoteAddress().toString().split(":")[0].substring(1) + ")");
                ctx.close();
        }
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
