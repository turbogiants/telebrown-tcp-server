package org.turbogiants.server;

import org.turbogiants.server.connection.network.ServerInit;
import org.turbogiants.common.utility.Config;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

/*** Driver Class
 * @author https://github.com/Raitou
 * @version 1.0
 */
public class Server {

    /**
     * Singleton Server Instance
     */
    private static final Server server = new Server();

    /**
     * Get the Singleton Instance of log4j
     */
    public final Logger logger = LogManager.getRootLogger();

    /**
     * Returns the singleton server instance
     * @return Server Instance
     */
    public static Server getInstance() {
        return server;
    }

    /**
     * Main Driver
     */
    public static void main(String[] args) {
        try {
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            File file = new File("log4j2_server.xml");
            context.setConfigLocation(file.toURI());
            Server.getInstance().init(args);
        } catch (Exception e) {
            Server.getInstance().logger.error(e.getStackTrace());
        }

    }

    /**
     * Server Initializer
     */
    private void init(String[] args) {
        logger.info("Browny Server [Start]");
        long startNow = System.currentTimeMillis();
        Config.init(true);
        logger.info("Configuration loaded in " + (System.currentTimeMillis() - startNow) + "ms");
        new Thread(new ServerInit()).start();
        logger.info("Binded to " + Config.getSocketIp() + ":" + Config.getSocketPort() + " in " + (System.currentTimeMillis() - startNow) + "ms");
        logger.info(String.format("Finished loading server in %dms", System.currentTimeMillis() - startNow));

    }
}
