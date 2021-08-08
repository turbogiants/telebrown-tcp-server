package net.browny.server.client;

import net.browny.server.connection.crypto.AESCrypto;
import net.browny.server.connection.packet.InPacket;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import net.browny.server.connection.packet.Packet;

import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

public class NettyUser {

    public static final AttributeKey<AESCrypto> CRYPTO_KEY = AttributeKey.valueOf("A");

    public static final AttributeKey<NettyUser> CLIENT_KEY = AttributeKey.valueOf("C");

    private byte[] serverIV;

    private byte[] clientIV;

    private int storedLength = -1;

    protected final Channel ch;

    private final ReentrantLock lock;

    private final InPacket r;

    private NettyUser() {
        ch = null;
        lock = null;
        r = null;
    }

    public NettyUser(Channel c, byte[] serverIV, byte[] clientIV) {
        ch = c;
        this.serverIV = serverIV;
        this.clientIV = clientIV;
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

    public final byte[] getServerIV() {
        return serverIV;
    }

    public final byte[] getClientIV() {
        return clientIV;
    }

    public boolean checkClientIV(byte[] rIV){
        return Arrays.equals(this.clientIV, rIV);
    }

    public boolean checkServerIV(byte[] sIV){
        return Arrays.equals(this.serverIV, sIV);
    }

    public final void setServerIV(byte[] serverIV) {
        this.serverIV = serverIV;
    }

    public final void setClientIV(byte[] clientIV) {
        this.clientIV = clientIV;
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
