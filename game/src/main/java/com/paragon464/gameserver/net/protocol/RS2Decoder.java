package com.paragon464.gameserver.net.protocol;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.net.Packet;
import com.paragon464.gameserver.net.packet.Packets;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Game protocol decoding class.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public class RS2Decoder extends CumulativeProtocolDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(RS2Decoder.class);

    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        /*
         * Fetch the ISAAC cipher for this session.
         */
        @SuppressWarnings("unused")
        ISAACCipher inCipher = ((Player) session.getAttribute("player")).getInCipher();
        Player player = (Player) session.getAttribute("player");
        /*
         * Fetch any cached opcodes and sizes, reset to -1 if not present.
         */
        int opcode = (Integer) session.getAttribute("opcode", -1);
        int size = (Integer) session.getAttribute("size", -1);

        /*
         * If the opcode is not present.
         */
        if (opcode == -1) {
            /*
             * Check if it can be read.
             */
            if (in.remaining() >= 1) {
                /*
                 * Read and decrypt the opcode.
                 */
                opcode = in.get() & 0xFF;
                // opcode = (opcode - inCipher.getNextValue()) & 0xFF;

                /*
                 * Find the packet size.
                 */
                size = Packets.SIZES[opcode];

                /*
                 * Set the cached opcode and size.
                 */
                session.setAttribute("opcode", opcode);
                session.setAttribute("size", size);
            } else {
                /*
                 * We need to wait for more data.
                 */
                return false;
            }
        }

        /*
         * If the packet is variable-length.
         */
        if (size == -1) {
            /*
             * Check if the size can be read.
             */
            if (in.remaining() >= 1) {
                /*
                 * Read the packet size and cache it.
                 */
                size = in.get() & 0xFF;
                session.setAttribute("size", size);
            } else {
                /*
                 * We need to wait for more data.
                 */
                in.rewind();
                return false;
            }
        }

        /*
         * If the packet is variable-length.
         */
        if (size == -3) {//TODO - packet 0 so far
            player.getFrames().forceLogout();
            LOGGER.info("[UNUSED] Packet{" + opcode + "} was sent with no length.");
            return false;
            /*
             * Check if the size can be read.

            if (in.remaining() >= 1) {
                 * Read the packet size and cache it.

                size = in.remaining();
                session.setAttribute("size", size);
            } else {

                 * We need to wait for more data.

                return false;
            }*/
        }

        /*
         * If the packet payload (data) can be read.
         */
        if (in.remaining() >= size) {
            /*
             * Read it.
             */
            byte[] data = new byte[size];
            in.get(data);
            IoBuffer payload = IoBuffer.allocate(data.length);
            payload.put(data);
            payload.flip();

            /*
             * Produce and write the packet object.
             */
            out.write(new Packet(opcode, Packet.Type.FIXED, payload));

            /*
             * Reset the cached opcode and sizes.
             */
            session.setAttribute("opcode", -1);
            session.setAttribute("size", -1);

            /*
             * Indicate we are ready to read another packet.
             */
            return true;
        }

        /*
         * We need to wait for more data.
         */
        return false;
    }
}
