package net.browny.client;


//To be deleted once all test been done on this small puppy.

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.browny.client.connection.handler.HandshakeHandler;
import net.browny.client.connection.packet.HandshakeDecoder;
import net.browny.server.connection.packet.PacketEncoder;


public class TestClient {

    public static void main(String[] args) {
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
                    socketChannel.pipeline().addLast(new HandshakeDecoder(), new PacketEncoder(), new HandshakeHandler());
                }

            });
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
