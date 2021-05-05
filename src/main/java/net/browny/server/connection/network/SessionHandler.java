package net.browny.server.connection.network;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class SessionHandler  {

    private static final Map<Integer, Method> handlers = new HashMap<>();

    public static void init(){
        long start = System.currentTimeMillis();

    }

}
