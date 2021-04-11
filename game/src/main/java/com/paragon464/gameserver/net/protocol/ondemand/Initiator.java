package com.paragon464.gameserver.net.protocol.ondemand;

/**
 * @author Lazaro <lazaro@ziotic.com>
 */
public interface Initiator<T extends Poolable> {
    void init(T object) throws Exception;
}
