package com.paragon464.gameserver.net.protocol.ondemand;

import com.paragon464.gameserver.net.Packet;
import org.apache.mina.core.session.IoSession;

/**
 * <p>
 * Represents a single 'ondemand' request. Ondemand requests are created when
 * the client requests a file from the cache using the update protocol.<?p>
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public class OnDemandRequest {

    /**
     * The session.
     */
    private IoSession session;

    /**
     *
     */
    private byte priority;

    /**
     * The cache.
     */
    private Packet p;

    /**
     * Creates the request.
     *
     * @param session  The session.
     * @param cacheId  The cache.
     * @param fileId   The file.
     * @param priority The priority.
     */
    public OnDemandRequest(IoSession session, int priority, Packet p) {
        this.session = session;
        this.p = p;
        this.priority = (byte) priority;
    }

    /**
     * Services the request.
     */
    public void service() {
        if (p != null)
            session.write(p);
    }

    public int getPriority() {
        return priority;
    }
}
