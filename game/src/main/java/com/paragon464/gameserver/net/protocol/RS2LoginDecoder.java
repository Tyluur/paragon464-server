package com.paragon464.gameserver.net.protocol;

import com.paragon464.gameserver.Config;
import com.paragon464.gameserver.cache.Cache;
import com.paragon464.gameserver.cache.UpdateServer;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.player.PlayerDetails;
import com.paragon464.gameserver.net.PacketBuilder;
import com.paragon464.gameserver.net.protocol.ondemand.OnDemandPool;
import com.paragon464.gameserver.net.protocol.ondemand.OnDemandRequest;
import com.paragon464.gameserver.util.IoBufferUtils;
import com.paragon464.gameserver.util.TextUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.SecureRandom;

import static java.lang.String.format;

/**
 * Login protocol decoding class.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public class RS2LoginDecoder extends CumulativeProtocolDecoder {

    /**
     * Opcode stage.
     */
    public static final int STATE_OPCODE = 0;
    /**
     * Login stage.
     */
    public static final int STATE_LOGIN = 1;
    /**
     * Precrypted stage.
     */
    public static final int STATE_PRECRYPTED = 2;
    /**
     * Crypted stage.
     */
    public static final int STATE_CRYPTED = 3;
    /**
     * Update stage.
     */
    public static final int STATE_UPDATE = -1;
    /**
     * World stage.
     */
    public static final int STATE_WORLD = -2;
    /**
     * Game opcode.
     */
    public static final int OPCODE_GAME = 14;
    /**
     * Update opcode.
     */
    public static final int OPCODE_UPDATE = 15;
    /**
     * Creation opcode.
     */
    public static final int OPCODE_BIRTH = 20;
    /**
     * Creation opcode.
     */
    public static final int OPCODE_USERNAME = 21;
    /**
     * Creation opcode.
     */
    public static final int OPCODE_CONFIRM = 22;
    /**
     * World opcode.
     */
    public static final int OPCODE_WORLD = 255;
    /**
     * Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RS2LoginDecoder.class);
    /**
     * Secure random number generator.
     */
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final BigInteger RSA_MODULUS = new BigInteger(
        "8784096651353199997452439564504785205116818155382223182025762557587092401706326230662806996238727283959643159575581225953287971213717511802074868210887121");
    private static final BigInteger RSA_EXPONENT = new BigInteger(
        "2257511633104077811878655409691534510425905491418630469122174020132740243250933668950420887342415359337655353939232877949674457997287503900839222217376721");
    /**
     * The displayName hash is a simple hash of the displayName which is suspected to be used
     * to select the appropriate login server.
     */
    private int nameHashCode;

    @SuppressWarnings("unused")
    @Override
    protected boolean doDecode(final IoSession session, IoBuffer in, ProtocolDecoderOutput out) {
        try {
            int state = (Integer) session.getAttribute("state", STATE_OPCODE);
            switch (state) {
                case STATE_UPDATE:
                    while (in.remaining() >= 4) {
                        int type = in.get() & 0xff;
                        final int cache = in.get() & 0xff;
                        final int id = in.getShort() & 0xffff;
                        switch (type) {
                            case 0: // non-urgent
                            case 1: // urgent
                                OnDemandPool.getOnDemandPool()
                                    .pushRequest(new OnDemandRequest(session, type, UpdateServer.getRequest(cache, id)));
                                break;
                            case 2: // clear requests
                            case 3:
                                OnDemandPool.getOnDemandPool().clearRequests();
                                break;
                            case 4: // client error
                                break;
                        }
                    }
                    return false;
                case STATE_OPCODE:
                    if (in.remaining() >= 1) {
                        /*
                         * Here we read the first opcode which indicates the type of
                         * connection.
                         *
                         * 14 = game 15 = update
                         *
                         * Updating is disabled in the vast majority of 317 clients.
                         */
                        int opcode = in.get() & 0xFF;
                        switch (opcode) {
                            case OPCODE_GAME:
                                session.setAttribute("state", STATE_LOGIN);
                                return true;
                            case OPCODE_UPDATE:
                                if (in.remaining() >= 4) {
                                    if (in.getInt() != Config.CLIENT_MAJOR_VERSION) {
                                        session.write(new PacketBuilder().put((byte) ReturnCode.SERVER_UPDATED.getOpcode()).toPacket());
                                        session.closeOnFlush();
                                        break;
                                    }
                                    session.setAttribute("state", STATE_UPDATE);
                                    session.write(new PacketBuilder().put((byte) 0).toPacket());
                                    return true;
                                }
                                in.rewind();
                                return false;
                            default:
                                LOGGER.info("Invalid opcode : " + opcode);
                                session.closeOnFlush();
                                break;
                        }
                    } else {
                        in.rewind();
                        return false;
                    }
                    break;
                case STATE_LOGIN:
                    if (in.remaining() >= 1) {
                        /*
                         * The displayName hash is a simple hash of the displayName which is suspected
                         * to be used to select the appropriate login server.
                         */
                        nameHashCode = in.get() & 0xFF;

                        /*
                         * We generated the server session key using a SecureRandom
                         * class for security.
                         */
                        long serverKey = RANDOM.nextLong();

                        /*
                         * The initial response is just 0s which the client is set to
                         * ignore (probably some sort of modification).
                         */
                        session.write(new PacketBuilder().put((byte) 0).putLong(serverKey).toPacket());
                        session.setAttribute("state", STATE_PRECRYPTED);
                        session.setAttribute("serverKey", serverKey);
                        return true;
                    }
                    break;
                case STATE_PRECRYPTED:
                    if (in.remaining() >= 2) {
                        /*
                         * We read the type of login.
                         *
                         * 16 = normal 18 = reconnection
                         */
                        int loginOpcode = in.get() & 0xFF;
                        if (loginOpcode != 16 && loginOpcode != 18) {
                            LOGGER.info("Invalid login opcode : " + loginOpcode);
                            session.closeOnFlush();
                            in.rewind();
                            return false;
                        }

                        /*
                         * We read the size of the login packet.
                         */
                        int loginSize = in.get() & 0xFF;

                        /*
                         * And calculated how long the encrypted block will be.
                         */
                        int loginEncryptSize = loginSize - (69);

                        /*
                         * This could be invalid so if it is we ignore it.
                         */
                        if (loginEncryptSize <= 0) {
                            LOGGER.info("Encrypted packet size zero or negative : " + loginEncryptSize);
                            session.closeOnFlush();
                            in.rewind();
                            return false;
                        }
                        session.setAttribute("state", STATE_CRYPTED);
                        session.setAttribute("size", loginSize);
                        session.setAttribute("encryptSize", loginEncryptSize);
                        return true;
                    }
                    break;
                case STATE_CRYPTED:
                    int size = (Integer) session.getAttribute("size");
                    int encryptSize = (Integer) session.getAttribute("encryptSize");
                    if (in.remaining() >= size) {
                        /*
                         * We now read a short which is the client version and check if
                         * it equals 464.
                         */
                        int version = in.getInt();
                        boolean wrong = !(version == 602 || version == 464 || version == 468 || version == 562 || version == 530);
                        if (wrong) {
                            session.closeOnFlush();
                            in.rewind();
                            return false;
                        }
                        boolean lowMemoryVersion = (in.get() & 0xFF) == 1;
                        boolean outdated = false;
                        for (int i = 0; i < 16; i++) {
                            int check = i;
                            if (version == 468) {
                                if (check == 0) {
                                    check = 28;
                                } else if (check == 1) {
                                    check = 29;
                                } else if (check == 7) {
                                    check = 30;
                                }
                            } else if (version == 602) {
                                if (check == 0) {
                                    check = 22;
                                } else if (check == 1) {
                                    check = 23;
                                } else if (check == 7) {
                                    check = 24;
                                }
                            } else if (version == 562) {
                                if (check == 0) {
                                    check = 16;
                                } else if (check == 1) {
                                    check = 17;
                                } else if (check == 7) {
                                    check = 18;
                                }
                            } else if (version == 530) {
                                if (check == 0) {
                                    check = 19;
                                } else if (check == 1) {
                                    check = 20;
                                } else if (check == 7) {
                                    check = 21;
                                }
                            }
                            int crc = in.getInt();
                            int cachedCrc = Cache.getCacheFileManagers()[check].crc;
                            if (cachedCrc != crc) {
                                outdated = true;
                            }
                        }

                        /*
                         * The encrypted size includes the size byte which we don't
                         * need.
                         */
                        encryptSize--;

                        /*
                         * We check if there is a mismatch in the sizing.
                         */
                        int reportedSize = in.get() & 0xFF;
                        if (reportedSize != encryptSize) {
                            session.closeOnFlush();
                            in.rewind();
                            return false;
                        }
                        byte[] rsaPayload = new byte[encryptSize];
                        in.get(rsaPayload);
                        IoBuffer rsaBuffer = IoBuffer
                            .wrap(new BigInteger(rsaPayload).modPow(RSA_EXPONENT, RSA_MODULUS).toByteArray());
                        int rsaOpcode = rsaBuffer.get() & 0xff;
                        if (rsaOpcode != 10) {
                            session.closeOnFlush();
                            in.rewind();
                            return false;
                        }

                        long clientKey = rsaBuffer.getLong();
                        long serverKey = (Long) session.getAttribute("serverKey");
                        long reportedServerKey = rsaBuffer.getLong();
                        if (reportedServerKey != serverKey) {
                            session.closeOnFlush();
                            in.rewind();
                            return false;
                        }
                    /*String macAddress = IoBufferUtils.getRS2String(rsaBuffer);
                    LOGGER.info("mac address: " + macAddress);*/

                        /*
                         * We read and format the name and passwords.
                         */
                        String name = IoBufferUtils.getRS2String(rsaBuffer).trim();
                        if (name.length() > Config.USERNAME_LENGTH_LIMIT) {
                            in.rewind();
                            return false;
                        }
                        String pass = IoBufferUtils.getRS2String(rsaBuffer);
                        if (pass.length() > Config.PASSWORD_LENGTH_LIMIT) {
                            in.rewind();
                            return false;
                        }

                        /*
                         * We check if hash matches
                         */
                        long user_hash = TextUtils.stringToLong(name);
                        int expectedHashCode = (int) (user_hash >> 16 & 31L);
                        if (expectedHashCode != nameHashCode) {
                            session.closeOnFlush();
                            in.rewind();
                            return false;
                        }

                        LOGGER.info(format("New login request for player \"%s\".", name));

                        /*
                         * And setup the ISAAC cipher which is used to encrypt and
                         * decrypt opcodes.
                         *
                         * However, without RSA, this is rendered useless anyway.
                         */
                        int[] sessionKey = new int[4];
                        sessionKey[0] = (int) (clientKey >> 32);
                        sessionKey[1] = (int) clientKey;
                        sessionKey[2] = (int) (reportedServerKey >> 32);
                        sessionKey[3] = (int) reportedServerKey;

                        session.removeAttribute("state");
                        session.removeAttribute("serverKey");
                        session.removeAttribute("size");
                        session.removeAttribute("encryptSize");

                        ISAACCipher inCipher = new ISAACCipher(sessionKey);
                        for (int i = 0; i < 4; i++) {
                            sessionKey[i] += 50;
                        }
                        ISAACCipher outCipher = new ISAACCipher(sessionKey);

                        /*
                         * Now, the login has completed, and we do the appropriate
                         * things to fire off the chain of events which will load and
                         * check the saved games etc.
                         */
                        session.getFilterChain().remove("protocol");
                        session.getFilterChain().addFirst("protocol", new ProtocolCodecFilter(RS2CodecFactory.GAME));
                        PlayerDetails pd = new PlayerDetails(session, name, pass, inCipher, outCipher, outdated, version,
                            false);
                        World.getWorld().sendPlayer(pd);
                    }
                    break;
            }
            in.rewind();
            return false;
        } catch (Exception e) {
            if (session instanceof InetSocketAddress) {
                final InetSocketAddress inetSocketAddress = (InetSocketAddress) session.getRemoteAddress();

                LOGGER.error("An error occurred whilst decoding the login for player at {}:{}!",
                    inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort(), e);
            } else {
                LOGGER.error("An error occurred whilst decoding the login of a player!", e);
            }

            in.rewind();
            return false;
        }
    }
}
