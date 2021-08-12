package net.browny.common.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Arrays;

public class InPacket extends Packet {

    private ByteBuf byteBuf;

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
        for(int i = 0; i < amount; i++) {
            arr[i] = byteBuf.readByte();
        }
        return arr;
    }

    public int decodeInt() {
        return byteBuf.readIntLE();
    }

    public short decodeShort() {
        return byteBuf.readShortLE();
    }

    public String decodeString(int amount) {
        byte[] bytes = decodeArr(amount);
        char[] chars = new char[amount];
        for(int i = 0; i < amount; i++) {
            chars[i] = (char) bytes[i];
        }
        return String.valueOf(chars);
    }

    public String decodeString() {
        int amount = decodeShort();
        return decodeString(amount);
    }

    @Override
    public String toString() {
        return readableByteArray(Arrays.copyOfRange(getData(), getData().length - getUnreadAmount(), getData().length)); // Substring after copy of range xd
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

}