package org.turbogiants.common.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class InPacket extends Packet {

    private final ByteBuf byteBuf;

    private short op;

    public InPacket(ByteBuf data) {
        super(data.array());
        this.byteBuf = data.copy();
    }

    public InPacket() {
        this(Unpooled.buffer());
    }

    public InPacket(byte[] data) {
        this(Unpooled.copiedBuffer(data));
    }

    public InPacket(byte[] data, short op) {
        this(Unpooled.copiedBuffer(data));
        this.op = op;
    }

    @Override
    public int getHeader() {
        return op;
    }

    public void setHeader(short op) {
        this.op = op;
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

    public byte decodeByte() {
        return byteBuf.readByte();
    }

    public short decodeUByte() {
        return byteBuf.readUnsignedByte();
    }

    public byte[] decodeArr(int amount) {
        byte[] arr = new byte[amount];
        for (int i = 0; i < amount; i++) {
            arr[i] = byteBuf.readByte();
        }
        return arr;
    }

    public byte[] decodeArr() {
        int len = decodeShort();
        return decodeArr(len);
    }


    public int decodeInt() {
        return byteBuf.readIntLE();
    }

    public int decodeIntBE() {
        return byteBuf.readInt();
    }

    public short decodeShort() {
        return byteBuf.readShortLE();
    }

    public String decodeString() {
        byte[] bytes = decodeArr();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public long decodeLong() {
        return byteBuf.readLongLE();
    }

    public int getUnreadAmount() {
        return byteBuf.readableBytes();
    }

    public void release() {
        byteBuf.release();
    }

    @Override
    public String toString() {
        return String.format("%s, %s/0x%s\t| %s", PacketEnum.checkHeaderByOP(op), op, Integer.toHexString(op).toUpperCase()
                , readableByteArray(Arrays.copyOfRange(getData(), 2, getData().length)));
    }

}
