package net.browny.server.client;

import net.browny.server.connection.crypto.AESCrypto;
import net.browny.server.connection.packet.InPacket;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import net.browny.server.connection.packet.Packet;

import java.util.concurrent.locks.ReentrantLock;

public class NettyClient {

    public static final AttributeKey<AESCrypto> CRYPTO_KEY = AttributeKey.valueOf("A");

    public static final AttributeKey<NettyClient> CLIENT_KEY = AttributeKey.valueOf("C");

    private byte[] sIV;

    private byte[] rIV;

    private int storedLength = -1;

    protected final Channel ch;

    private final ReentrantLock lock;

    private final InPacket r;

    private NettyClient() {
        ch = null;
        lock = null;
        r = null;
    }

    public NettyClient(Channel c, byte[] sIV, byte[] rIV) {
        ch = c;
        this.sIV = sIV;
        this.rIV = rIV;
        r = new InPacket();
        lock = new ReentrantLock(true); // note: lock is fair to ensure logical sequence is maintained server-side
    }

    public final InPacket getReader() {
        return r;
    }

    public final int getStoredLength() {
        return storedLength;
    }

    public final void setStoredLength(int val) {
        storedLength = val;
    }

    public final byte[] getSendIV() {
        return sIV;
    }

    public final byte[] getRecvIV() {
        return rIV;
    }

    public final void setSendIV(byte[] sIV) {
        this.sIV = sIV;
    }

    public final void setRecvIV(byte[] rIV) {
        this.rIV = rIV;
    }


    public void write(Packet msg) {
        assert ch != null;
        ch.writeAndFlush(msg);
    }

    public void close() {
        assert ch != null;
        ch.close();
    }

    public String getIP() {
        assert ch != null;
        return ch.remoteAddress().toString().split(":")[0].substring(1);
    }

    public final void acquireEncoderState() {
        assert lock != null;
        lock.lock();
    }

    public final void releaseEncodeState() {
        assert lock != null;
        lock.unlock();
    }
}
