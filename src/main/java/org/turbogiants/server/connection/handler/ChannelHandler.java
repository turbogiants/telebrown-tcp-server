package org.turbogiants.server.connection.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import org.turbogiants.common.handler.EventHandler;
import org.turbogiants.common.packet.InPacket;
import org.turbogiants.common.packet.OutPacket;
import org.turbogiants.common.packet.PacketEnum;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.turbogiants.server.connection.packet.PacketHandler;
import org.turbogiants.server.user.NettyUser;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import static org.turbogiants.server.user.NettyUser.CLIENT_KEY;


public class ChannelHandler extends SimpleChannelInboundHandler<InPacket> {

    private static final Logger LOGGER = LogManager.getRootLogger();

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        NettyUser user = ctx.channel().attr(CLIENT_KEY).get();
        LOGGER.error("Client(" + ctx.channel().remoteAddress().toString().split(":")[0].substring(1) + ") " + "channelInactive");
        user.close();
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        NettyUser user = ctx.channel().attr(CLIENT_KEY).get();
        if (e instanceof IOException) {
            LOGGER.info("Client(" + ctx.channel().remoteAddress().toString().split(":")[0].substring(1) + ") forcibly closed the application.");
        } else {
            LOGGER.error("Client(" + ctx.channel().remoteAddress().toString().split(":")[0].substring(1) + ") " + Arrays.toString(e.getStackTrace()));
        }
        user.close(); //we should close the client it if it makes a problem
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InPacket inPacket) {
        NettyUser user = ctx.channel().attr(CLIENT_KEY).get();
        short opcode = inPacket.decodeShort();
        PacketEnum pEnum = PacketEnum.getHeaderByOP(opcode);
        if(pEnum != null){
            OutPacket oPacket = null;
            switch (Objects.requireNonNull(pEnum)) {
                case TCS_HANDSHAKE_REQ:
                {
                    oPacket = PacketHandler.Handler_TCS_HANDSHAKE_REQ(user, inPacket);
                    break;
                }
                case TCS_HEARTBEAT_REQ:
                {
                    oPacket = PacketHandler.Handler_TCS_HEARTBEAT_REQ();
                    break;
                }
                case TCS_USER_SET_ID_REQ:
                {
                    oPacket = PacketHandler.Handler_TCS_USER_SET_ID_REQ(user, inPacket);
                    if (oPacket == null)
                        user.close(); // setID is weird
                    else {
                        EventHandler.addEvent(() -> user.write(PacketHandler.Handler_TCS_HEARTBEAT_NOT()), 2500); // start doing heartbeat
                    }
                    break;
                }
                case TCS_COMM_MESSAGE_REQ:
                {
                    oPacket = PacketHandler.Handler_TCS_COMM_MESSAGE_REQ(inPacket);
                    break;
                }
                case TCS_SPAM_WARNING_NOT:
                {
                    oPacket = PacketHandler.Handler_TCS_SPAM_WARNING_NOT();
                    break;
                }
                case TCS_USER_IS_ONLINE_REQ:
                {
                    oPacket = PacketHandler.Handler_TCS_USER_IS_ONLINE_REQ(user, inPacket);
                    break;
                }

                default:
                    LOGGER.error("Invalid Packet ID : " + opcode + " - Client(" + ctx.channel().remoteAddress().toString().split(":")[0].substring(1) + ")");
                    user.close();
            }

            if (oPacket == null)
                user.close(); // packet error
            else {
                user.write(oPacket);
            }

        } else {
            LOGGER.error("Invalid Packet ID : " + opcode + " - Client(" + ctx.channel().remoteAddress().toString().split(":")[0].substring(1) + ")");
            user.close();
        }
        inPacket.release();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        NettyUser user = ctx.channel().attr(CLIENT_KEY).get();
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE ||
                    e.state() == IdleState.WRITER_IDLE ||
                    e.state() == IdleState.ALL_IDLE) {
                LOGGER.info("Client(" + ctx.channel().remoteAddress().toString().split(":")[0].substring(1) + ") will be disconnected due to inactivity.");
                user.close();
            }
        }
    }
}
