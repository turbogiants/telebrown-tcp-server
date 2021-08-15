package org.turbogiants.client.connection.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.turbogiants.common.crypto.AESCrypto;
import org.turbogiants.common.packet.OutPacket;
import org.turbogiants.common.packet.Packet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.turbogiants.client.Client;

import java.security.GeneralSecurityException;
import java.util.concurrent.locks.ReentrantLock;

public final class PacketEncoder extends MessageToByteEncoder<OutPacket> {
    private static final Logger LOGGER = LogManager.getRootLogger();

    private final ReentrantLock lock;

    public PacketEncoder() {
        lock = new ReentrantLock(true); //so if multiple method wants to write into the encoder
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, OutPacket outPacket, ByteBuf byteBuf) {
        AESCrypto aesCrypto = Client.getAESCryptoInstance();
        byte[] iv = aesCrypto.getClientIV();
        byte[] data = outPacket.getData();
        try {
            lock.lock();
            data = aesCrypto.encrypt(data, iv);

            byteBuf.writeBytes(iv);
            byteBuf.writeInt(data.length);
            byteBuf.writeBytes(data);
        } catch (GeneralSecurityException e) {
            LOGGER.error(e.getLocalizedMessage());
        } finally {
            lock.unlock();
        }
    }
}
