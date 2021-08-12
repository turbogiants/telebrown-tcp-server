package net.browny.client.connection.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.browny.common.packet.InPacket;
import net.browny.common.packet.PacketEnum;
import net.browny.common.packet.definition.client.Handshake;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


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
                ctx.writeAndFlush(Handshake.Handler_TCS_HANDSHAKE_REQ());
                break;
            }
            case 2: //TCS_HANDSHAKE_ACK
            {

                break;
            }
            default:
                LOGGER.error("Invalid Packet ID : " + opcode + " - Client Closing");
                ctx.close();
                return;
        }

    }
}
