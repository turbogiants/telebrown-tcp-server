package net.browny.server;


//To be deleted once all test been done on this small puppy.

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class TestClient {

    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 6100;

        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new TestClientHandler())
                        .addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()))
                        .addLast(new StringDecoder())
                        .addLast(new StringEncoder());
                }
            });
            Channel ch = b.connect(host, port).sync().channel();
            ChannelFuture stdin_send = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while(true){
                String stdin = in.readLine();
                if(stdin == null)
                    break;

                stdin_send = ch.writeAndFlush(stdin + "\n");
            }

            if(stdin_send != null)
                stdin_send.sync();

        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
