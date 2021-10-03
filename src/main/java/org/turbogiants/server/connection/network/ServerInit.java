package org.turbogiants.server.connection.network;

import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.handler.timeout.IdleStateHandler;
import org.turbogiants.common.crypto.AESCrypto;
import org.turbogiants.server.connection.packet.PacketHandler;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Server Initializer Class
 * @author https://github.com/Raitou
 * Desc: This class handle's server initialization which binds to a network socket
 * @version 1.2
 * @since 1.0
 */
public class ServerInit implements Runnable {

    /**
     * Date: --.--.--
     * Desc: Get the singleton instance of log4j
     * @since 1.0
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Date: --.--.--
     * Desc: Check whether EPOLL is available especially in Linux Systems
     * @since 1.0
     */
    public static final boolean isEPOLL = Epoll.isAvailable();

    /**
     * Standard Initialization of NettyIO TCP Socket
     * see more at NettyIO documentation
     */
    @Override
    public void run() {

        EventLoopGroup bossGroup = isEPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        EventLoopGroup workerGroup = isEPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new ChannelInitializer<SocketChannel>() {

                /**
                 * Packet Encoder - This encodes the packet before sending to the socket.
                 * Packet Decoder - This decodes the packet before receiving to the channel handler.
                 * Channel Handler - This process information for both receiving and sending.
                 * @since 1.1
                 */
                 @Override
                protected void initChannel(SocketChannel socketChannel) {

                    socketChannel.pipeline().addLast(new IdleStateHandler(30, 30, 30), new PacketDecoder(), new PacketEncoder(), new ChannelHandler());

                    try {
                        byte[] serverIV = AESCrypto.generateIV();
                        byte[] clientIV = AESCrypto.generateIV();

                        NettyUser user = new NettyUser(socketChannel, serverIV, clientIV);

                        socketChannel.attr(NettyUser.CLIENT_KEY).set(user);
                        socketChannel.attr(NettyUser.CRYPTO_KEY).set(new AESCrypto());

                        user.write(PacketHandler.Handler_TCS_HANDSHAKE_NOT());

                    } catch (GeneralSecurityException e) {
                        LOGGER.error(Arrays.toString(e.getStackTrace()));
                    }
                }

            });

            b.childOption(ChannelOption.TCP_NODELAY, true);
            b.childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(Config.getSocketIp(), Config.getSocketPort()).sync();
            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            LOGGER.error(Arrays.toString(e.getStackTrace()));
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }
}
