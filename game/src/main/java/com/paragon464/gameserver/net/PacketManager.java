package com.paragon464.gameserver.net;

import com.moandjiezana.toml.Toml;
import com.paragon464.gameserver.Config;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.net.packet.DefaultPacketHandler;
import com.paragon464.gameserver.net.packet.PacketHandler;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Managers <code>PacketHandler</code>s.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public class PacketManager {

    /**
     * The logger class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PacketManager.class);

    /**
     * The instance.
     */
    private static final PacketManager INSTANCE = new PacketManager();
    /**
     * The packet handler array.
     */
    private PacketHandler[] packetHandlers = new PacketHandler[256];

    /**
     * Creates the packet manager.
     */
    public PacketManager() {
        /*
         * Set default handlers.
         */
        final PacketHandler defaultHandler = new DefaultPacketHandler();
        for (int i = 0; i < packetHandlers.length; i++) {
            if (packetHandlers[i] == null) {
                packetHandlers[i] = defaultHandler;
            }
        }
    }

    /**
     * Gets the packet manager instance.
     *
     * @return The packet manager instance.
     */
    public static PacketManager getPacketManager() {
        return INSTANCE;
    }

    public void bind() {
        LOGGER.debug("Binding packet handlers.");

        final List<Toml> packetTables = Config.getConfig().getTables("network.packet_handler");
        for (Toml packetTable : packetTables) {
            final List<Integer> opcodes = packetTable.getList("opcodes").stream().map(opcode -> Math.toIntExact((Long) opcode)).collect(Collectors.toList());
            final String handlerName = packetTable.getString("packet_handler");

            try {
                final Class<?> handlerClass = Class.forName(handlerName);
                final Object handlerInstance = handlerClass.getConstructor().newInstance();

                if (!(handlerInstance instanceof PacketHandler)) {
                    LOGGER.error("Failed to bind packet handler: {}; not an extension of PacketHandler.", handlerName);
                    continue;
                }

                opcodes.forEach(opcode -> {
                    bind(opcode, (PacketHandler) handlerInstance);
                    LOGGER.trace("Bound opcode {} to {}.", opcode, handlerName);
                });
            } catch (ClassNotFoundException e) {
                LOGGER.error("Failed to bind packet handler: {}; unable to find class.", handlerName, e);
            } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                LOGGER.error("Failed to bind packet handler: {}; unable to instantiate class.", handlerName, e);
            }
        }
    }

    /**
     * Binds an opcode to a handler.
     *
     * @param id      The opcode.
     * @param handler The handler.
     */
    public void bind(int id, PacketHandler handler) {
        packetHandlers[id] = handler;
    }

    /**
     * Handles a packet.
     *
     * @param player The player.
     * @param packet The packet.
     */
    public void handle(Player player, Packet packet) {
        if (packet == null)
            return;
        IoSession session = player.getSession();
        if (session == null)
            return;
        try {
            PacketHandler handler = packetHandlers[packet.getOpcode()];
            if (handler != null) {
                handler.handle(player, packet);
            }
        } catch (Exception ex) {
            LOGGER.error("Exception handling packet.", ex);
            // session.close(false);
        }
    }
}
