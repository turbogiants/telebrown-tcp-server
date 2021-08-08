package net.browny.server.connection.network;

import io.netty.channel.*;
import net.browny.server.client.Client;
import net.browny.server.connection.crypto.AESCrypto;
import net.browny.server.connection.packet.Packet;
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

import static net.browny.server.client.NettyClient.CLIENT_KEY;

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
                    socketChannel.pipeline().addLast(new PacketDecoder(), new PacketEncoder());

                    try {
                        byte[] serverIV = AESCrypto.generateIV();
                        byte[] clientIV = AESCrypto.generateIV();

                        Client client = new Client(socketChannel, serverIV, clientIV);
                        LOGGER.info(String.format("serverIV for Client %s =", client.getIP()) + Arrays.toString(serverIV));
                        LOGGER.info(String.format("clientIV for Client %s =", client.getIP()) + Arrays.toString(clientIV));

                        client.write(Handshake.initializeCommunication(serverIV, clientIV));

                        channelPool.put(client.getIP(), socketChannel);
                        socketChannel.attr(CLIENT_KEY).set(client);
                        socketChannel.attr(Client.CRYPTO_KEY).set(new AESCrypto());

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
