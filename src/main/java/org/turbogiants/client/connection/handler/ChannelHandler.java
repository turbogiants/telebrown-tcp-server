package org.turbogiants.client.connection.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.turbogiants.client.connection.packet.PacketHandler;
import org.turbogiants.common.handler.EventHandler;
import org.turbogiants.common.packet.InPacket;
import org.turbogiants.common.packet.PacketEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Objects;

import static org.turbogiants.client.connection.network.ClientInit.socketChannel;


public class ChannelHandler extends SimpleChannelInboundHandler<InPacket> {

    private static final Logger LOGGER = LogManager.getRootLogger();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        LOGGER.error(Arrays.toString(e.getStackTrace()));
        ctx.close(); //disconnect to server if we have a problem
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InPacket inPacket) {
        short opcode = inPacket.decodeShort();
        PacketEnum pEnum = PacketEnum.checkHeaderByOP(opcode);
        if(pEnum != null){
            switch (Objects.requireNonNull(pEnum)) {
                case TCS_HANDSHAKE_NOT:
                {
                    socketChannel.writeAndFlush(PacketHandler.Handler_TCS_HANDSHAKE_NOT());
                    break;
                }
                case TCS_HANDSHAKE_ACK:
                {
                    PacketHandler.Handler_TCS_HANDSHAKE_ACK(inPacket);
                    break;
                }
                case TCS_HEARTBEAT_NOT:
                {
                    EventHandler.addFixedRateEvent(
                            () -> socketChannel.writeAndFlush(PacketHandler.Handler_TCS_HEARTBEAT_NOT()),
                            160000, 160000);
                    break;
                }
                case TCS_HEARTBEAT_ACK:
                {
                    PacketHandler.Handler_TCS_HEARTBEAT_ACK();
                    break;
                }
                case TCS_USER_SET_ID_ACK:
                {
                    PacketHandler.Handler_TCS_USER_SET_ID_ACK(inPacket);
                    break;
                }
                case TCS_COMM_MESSAGE_NOT:
                {
                    PacketHandler.Handler_TCS_COMM_MESSAGE_NOT(inPacket);
                    break;
                }
                case TCS_COMM_MESSAGE_ACK:
                {
                    PacketHandler.Handler_TCS_COMM_MESSAGE_ACK(inPacket);
                    break;
                }
                case TCS_SPAM_WARNING_NOT:
                {
                    PacketHandler.Handler_TCS_SPAM_WARNING_NOT(false);
                    break;
                }
                case TCS_USER_IS_ONLINE_ACK:
                {
                    PacketHandler.Handler_TCS_USER_IS_ONLINE_ACK(inPacket);
                    break;
                }
                case TCS_COMM_2_MESSAGE_ACK:
                {
                    break;
                }
                default:
                    LOGGER.error("Invalid Packet ID : " + opcode + " - Client Closing");
                    ctx.close();
            }
        } else {
            LOGGER.error("Invalid Packet ID : " + opcode + " - Client Closing");
            ctx.close();
        }
       inPacket.release();
    }
}
