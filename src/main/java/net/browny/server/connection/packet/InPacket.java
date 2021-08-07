package net.browny.server.connection.packet;

import io.netty.buffer.ByteBuf;

public class InPacket extends Packet {

    private ByteBuf byteBuf;

    public InPacket(ByteBuf data) {
        super(data.array());
        this.byteBuf = byteBuf.copy();
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
