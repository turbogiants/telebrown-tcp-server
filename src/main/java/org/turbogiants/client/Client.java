package org.turbogiants.client;

import org.turbogiants.client.cli.CommandListener;
import org.turbogiants.client.connection.network.ClientInit;
import org.turbogiants.common.crypto.AESCrypto;
import org.turbogiants.common.utility.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;


public class Client {
    private static final Client client = new Client();
    private static final AESCrypto aesCrypto = new AESCrypto();
    public final Logger logger = LogManager.getRootLogger();

    public static Client getClientInstance() {
        return client;
    }

    public static AESCrypto getAESCryptoInstance() {
        return aesCrypto;
    }

    public static void main(String[] args) {
        try {
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            File file = new File("log4j2_client.xml");
            context.setConfigLocation(file.toURI());
            Client.getClientInstance().init(args);
        } catch (Exception e) {
            Client.getClientInstance().logger.error(e.getLocalizedMessage());
        }

    }

    private void init(String[] args) {
        logger.info("Browny Test Client [Start]");
        long startNow = System.currentTimeMillis();
        Config.init(false);
        logger.info("Configuration loaded in " + (System.currentTimeMillis() - startNow) + "ms");
        Thread clientInit = new Thread(new ClientInit());
        clientInit.start();
        logger.info("Client attempting connection to " + Config.getSocketIp() + ":" + Config.getSocketPort() + " in " + (System.currentTimeMillis() - startNow) + "ms");
        logger.info(String.format("Finished loading test client in %dms", System.currentTimeMillis() - startNow));
        new Thread(new CommandListener()).start();

        try {
            clientInit.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.exit(0);
    }
}
