package net.browny.server.client;

import io.netty.channel.Channel;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class User extends NettyUser {

    private Lock lock;
    private Channel channelInstance;

    public User(Channel channel, byte[] serverIV, byte[] clientIV) {
        super(channel, serverIV, clientIV);
        lock = new ReentrantLock(true);
    }

    public Lock getLock() {
        return lock;
    }

    public void setChannelInstance(Channel channelInstance) {
        this.channelInstance = channelInstance;
    }

    public Channel getChannelInstance() {
        return channelInstance;
    }


}
