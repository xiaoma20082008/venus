package org.venus.startup;

import org.venus.config.ServerConfig;
import org.venus.netty.NettyServer;

public class Main {

    public static void main(String[] args) throws Exception {
        NettyServer server = new NettyServer(new ServerConfig());
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                server.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }, "shutdown"));
    }

}
