package net.browny.server.client;

import io.netty.channel.Channel;

public class Client extends NettyClient {

    public Client(Channel channel) {
        super(channel);
    }
}
