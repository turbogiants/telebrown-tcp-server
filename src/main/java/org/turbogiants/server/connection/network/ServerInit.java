package org.turbogiants.server.connection.network;

import io.netty.handler.timeout.IdleStateHandler;
import org.turbogiants.server.user.User;
import org.turbogiants.common.crypto.AESCrypto;
import org.turbogiants.server.connection.definition.Handshake;
import org.turbogiants.server.connection.handler.ChannelHandler;
import org.turbogiants.server.connection.packet.PacketDecoder;
import org.turbogiants.server.connection.packet.PacketEncoder;
import org.turbogiants.common.utility.Config;

import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.bootstrap.ServerBootstrap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.turbogiants.server.user.NettyUser;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

public class ServerInit implements Runnable {

    private static final Logger LOGGER = LogManager.getRootLogger();
    public static Map<String, Channel> channelPool = new HashMap<>();


    @Override
    public void run() {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel socketChannel) {
                    socketChannel.pipeline().addLast(new IdleStateHandler(15, 15, 15), new PacketDecoder(), new PacketEncoder(), new ChannelHandler());

                    try {
                        byte[] serverIV = AESCrypto.generateIV();
                        byte[] clientIV = AESCrypto.generateIV();

                        User user = new User(socketChannel, serverIV, clientIV);

                        channelPool.put(user.getIP(), socketChannel);
                        socketChannel.attr(NettyUser.CLIENT_KEY).set(user);
                        socketChannel.attr(User.CRYPTO_KEY).set(new AESCrypto());

                        //starts a handshake
                        user.write(Handshake.Handler_TCS_HANDSHAKE_NOT());

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
