package org.turbogiants.server.user;

import org.turbogiants.common.crypto.AESCrypto;
import org.turbogiants.common.packet.InPacket;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.turbogiants.common.packet.Packet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

public class NettyUser {

    public static final AttributeKey<AESCrypto> CRYPTO_KEY = AttributeKey.valueOf("A");
    public static final AttributeKey<NettyUser> CLIENT_KEY = AttributeKey.valueOf("C");

    protected final Channel ch;
    private final ReentrantLock lock;
    private final InPacket inPacket;
    private byte[] serverIV;
    private byte[] clientIV;
    private int storedLength = -1;
    private UserDef userDef = null;

    // this is not the best sln but it does what i want
    public static ArrayList<NettyUser> userPool = new ArrayList<>();

    public static boolean isUserIDExists(int id){
        boolean isExists = false;
        for (NettyUser nettyUser : userPool) {
            if (nettyUser.getUserDef() == null)
                continue;
            if (nettyUser.getUserDef().getUID() == 0)
                continue;
            if (nettyUser.getUserDef().getUID() == id) {
                isExists = true;
                break;
            }
        }
        return isExists;
    }



    // inferior (need to make this multi thread esp if theres a lot of users)
    // we can live with this for now as this is only a prototype
    // then we could just make a limit of how many users per servers (distributed)
    // after the paper is done
    public static NettyUser getUserByID(int id){
        for (NettyUser nettyUser : userPool) {
            if (nettyUser.getUserDef() == null)
                continue;
            if (nettyUser.getUserDef().getUID() == 0)
                continue;
            if (nettyUser.getUserDef().getUID() == id) {
                return nettyUser;
            }
        }
        return null;
    }


    public void setUserDef(UserDef userDef){
        this.userDef = userDef;
    }

    public UserDef getUserDef(){
        return userDef;
    }

    private NettyUser() {
        ch = null;
        lock = null;
        inPacket = null;
    }

    public NettyUser(Channel c, byte[] serverIV, byte[] clientIV) {
        ch = c;
        this.serverIV = serverIV;
        this.clientIV = clientIV;
        this.inPacket = new InPacket();
        lock = new ReentrantLock(true); // note: lock is fair to ensure logical sequence is maintained server-side
        userPool.add(this);
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

    public final void setServerIV(byte[] serverIV) {
        this.serverIV = serverIV;
    }

    public final byte[] getClientIV() {
        return clientIV;
    }

    public final void setClientIV(byte[] clientIV) {
        this.clientIV = clientIV;
    }

    public boolean checkClientIV(byte[] rIV) {
        return !Arrays.equals(this.clientIV, rIV);
    }

    public boolean checkServerIV(byte[] sIV) {
        return Arrays.equals(this.serverIV, sIV);
    }

    public void write(Packet msg) {
        if(ch != null)
            ch.writeAndFlush(msg);
    }

    public void close() {
        if(ch != null){
            ch.close();
            userPool.remove(this);
        }

    }

    public String getIP() {
        if(ch != null)
            return ch.remoteAddress().toString().split(":")[0].substring(1);
        return null;
    }

    public final void acquireEncoderState() {
        if(lock != null)
            lock.lock();
    }

    public final void releaseEncodeState() {
        if(lock != null)
            lock.unlock();
    }
}
