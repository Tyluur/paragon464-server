package com.paragon464.gameserver.net;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.net.protocol.RS2CodecFactory;
import com.paragon464.gameserver.tickable.Tickable;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;

import java.net.InetSocketAddress;

/**
 * The <code>ConnectionHandler</code> processes incoming events from MINA,
 * submitting appropriate tasks to the <code>GameEngine</code>.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public class ConnectionHandler extends IoHandlerAdapter {

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        session.setAttribute("remote", session.getRemoteAddress());
        session.getFilterChain().addFirst("protocol", new ProtocolCodecFilter(RS2CodecFactory.LOGIN));
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        if (session.containsAttribute("player")) {
            final Player player = (Player) session.getAttribute("player");
            if (!player.getDetails().dummyPlayer.attemptLogout) {
                player.getDetails().dummyPlayer.attemptLogout = true;
                boolean instant = (player.getCombatState().outOfCombat()
                    || player.getAttributes().isSet("force_logout")) || (player.getAttributes().isSet("stopActions") && player.getVariables().getTutorial() != null);
                if (instant) {
                    World.unregister(player);
                } else {
                    World.getWorld().submit(new Tickable(100) {
                        @Override
                        public void execute() {
                            this.stop();
                            World.unregister(player);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        //session.close(false);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable throwable) throws Exception {
        //session.close(false);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        if (session.containsAttribute("player")) {
            Player p = (Player) session.getAttribute("player");
            Packet packet = (Packet) message;
            PacketManager.getPacketManager().handle(p, packet);
        }
    }
}
