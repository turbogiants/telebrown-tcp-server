package org.turbogiants.common.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class OutPacket extends Packet {
    private static final Logger LOGGER = LogManager.getRootLogger();
    private final ByteBuf byteBuf;
    private short op;

    public OutPacket(short op) {
        super(new byte[]{});
        byteBuf = PooledByteBufAllocator.DEFAULT.buffer();
        encodeShort(op);
        this.op = op;
    }

    public OutPacket(int op) {
        this((short) op);
    }

    public OutPacket() {
        super(new byte[]{});
        byteBuf = ByteBufAllocator.DEFAULT.buffer();
    }

    public OutPacket(byte[] data) {
        super(data);
        byteBuf = ByteBufAllocator.DEFAULT.buffer();
        encodeArr(data);
    }

    public OutPacket(PacketEnum header) {
        this(header.getPacketID());
    }

    @Override
    public int getHeader() {
        return op;
    }

    public void encodeByte(int b) {
        encodeByte((byte) b);
    }

    public void encodeByte(byte b) {
        byteBuf.writeByte(b);
    }

    public void encodeArr(byte[] bArr) {
        encodeShort((short) bArr.length);
        byteBuf.writeBytes(bArr);
    }

    public void encodeArr(String arr) {
        encodeArr(getByteArrayByString(arr));
    }

    public void encodeChar(char c) {
        byteBuf.writeByte(c);
    }

    public void encodeByte(boolean b) {
        byteBuf.writeBoolean(b);
    }

    public void encodeShort(short s) {
        byteBuf.writeShortLE(s);
    }

    public void encodeShortBE(short s) {
        byteBuf.writeShort(s);
    }

    public void encodeIntBE(int i) {
        byteBuf.writeInt(i);
    }

    public void encodeInt(int i) {
        byteBuf.writeIntLE(i);
    }

    public void encodeLong(long l) {
        byteBuf.writeLongLE(l);
    }

    public void encodeString(String s) {
        if (s == null) {
            s = "";
        }
        int stringLen = s.getBytes(StandardCharsets.UTF_8).length;
        if (stringLen > Short.MAX_VALUE) {
            LOGGER.error("Tried to encode a string that is too big.");
            return;
        }
        if (s.length() > 0) {
            encodeArr(s.getBytes(StandardCharsets.UTF_8));
        }
    }

    @Override
    public byte[] getData() {
        if (byteBuf.hasArray()) {
            return byteBuf.array();
        } else {
            byte[] arr = new byte[byteBuf.writerIndex()];
            byteBuf.nioBuffer().get(arr, 0, byteBuf.writerIndex());
            return arr;
        }
    }

    @Override
    public void setData(byte[] nD) {
        super.setData(nD);
        byteBuf.clear();
        encodeArr(nD);
    }

    @Override
    public Packet clone() {
        return new OutPacket(getData());
    }

    @Override
    public int getLength() {
        return getData().length;
    }

    @Override
    public String toString() {
        return String.format("%s, %s/0x%s\t| %s", PacketEnum.checkHeaderByOP(op), op, Integer.toHexString(op).toUpperCase()
                , readableByteArray(Arrays.copyOfRange(getData(), 2, getData().length)));
    }

    public void encodeShort(int value) {
        encodeShort((short) value);
    }

    public void encodeString(String name, int length) {
        encodeString(name, (short) length);
    }


}

