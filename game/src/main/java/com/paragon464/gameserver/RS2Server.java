package com.paragon464.gameserver;

import com.paragon464.gameserver.net.ConnectionHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

/**
 * Starts everything else including MINA and the <code>GameEngine</code>.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 * @author Omar Saleh Assadi <omar@assadi.co.il>
 */
public class RS2Server {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RS2Server.class.getName());

    /**
     * The <code>NioAcceptor</code> instance.
     */
    private final NioSocketAcceptor acceptor = new NioSocketAcceptor();

    /**
     * Creates the server and the <code>GameEngine</code> and initializes the
     * <code>World</code>.
     */
    public RS2Server() {
        acceptor.setReuseAddress(true);
        acceptor.setHandler(new ConnectionHandler());
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 5);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.READER_IDLE, 5);
    }

    /**
     * Binds the server to the specified port.
     *
     * @param address The address on which to bind.
     * @param port    The port to bind to.
     * @return The server instance, for chaining.
     * @throws IOException
     */
    public RS2Server bind(final InetAddress address, final int port) throws IOException {
        LOGGER.info("Binding server to: {}:{}...", address.getHostAddress(), port);
        acceptor.bind(new InetSocketAddress(address, port));
        return this;
    }

    /**
     * Starts the <code>GameEngine</code>.
     *
     * @throws ExecutionException if an error occured during background loading.
     */
    public void start() throws ExecutionException {
        LOGGER.info("Ready");
    }
}
