package net.browny.server.client;

import io.netty.channel.Channel;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Client extends NettyClient {

    private Lock lock;
    private Channel channelInstance;

    public Client(Channel channel, byte[] sIV, byte[] rIV) {
        super(channel, sIV, rIV);
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
