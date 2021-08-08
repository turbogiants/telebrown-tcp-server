package net.browny.server.connection.network;

import net.browny.common.user.User;
import net.browny.common.crypto.AESCrypto;
import net.browny.common.packet.definition.Handshake;
import net.browny.server.connection.handler.ChannelHandler;
import net.browny.server.connection.packet.PacketDecoder;
import net.browny.server.connection.packet.PacketEncoder;
import net.browny.common.utility.Config;

import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.bootstrap.ServerBootstrap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static net.browny.common.user.NettyUser.CLIENT_KEY;

public class ServerInit implements Runnable {

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
                    socketChannel.pipeline().addLast(new PacketDecoder(), new PacketEncoder(), new ChannelHandler());

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
                        LOGGER.error(e.getLocalizedMessage());
                    }
                }

            });

            b.childOption(ChannelOption.TCP_NODELAY, true);
            b.childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(Config.getSocketIp(), Config.getSocketPort()).sync();
            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            LOGGER.error(e.getLocalizedMessage());
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }
}
