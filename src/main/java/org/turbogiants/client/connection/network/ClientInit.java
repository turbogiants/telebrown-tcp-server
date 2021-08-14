package org.turbogiants.client.connection.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.turbogiants.client.connection.handler.ChannelHandler;
import org.turbogiants.client.connection.packet.PacketDecoder;
import org.turbogiants.client.connection.packet.PacketEncoder;
import org.turbogiants.common.utility.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ClientInit implements Runnable{
    private static final Logger LOGGER = LogManager.getRootLogger();
    public static SocketChannel socketChannel;
    @Override
    public void run() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.handler(new ChannelInitializer<SocketChannel>(){
                @Override
                protected void initChannel(SocketChannel socketChannel) {
                    socketChannel.pipeline().addLast(new PacketDecoder(), new ChannelHandler(), new PacketEncoder());
                    ClientInit.socketChannel = socketChannel;
                }
            });
            ChannelFuture f = b.connect(Config.getSocketIp() , Config.getSocketPort()).sync();
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage());
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
