package net.browny.server.connection.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.Arrays;

public class ServerPacket extends Packet {
    private ByteBuf byteBuf;
    private boolean loopback = false;
    private boolean encryptedByShanda = false;
    private short op;
    private static final Logger LOGGER = LogManager.getRootLogger();

    public ServerPacket(short op) {
        super(new byte[]{});
        byteBuf = PooledByteBufAllocator.DEFAULT.buffer();
        encodeShort(op);
        this.op = op;
    }

    public ServerPacket(int op) {
        this((short) op);
    }

    public ServerPacket() {
        super(new byte[]{});
        byteBuf = ByteBufAllocator.DEFAULT.buffer();
    }

    public ServerPacket(byte[] data) {
        super(data);
        byteBuf = ByteBufAllocator.DEFAULT.buffer();
        encodeArr(data);
    }

    public ServerPacket(PacketEnum header) {
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
        if (s.length() > Short.MAX_VALUE) {
            LOGGER.error("Tried to encode a string that is too big.");
            return;
        }
        encodeShort((short) s.length());
        encodeString(s, (short) s.length());
    }

    public void encodeString(String s, short length) {
        if (s == null) {
            s = "";
        }
        if (s.length() > 0) {
            for (char c : s.toCharArray()) {
                encodeChar(c);
            }
        }
        for (int i = s.length(); i < length; i++) {
            encodeByte((byte) 0);
        }
    }

    @Override
    public void setData(byte[] nD) {
        super.setData(nD);
        byteBuf.clear();
        encodeArr(nD);
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
    public Packet clone() {
        return new ServerPacket(getData());
    }

    @Override
    public int getLength() {
        return getData().length;
    }

    public boolean isLoopback() {
        return loopback;
    }

    public boolean isEncryptedByShanda() {
        return encryptedByShanda;
    }

    @Override
    public String toString() {
        return String.format("%s, %s/0x%s\t| %s", PacketEnum.getHeaderByOP(op), op, Integer.toHexString(op).toUpperCase()
                , readableByteArray(Arrays.copyOfRange(getData(), 2, getData().length)));
    }

    public void encodeShort(int value) {
        encodeShort((short) value);
    }

    public void encodeString(String name, int length) {
        encodeString(name, (short) length);
    }


}

