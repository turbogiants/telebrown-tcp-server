package net.browny.client.connection.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.browny.client.connection.handler.HandshakeHandler;
import net.browny.client.connection.packet.HandshakeDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ClientInit implements Runnable{
    private static final Logger LOGGER = LogManager.getRootLogger();

    @Override
    public void run() {
        String host = "127.0.0.1";
        int port = 6100;
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.handler(new ChannelInitializer<SocketChannel>(){

                @Override
                protected void initChannel(SocketChannel socketChannel) {
                    socketChannel.pipeline().addLast(new HandshakeDecoder(), new HandshakeHandler());
                }

            });
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage());
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
