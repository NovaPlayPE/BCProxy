package net.novaplay.bcproxy.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ThreadStore {
    public static Map<String, Object> store = new ConcurrentHashMap<>();
}
