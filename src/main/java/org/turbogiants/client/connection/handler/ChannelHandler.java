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
        PacketEnum packetEnum = PacketEnum.getHeaderByOP(opcode);
        if (packetEnum == null) {
            LOGGER.error("Invalid Packet ID : " + opcode + " - Client Closing");
            ctx.close();
            return;
        }

        switch (opcode) {
            case 0: //TCS_HANDSHAKE_NOT
            {
                socketChannel.writeAndFlush(PacketHandler.Handler_TCS_HANDSHAKE_NOT());
                break;
            }
            case 2: //TCS_HANDSHAKE_ACK
            {
                PacketHandler.Handler_TCS_HANDSHAKE_ACK(inPacket);
                break;
            }
            case 3: //TCS_HEARTBEAT_NOT
            {
                EventHandler.addFixedRateEvent(
                        () -> socketChannel.writeAndFlush(PacketHandler.Handler_TCS_HEARTBEAT_NOT()),
                        15000, 15000);
                break;
            }
            case 5: //TCS_HEARTBEAT_ACK
            {
                PacketHandler.Handler_TCS_HEARTBEAT_ACK();
                break;
            }
            case 7: //TCS_USER_SET_ID_ACK
            {
                PacketHandler.Handler_TCS_USER_SET_ID_ACK(inPacket);
                break;
            }
            case 8: //TCS_COMM_MESSAGE_NOT
            {
                PacketHandler.Handler_TCS_COMM_MESSAGE_NOT(inPacket);
                break;
            }
            case 10: //TCS_COMM_MESSAGE_ACK
            {
                PacketHandler.Handler_TCS_COMM_MESSAGE_ACK(inPacket);
                break;
            }
            case 11: //TCS_COMM_MESSAGE_ACK
            {
                PacketHandler.Handler_TCS_SPAM_WARNING_NOT(false);
                break;
            }
            default:
                LOGGER.error("Invalid Packet ID : " + opcode + " - Client Closing");
                ctx.close();
        }

    }
}
