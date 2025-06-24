package dev.akarah.qh;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Util {
    public static int PORT = 25565;
    public static int PROTOCOL_VERSION = 2;
    public static Logger LOGGER = LogManager.getLogger("qh-server");

    public interface Throwing<T, E extends Exception> {
        T get() throws E;
    }

    public static <T, E extends Exception> T sneakyThrows(Throwing<T, E> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
