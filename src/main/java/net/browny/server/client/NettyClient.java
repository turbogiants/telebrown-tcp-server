package net.browny.server.client;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import net.browny.server.connection.crypto.AESCrypto;
import net.browny.server.connection.packet.Packet;

import java.util.concurrent.locks.ReentrantLock;

public class NettyClient {

    public static final AttributeKey<AESCrypto> CRYPTO_KEY = AttributeKey.valueOf("S");

    public static final AttributeKey<NettyClient> CLIENT_KEY = AttributeKey.valueOf("C");

    private int storedLength = -1;

    protected final Channel channel;

    private final ReentrantLock lock;


    private NettyClient() {
        this.channel = null;
        this.lock = null;
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

    public void close() {
        assert channel != null;
        channel.close();
    }

    public String getIP() {
        assert channel != null;
        return channel.remoteAddress().toString().split(":")[0].substring(1);
    }

    public final void acquireEncoderState() {
        assert lock != null;
        lock.lock();
    }

    public final void releaseEncodeState() {
        assert lock != null;
        lock.unlock();
    }

    public void write(Packet msg) {
        assert channel != null;
        channel.writeAndFlush(msg);
    }
}
