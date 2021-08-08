package net.browny.server.connection.handler;

import io.netty.channel.*;
import net.browny.server.client.User;
import net.browny.server.connection.crypto.AESCrypto;
import net.browny.server.connection.packet.PacketDecoder;
import net.browny.server.connection.packet.PacketEncoder;
import net.browny.server.connection.packet.definition.Handshake;
import net.browny.server.utility.Config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static net.browny.server.client.NettyUser.CLIENT_KEY;

public class HandshakeHandler implements Runnable {

    private static final Logger LOGGER = LogManager.getRootLogger();
    public static Map<String, Channel> channelPool = new HashMap<>();

    @Override
    public void run() {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new ChannelInitializer<SocketChannel>(){

                @Override
                protected void initChannel(SocketChannel socketChannel) {
                    socketChannel.pipeline().addLast(new PacketDecoder(), new PacketEncoder(), new SessionHandler());

                    try {
                        byte[] serverIV = AESCrypto.generateIV();
                        byte[] clientIV = AESCrypto.generateIV();

                        User user = new User(socketChannel, serverIV, clientIV);
                        LOGGER.info(String.format("serverIV for Client %s =", user.getIP()) + Arrays.toString(serverIV));
                        LOGGER.info(String.format("clientIV for Client %s =", user.getIP()) + Arrays.toString(clientIV));

                        user.write(Handshake.initializeCommunication(serverIV, clientIV));

                        channelPool.put(user.getIP(), socketChannel);
                        socketChannel.attr(CLIENT_KEY).set(user);
                        socketChannel.attr(User.CRYPTO_KEY).set(new AESCrypto());

                    } catch (GeneralSecurityException e) {
                        LOGGER.error(e.getStackTrace());
                    }
                }

            });

            b.childOption(ChannelOption.TCP_NODELAY, true);
            b.childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(Config.getSocketIp(), Config.getSocketPort()).sync();
            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            LOGGER.error(e.getStackTrace());
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }
}
