package org.turbogiants.client.connection.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.turbogiants.client.connection.handler.ChannelHandler;
import org.turbogiants.client.connection.packet.PacketDecoder;
import org.turbogiants.client.connection.packet.PacketEncoder;
import org.turbogiants.common.utility.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;


public class ClientInit implements Runnable {
    private static final Logger LOGGER = LogManager.getRootLogger();
    public static SocketChannel socketChannel;
    public static final boolean isEPOLL = Epoll.isAvailable();
    @Override
    public void run() {
        EventLoopGroup workerGroup = isEPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) {
                    socketChannel.pipeline().addLast(new PacketDecoder(), new ChannelHandler(), new PacketEncoder());
                    ClientInit.socketChannel = socketChannel;
                }
            });
            ChannelFuture f = b.connect(Config.getSocketIp(), Config.getSocketPort()).sync();
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.error(Arrays.toString(e.getStackTrace()));
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
