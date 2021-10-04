package org.turbogiants.server.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.turbogiants.common.crypto.AESCrypto;
import org.turbogiants.common.packet.InPacket;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.turbogiants.common.packet.Packet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Netty User Class
 * @author https://github.com/Raitou
 * Desc: This class manage the exchange of AES Keys and its clients
 * per object created from this class is a client of this server
 * @version 1.2
 * @since 1.0
 */
public class NettyUser {

    /**
     * Date: --.--.--
     * Desc: Get the singleton instance of log4j
     * @since 1.0
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Date: --.--.--
     * Desc: Use of Attribute Key to make sure this class and AESCrypto will be objected for every nettyUser created
     * by the Server Initializer
     * @since 1.0
     */
    public static final AttributeKey<AESCrypto> CRYPTO_KEY = AttributeKey.valueOf("A");
    public static final AttributeKey<NettyUser> CLIENT_KEY = AttributeKey.valueOf("C");

    protected final Channel ch;
    private final ReentrantLock lock;
    private final InPacket inPacket;
    private byte[] serverIV;
    private byte[] clientIV;
    private int storedLength = -1;
    private UserDef userDef = null;

    /**
     * Date: --.--.--
     * Desc: This user pool designated to list every single client connected to this server so we can make
     * @since 1.1
     */
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


    /**
     * This function returns a NettyUser object from the userPool of nettyUser object by using the Client ID's to find
     * the object.
     * @param id: The Client's ID
     * @return NettyUser:
     */
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

    public static boolean closeByUserID(int id){
        return userPool.removeIf(nettyUser -> nettyUser.getUserDef().getUID() == id);
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
            if(userPool.remove(this))
                LOGGER.info(String.format("Method 1: Client(%s) closed successfully", getIP()));
            else if(NettyUser.closeByUserID(getUserDef().getUID()))
                LOGGER.info(String.format("Method 2: Client(%s) closed successfully", getIP()));
            else{
                LOGGER.warn(String.format("Method 3: Client(%s) will be forcefully closed", getIP()));
                setUserDef(null);
            }
            ch.close();
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
