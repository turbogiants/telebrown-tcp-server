package net.browny.server;


//To be deleted once all test been done on this small puppy.

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import net.browny.server.client.Client;
import net.browny.server.connection.crypto.AESCrypto;
import net.browny.server.connection.packet.PacketDecoder;
import net.browny.server.connection.packet.PacketEncoder;
import net.browny.server.connection.packet.definition.Handshake;
import net.browny.server.utility.Config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import static net.browny.server.client.NettyClient.CLIENT_KEY;


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
                    socketChannel.pipeline().addLast(new PacketDecoder(), new PacketEncoder());
                }

            });
            Channel ch = b.connect(host, port).sync().channel();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
//        try {
//            Bootstrap b = new Bootstrap();
//            b.group(workerGroup)
//                .channel(NioSocketChannel.class)
//                .option(ChannelOption.SO_KEEPALIVE, true)
//                .handler(new ChannelInitializer<SocketChannel>() {
//                @Override
//                public void initChannel(SocketChannel ch) throws Exception {
//                    ch.pipeline().addLast(new TestClientHandler())
//                        .addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()))
//                        .addLast(new StringDecoder())
//                        .addLast(new StringEncoder());
//                }
//            });
//            Channel ch = b.connect(host, port).sync().channel();
//            ChannelFuture stdin_send = null;
//            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//            while(true){
//                String stdin = in.readLine();
//                if(stdin == null)
//                    break;
//
//                stdin_send = ch.writeAndFlush(stdin + "\n");
//            }
//
//            if(stdin_send != null)
//                stdin_send.sync();
//
//        } finally {
//            workerGroup.shutdownGracefully();
//        }
    }
}
