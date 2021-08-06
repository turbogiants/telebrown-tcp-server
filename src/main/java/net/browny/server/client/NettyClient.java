package net.browny.server.client;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import net.browny.server.connection.crypto.AESCrypto;

import java.util.concurrent.locks.ReentrantLock;

public class NettyClient {

    public static final AttributeKey<AESCrypto> CRYPTO_KEY = AttributeKey.valueOf("S");

    public static final AttributeKey<AESCrypto> CLIENT_KEY = AttributeKey.valueOf("C");

    private int storedLength = -1;

    protected final Channel channel;

    private final ReentrantLock lock;

    private byte[] sIV;
    private byte[] rIV;

    private NettyClient() {
        this.channel = null;
        this.lock = null;
        this.sIV = null;
        this.rIV = null;
    }

    public NettyClient(Channel channel) {
        this.channel = channel;
        this.lock = new ReentrantLock(true); // note: lock is fair to ensure logical sequence is maintained server-side
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

    public final void setSendIV(byte[] IV) {
        this.sIV = IV;
    }

    public final void setRecvIV(byte[] IV) {
        this.rIV = IV;
    }


    public void close() {
        channel.close();
    }

    public String getIP() {
        return channel.remoteAddress().toString().split(":")[0].substring(1);
    }

    public final void acquireEncoderState() {
        lock.lock();
    }

    public final void releaseEncodeState() {
        lock.unlock();
    }
}
