package net.browny.client;

import net.browny.client.connection.network.ClientInit;
import net.browny.common.crypto.AESCrypto;
import net.browny.common.utility.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;


public class Client {
    public final Logger logger = LogManager.getRootLogger();
    private static final Client client = new Client();
    private static final AESCrypto aesCrypto = new AESCrypto();

    public static Client getClientInstance() {
        return client;
    }
    public static AESCrypto getAESCryptoInstance() {
        return aesCrypto;
    }

    private void init(String[] args){
        logger.info("Browny Test Client [Start]");
        long startNow = System.currentTimeMillis();
        Config.init(false);
        logger.info("Configuration loaded in " + (System.currentTimeMillis() - startNow) + "ms");
        new Thread(new ClientInit()).start();
        logger.info("Client attempting connection to " + Config.getSocketIp() + ":" + Config.getSocketPort() + " in " + (System.currentTimeMillis() - startNow) + "ms");

    }

    public static void main(String[] args) {
        try{
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            File file = new File("log4j2_client.xml");
            context.setConfigLocation(file.toURI());
            Client.getClientInstance().init(args);
        }catch(Exception e){
            Client.getClientInstance().logger.error(e.getLocalizedMessage());
        }

    }
}