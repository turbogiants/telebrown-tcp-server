package org.turbogiants.client.connection.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.turbogiants.common.crypto.AESCrypto;
import org.turbogiants.common.handler.EventHandler;
import org.turbogiants.common.packet.OutPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.turbogiants.client.Client;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

public final class PacketEncoder extends MessageToByteEncoder<OutPacket> {
    private static final Logger LOGGER = LogManager.getRootLogger();

    private final ReentrantLock lock;

    public PacketEncoder() {
        lock = new ReentrantLock(true); //so if multiple method wants to write into the encoder
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, OutPacket outPacket, ByteBuf byteBuf) {
        lock.lock();
        AESCrypto aesCrypto = Client.getAESCryptoInstance();
        byte[] iv = aesCrypto.getClientIV();
        byte[] data = outPacket.getData();
        try {
            //LOGGER.info("Encode: " + Arrays.toString(data));
            data = aesCrypto.encrypt(data, iv);

            //only for test client
            if (outPacket.getHeader() == 11){
                byte[] spamIV = new byte[16];
                byteBuf.writeBytes(spamIV);
            } else {
                byteBuf.writeBytes(iv);
            }
            byteBuf.writeInt(data.length);
            byteBuf.writeBytes(data);
        } catch (GeneralSecurityException e) {
            LOGGER.error(Arrays.toString(e.getStackTrace()));
        } finally {
            //not the best sln find another.
            long startNow = System.currentTimeMillis();
            while(Arrays.equals(iv, aesCrypto.getClientIV()))
            {
                // basically delaying the unlock method within 1000 ms if the condition in the while statement doesn't
                // return false within the 1000ms mark then it will proceed on unlocking it. Basically not the best solution.
                // Queue System is still the better sln for this, but I don't know how I will implement it.
                if (System.currentTimeMillis() - startNow > 1000){
                    break;
                }

            }
            lock.unlock();
        }
    }
}
