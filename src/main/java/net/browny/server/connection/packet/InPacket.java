package net.browny.server.connection.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class InPacket extends Packet {

    private ByteBuf byteBuf;

    public InPacket(ByteBuf data) {
        super(data.array());
        this.byteBuf = byteBuf.copy();
    }

    public InPacket() {
        this(Unpooled.buffer());
    }

    @Override
    public int getLength() {
        return byteBuf.readableBytes();
    }

    @Override
    public byte[] getData() {
        return byteBuf.array();
    }

    @Override
    public InPacket clone() {
        return new InPacket(byteBuf);
    }

}
