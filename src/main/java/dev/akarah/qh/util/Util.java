package dev.akarah.qh.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Util {
    public static int PORT = 21214;
    public static int PROTOCOL_VERSION = 4;
    public static Logger LOGGER = LogManager.getLogger("qh-server");

    public static <T, E extends Exception> T sneakyThrows(Throwing<T, E> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public interface Throwing<T, E extends Exception> {
        T get() throws E;
    }
}
