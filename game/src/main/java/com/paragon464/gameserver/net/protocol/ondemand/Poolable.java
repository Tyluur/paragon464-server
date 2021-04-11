package com.paragon464.gameserver.net.protocol.ondemand;

/**
 * @author Lazaro <lazaro@ziotic.com>
 */
public interface Poolable {
    /**
     * If the object is still useful.
     *
     * @return If the object is still useful.
     */
    boolean expired();

    /**
     * Resets all the variables and data in the implemented object.
     */
    void recycle();
}
