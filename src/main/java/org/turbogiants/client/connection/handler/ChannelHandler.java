package org.turbogiants.client.connection.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.turbogiants.common.handler.EventHandler;
import org.turbogiants.common.packet.InPacket;
import org.turbogiants.common.packet.PacketEnum;
import org.turbogiants.common.packet.definition.client.CUser;
import org.turbogiants.common.packet.definition.client.Handshake;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.turbogiants.common.packet.definition.client.Heartbeat;

import static org.turbogiants.client.connection.network.ClientInit.socketChannel;


public class ChannelHandler extends SimpleChannelInboundHandler<InPacket> {

    private static final Logger LOGGER = LogManager.getRootLogger();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        LOGGER.error(e.getLocalizedMessage());
        ctx.close(); //disconnect to server if we have a problem
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InPacket inPacket) {
        short opcode = inPacket.decodeShort();
        PacketEnum packetEnum = PacketEnum.getHeaderByOP(opcode);
        if (packetEnum == null){
            LOGGER.error("Invalid Packet ID : " + opcode + " - Client Closing");
            ctx.close();
            return;
        }
//        byte[] serverIV = inPacket.decodeArr(16);
//        byte[] clientIV = inPacket.decodeArr(16);
//        LOGGER.info("opcode: " + opcode);
//        LOGGER.info("serverIV: " + Arrays.toString(serverIV));
//        LOGGER.info("clientIV: " + Arrays.toString(clientIV));

        switch(opcode){
            case 0: //TCS_HANDSHAKE_NOT
            {
                socketChannel.writeAndFlush(Handshake.Handler_TCS_HANDSHAKE_NOT());
                break;
            }
            case 2: //TCS_HANDSHAKE_ACK
            {
                Handshake.Handler_TCS_HANDSHAKE_ACK();
                break;
            }
            case 3: //TCS_HEARTBEAT_NOT
            {
                socketChannel.writeAndFlush(Heartbeat.Handler_TCS_HEARTBEAT_NOT());
                break;
            }
            case 5: //TCS_HEARTBEAT_ACK
            {
                EventHandler.addEvent(
                        () -> socketChannel.writeAndFlush(Heartbeat.Handler_TCS_HEARTBEAT_ACK()),
                        10000);
                break;
            }
            case 7: //TCS_USER_SET_ID_ACK
            {
                CUser.Handler_TCS_USER_SET_ID_ACK(inPacket);
                break;
            }
            default:
                LOGGER.error("Invalid Packet ID : " + opcode + " - Client Closing");
                ctx.close();
        }

    }
}
