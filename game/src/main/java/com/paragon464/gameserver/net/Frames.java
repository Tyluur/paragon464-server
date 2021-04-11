package com.paragon464.gameserver.net;

import com.paragon464.gameserver.Config;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.masks.Graphic;
import com.paragon464.gameserver.model.entity.mob.player.FriendsAndIgnores;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.entity.mob.player.container.impl.Equipment;
import com.paragon464.gameserver.model.area.Areas;
import com.paragon464.gameserver.model.content.BeginnerTutorial;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.grounditem.GroundItem;
import com.paragon464.gameserver.model.region.DynamicRegion;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.model.region.Region;
import com.paragon464.gameserver.util.ProtocolUtils;
import com.paragon464.gameserver.util.TextUtils;
import com.paragon464.gameserver.util.Utils;
import lombok.val;

import java.util.List;
import java.util.Map;

/**
 * A utility class for sending packets.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public class Frames {

    /**
     * The player.
     */
    private Player player;
    private int count = 0;

    /**
     * Creates an action sender for the specified player.
     *
     * @param player The player to create the action sender for.
     */
    public Frames(Player player) {
        this.player = player;
    }

    public Frames sendClanChannel(String owner, String channelName, boolean inChannel, List<Player> members, Map<String, FriendsAndIgnores.ClanRank> friends) {
        PacketBuilder bldr = new PacketBuilder(55, Packet.Type.VARIABLE_SHORT);
        bldr.putLong(TextUtils.stringToLong(owner));
        bldr.putLong(TextUtils.stringToLong(channelName));
        bldr.put((byte) (inChannel ? 1 : 0));
        bldr.put((byte) (inChannel ? members.size() : 0)); //size
        //loop here
        if (inChannel) {
            for (Player p : members) {
                bldr.putLong(p.getNameAsLong());//player displayName
                bldr.putShort(1);//world
                int rank = (friends.containsKey(p.getDetails().getName()) ? friends.get(p.getDetails().getName()).getId() : (p.getDetails().getName().equals(owner) ? FriendsAndIgnores.ClanRank.OWNER.getId() : -1));
                bldr.put((byte) rank); //rank
                bldr.putRS2String("World 1");
            }
        }
        player.write(bldr.toPacket());
        return this;
    }

    public Frames sendClanMessage(String name, String channelName, String text, int rights) {
        PacketBuilder bldr = new PacketBuilder(54, Packet.Type.VARIABLE);
        bldr.putLong(TextUtils.stringToLong(name));
        bldr.put((byte) 1);
        bldr.putLong(TextUtils.stringToLong(channelName));
        bldr.putShort(Utils.random(255));
        text = TextUtils.fixChatMessage(text);
        bldr.put((byte) 0);
        bldr.put((byte) 0);
        bldr.put((byte) 0);
        bldr.put((byte) 2);
        byte[] chatStr = new byte[256];
        chatStr[0] = (byte) text.length();
        int offset = 1 + ProtocolUtils.encryptPlayerChat(chatStr, 0, 1, text.length(),
            text.getBytes());
        bldr.put(chatStr, 0, offset);
        player.write(bldr.toPacket());
        return this;
    }

    public Frames openURL(String url) {
        PacketBuilder pb = new PacketBuilder(42, Packet.Type.VARIABLE);
        pb.put(url.getBytes());
        player.write(pb.toPacket());
        return this;
    }

    /**
     * Sends the map region load command.
     *
     * @return The action sender instance, for chaining.
     */
    public Frames sendMapRegion() {
        player.setLastKnownRegion(player.getPosition());
        PacketBuilder pb = new PacketBuilder(221, Packet.Type.VARIABLE_SHORT);
        pb.putShortA(player.getPosition().getZoneY());
        for (int xCalc = (player.getPosition().getZoneX() - 6) / 8; xCalc <= (player.getPosition().getZoneX() + 6)
            / 8; xCalc++) {
            for (int yCalc = (player.getPosition().getZoneY() - 6) / 8; yCalc <= (player.getPosition().getZoneY() + 6)
                / 8; yCalc++) {
                int region = yCalc + (xCalc << 8);
                //System.out.println("map region: " + region);
                int[] data = new int[4];//MapDataLoader.getData(region);
                pb.putInt1(data[0]);
                pb.putInt1(data[1]);
                pb.putInt1(data[2]);
                pb.putInt1(data[3]);
            }
        }
        pb.putLEShort(player.getPosition().getZoneX());
        pb.putShort(player.getPosition().getLocalX());
        pb.put((byte) player.getPosition().getZ());
        pb.putShort(player.getPosition().getLocalY());
        player.write(pb.toPacket());
        return this;
    }

    public Frames sendConstructedRegion() {
        player.setLastKnownRegion(player.getPosition());
        PacketBuilder bldr = new PacketBuilder(222, Packet.Type.VARIABLE_SHORT);
        int middleChunkX = player.getPosition().getZoneX();
        int middleChunkY = player.getPosition().getZoneY();
        int localX = player.getPosition().getLocalX();
        int localY = player.getPosition().getLocalY();
        int z = player.getPosition().getZ();
        bldr.putLEShortA((short) middleChunkY);
        bldr.putByteS((byte) z);
        bldr.putLEShortA((short) middleChunkX);
        bldr.putLEShortA((short) localY);
        bldr.putShortA((short) localX);
        int sceneLength = 104 >> 4;
        int[] regionIds = new int[4 * sceneLength * sceneLength];
        int newRegionIdsCount = 0;
        bldr.startBitAccess();
        for (int plane = 0; plane < 4; plane++) {
            for (int realChunkX = (middleChunkX - sceneLength); realChunkX <= ((middleChunkX
                + sceneLength)); realChunkX++) {
                int regionX = realChunkX / 8;
                y:
                for (int realChunkY = (middleChunkY - sceneLength); realChunkY <= ((middleChunkY
                    + sceneLength)); realChunkY++) {
                    int regionY = realChunkY / 8;
                    // rcx / 8 = rx, rcy / 8 = ry, regionid is encoded region x
                    // and y
                    int regionId = (regionX << 8) + regionY;
                    Region region = World.getRegion(regionId);
                    int newChunkX;
                    int newChunkY;
                    int newPlane;
                    int rotation;
                    if (region instanceof DynamicRegion) { // generated map
                        DynamicRegion dynamicRegion = (DynamicRegion) region;
                        int[] pallete = dynamicRegion.getRegionCoords()[plane][realChunkX - (regionX * 8)][realChunkY
                            - (regionY * 8)];
                        newChunkX = pallete[0];
                        newChunkY = pallete[1];
                        newPlane = pallete[2];
                        rotation = pallete[3];
                    } else { // real map
                        newChunkX = realChunkX;
                        newChunkY = realChunkY;
                        newPlane = plane;
                        rotation = 0;// no rotation
                    }
                    // invalid chunk, not built chunk
                    if (newChunkX == 0 || newChunkY == 0)
                        bldr.putBits(1, 0);
                    else {
                        bldr.putBits(1, 1);
                        // chunk encoding = (x << 14) | (y << 3) | (plane <<
                        // 24), theres addition of two more bits for rotation
                        bldr.putBits(26, (rotation << 1) | (newPlane << 24) | (newChunkX << 14) | (newChunkY << 3));
                        int newRegionId = (((newChunkX / 8) << 8) + (newChunkY / 8));
                        for (int index = 0; index < newRegionIdsCount; index++)
                            if (regionIds[index] == newRegionId)
                                continue y;
                        regionIds[newRegionIdsCount++] = newRegionId;
                    }
                }
            }
        }
        bldr.finishBitAccess();
        for (int index = 0; index < newRegionIdsCount; index++) {
            //int[] xtea = Mapdata.getData(regionIds[index]);
            bldr.putInt(0);
            bldr.putInt(0);
            bldr.putInt(0);
            bldr.putInt(0);
        }
        player.write(bldr.toPacket());
        return this;
    }

    public void sendWelcomeScreen() {
        sendWindowPane(549, true);
        sendInterface(549, 2, 378, true);
        sendInterface(549, 3, 21, true);
        if (player.getDetails().getClientMode() > 464) {
            for (int i = 3; i < 7; i++) {
                animateInterface(9835, 21, i);
            }
        }
        modifyText("Always double check transactions, you never know who you are interacting with!", 21, 1);
        modifyText("Welcome to " + Config.SERVER_NAME, 378, 12);
        if (!player.getVariables().getLastAddress().isEmpty()) {
            modifyText("You last logged in from: " + player.getVariables().getLastAddress(), 378, 13);
        }
        String pinString = player.getAttributes().getInt("bank_pin_hash") != -1 ? "Bank Pin: <col=00FF00>SET</col>." : "Bank Pin: <col=ff0000>NOT SET</col><br><br>Visit a local bankbooth to create one.";
        modifyText(pinString, 378, 17);

        String emailString = player.getDetails().emailVerified() ? "Your email is currently <col=00FF00>verified</col>." : "Your email is currently <col=ff0000>unverified</col>.<br> For maximum account security, please verify your email!";
        modifyText("Email registered: <col=00FF00>" + player.getDetails().getCurrentEmail().replaceAll("(?<=..).(?=...*@)", "*") + "</col>.<br><br>" + emailString + "", 378, 14);

        String msgString = player.getDetails().getUnreadMessage() > 0 ? "<col=00FF00>" + player.getDetails().getUnreadMessage() + "</col>" : "<col=ff0000>" + player.getDetails().getUnreadMessage() + "</col>";
        modifyText("Unread messages: " + msgString, 378, 16);
        if (player.getDetails().isGoldMember()) {
            String daysString = player.getDetails().getGoldMemberDays() > 5 ? "<col=00FF00>" + player.getDetails().getGoldMemberDays() + "</col>" : "<col=ff0000>" + player.getDetails().getGoldMemberDays() + "</col>";
            modifyText("You're currently a Gold member.<br><br>Days remaining: " + daysString, 378, 19);
        } else {
            modifyText("You're <col=ff0000>NOT</col> a Gold member. Try checking out the membership benefits on our forums and make the choice today!", 378, 19);
        }
        player.getAttributes().set("welcome_screen", true);
    }

    public void closeWelcomeScreen() {
        sendGameScreen();
        boolean emailLock = player.getDetails().getCurrentEmail().equalsIgnoreCase("none");
        if (!emailLock) {
            if (!player.getAttributes().is("tutorial_completed")) {
                player.getVariables().setTutorial(new BeginnerTutorial(player));
            }
        }
    }

    public void sendGameScreen() {
        boolean shown = player.getAttributes().isSet("welcome_screen");
        boolean exceptions = (Areas.inWilderness(player.getPosition()) || player.getAttributes().isSet("caves_session"));
        val isNew = player.getAttributes().isSet("new_account_verify");
        if (isNew) {
            player.getFrames().sendMessage("Your account has been created and you may log into the forums!");
            player.getFrames().sendMessage("However, your email has <col=ff0000>not yet been verified.</col>");
            player.getFrames().sendMessage("Verify your email to receive a free double experience lamp.");
            player.getFrames().sendMessage("To verify, head over to the forum and send your verification email.");
            player.getAttributes().remove("stopActions");
            player.getVariables().setTutorial(new BeginnerTutorial(player));
        } else if (!shown) {
            if (!player.getDetails().emailVerified()) {
                player.getFrames().sendMessage("Your email has <col=ff0000>not yet been verified.</col>");
                player.getFrames().sendMessage("Confirm your email to receive a free double experience lamp.");
                player.getFrames().sendMessage("To verify, head over to the forum and send your verification email.");
            }
        }

        if (!shown && !exceptions && !isNew) {
            sendWelcomeScreen();
            return;
        }

        sendWindowPane(player.getSettings().getWindowScreen(), true);
        int currentOverlay = player.getInterfaceSettings().getCurrentOverlay();
        if (currentOverlay != -1) {
            player.getInterfaceSettings().openOverlay(currentOverlay);
        }/* else {
            player.getInterfaceSettings().closeOverlay();
        }*/
        int autocastConfig = player.getAttributes().getInt("autocastconfig");
        sendLoginSettings();
        if (autocastConfig > 0) {//TODO - fix '90, autocastConfig' & '90, 83', wen switching to HD
            player.getFrames().sendInterfaceVisibility(90, 83, false);
            player.getFrames().sendInterfaceVisibility(90, autocastConfig, true);
            player.getFrames().sendVarp(43, 3);
            player.getFrames().sendTab(player.getSettings().isInResizable() ? 65 : 86, 90);
            player.getFrames().modifyText(player.getEquipment().getSlot(3).getDefinition().getName(), 90, 0);
        }
        refreshGameFrameConfigs();
    }

    private void refreshGameFrameConfigs() {
        if (!player.getAttributes().isSet("quick_prayer_toggled")) {
            player.getAttributes().set("quick_prayer_toggled", false);
        }
        if (!player.getAttributes().isSet("show_exp_counter")) {
            player.getAttributes().set("show_exp_counter", false);
        }
        player.getFrames().sendVarp(555, player.getAttributes().isSet("show_exp_counter") ? 1 : 0);
        player.getFrames().sendVarp(173, player.getWalkingQueue().isRunningToggled() ? 1 : 0);
        player.getFrames().sendVarp(602, !player.getPrayers().quickPrayersOff() ? 1 : 0);
    }

    public Frames sendChildColor(int inter, int child, int red, int green, int blue) {
        player.write(new PacketBuilder(244).putInt(inter << 16 | child)
            .putShortA((short) red << 198761418 | green << -1750503739 | blue).toPacket());
        return this;
    }

    public Frames sendComponentPosition(int interfaceId, int childId, int x, int y) {
        player.write(new PacketBuilder(201).putLEInt(interfaceId << 16 | childId).putShortA(y).putShort(x).toPacket());
        return this;
    }

    public Frames sendHintArrowFollowing(Mob mob) {
        PacketBuilder bldr = new PacketBuilder(160);
        // TODO - fix!
        /*
         * bldr.put((byte) ((mob.isNPC() ? 1 : 10 & 0x1f) | (1 << 5)));
         * bldr.put((byte) 1); bldr.putShort(mob.getIndex());
         * bldr.putShort(2500); bldr.skip(3); bldr.putShort(65535);
         * player.write(bldr.toPacket());
         */
        /*
         * bldr.put((byte) 2); bldr.put((byte) 1);
         * bldr.putShort(mob.getItemLocation().getX());
         * bldr.putShort(mob.getItemLocation().getY()); bldr.put((byte) (100 * 4
         * >> 2));// Distance to show on map bldr.putShort(65535);
         */
        // ////player.write(bldr.toPacket());
        return this;
    }

    public Frames sendHintArrow(Mob mob) {
        PacketBuilder bldr = new PacketBuilder(160);
        if (mob != null) {
            bldr.put((byte) (mob.isNPC() ? 1 : 10));
            bldr.put((byte) 1);
            bldr.putShort(mob.getIndex());
            bldr.put((byte) 0);
            bldr.put((byte) 0);
            bldr.put((byte) 0);
        } else {
            bldr.put((byte) (10));
            bldr.put((byte) 1);
            bldr.putShort(-1);
            bldr.put((byte) 0);
            bldr.put((byte) 0);
            bldr.put((byte) 0);
        }
        player.write(bldr.toPacket());
        return this;
    }

    public Frames sendRemoveHint(int index) {
        PacketBuilder bldr = new PacketBuilder(217);
        bldr.put((byte) (index << 5));
        // //////player.write(bldr.toPacket());
        return this;
    }

    public void setMinimapStatus(int setting) {
        // player.write(new PacketBuilder(55).put((byte)
        // setting).toPacket());
    }

    public void sendClueScroll(Item[] items) {
        player.getInterfaceSettings().openInterface(364);
        this.sendClickMask(0, 12, 364, 1, 1278);
        this.sendItems(364, 1, 0, items);
    }

    public void sendClickMask(int start, int end, int interfaceId, int child, int set) {
        PacketBuilder bldr = new PacketBuilder(254);
        bldr.putLEShort(end);
        bldr.putInt(interfaceId << 16 | child);
        bldr.putShortA(start);
        bldr.putInt1(set);
        player.write(bldr.toPacket());
    }

    public void sendItems(int interfaceId, int childId, int type, Item[] items) {
        PacketBuilder bldr = new PacketBuilder(92, Packet.Type.VARIABLE_SHORT);
        bldr.putInt(interfaceId << 16 | childId);
        bldr.putShort(type);
        bldr.putShort(items.length);
        for (Item item : items) {
            if (item != null) {
                int count = item.getAmount();
                if (count > 254) {
                    bldr.putByteC((byte) 255);
                    bldr.putInt(count);
                } else {
                    bldr.putByteC((byte) count);
                }
                bldr.putInt(item.getId() + 1);
            } else {
                bldr.putByteC((byte) 0);
                bldr.putInt(0);
            }
        }
        player.write(bldr.toPacket());
    }

    public void sendSystemUpdate(int time) {
        player.write(new PacketBuilder(30).putShort(time * 50).toPacket());
    }

    public void sendObjectAnimation(int anim, GameObject object) {
        Position loc = object.getPosition();
        sendArea(new Position(loc.getX(), loc.getY(), player.getPosition().getZ()));
        PacketBuilder spb = new PacketBuilder(179);
        int ot = ((object.getType() << 2) + (object.getRotation() & 0x3));
        spb.putShort(anim);
        spb.putByteS((byte) 0);
        spb.put((byte) ot);
        player.write(spb.toPacket());
    }

    public void sendArea(Position position) {
        PacketBuilder bldr = new PacketBuilder(132);
        int regionX = player.getLastKnownRegion().getZoneX();
        int regionY = player.getLastKnownRegion().getZoneY();
        bldr.put((byte) ((position.getY() - ((regionY - 6) * 8))));
        bldr.put((byte) ((position.getX() - ((regionX - 6) * 8))));
        player.write(bldr.toPacket());
    }

    public void setFriendsListStatus() {
        PacketBuilder spb = new PacketBuilder(152);
        spb.put((byte) 2);
        player.write(spb.toPacket());
    }

    public void setPrivacyOptions() {
        player.write(new PacketBuilder(156).put((byte) player.getFriendsAndIgnores().getPrivacyOption(0))
            .put((byte) player.getFriendsAndIgnores().getPrivacyOption(1))
            .put((byte) player.getFriendsAndIgnores().getPrivacyOption(2)).toPacket());
    }

    public void sendSentPrivateMessage(String name, byte[] message) {
        player.write(new PacketBuilder(23, Packet.Type.VARIABLE).putRS2String(name).put(message).toPacket());
    }

    public void sendReceivedPrivateMessage(String name, int rights, byte[] packed) {
        int messageCounter = 0;
        player.write(new PacketBuilder(50, Packet.Type.VARIABLE).putRS2String(name)
            .putShort(1).put(new byte[]{(byte) ((messageCounter << 16) & 0xFF),
                (byte) ((messageCounter << 8) & 0xFF), (byte) (messageCounter & 0xFF)})
            .put((byte) rights).put(packed).toPacket());
    }

    public void sendFriend(String name, int rank, int world) {
        PacketBuilder bldr = new PacketBuilder(100, Packet.Type.VARIABLE);
        bldr.putRS2String(name);
        bldr.putShort(world);
        bldr.put((byte) rank);
        player.write(bldr.toPacket());
    }

    public void sendIgnores(String[] names) {
        PacketBuilder spb = new PacketBuilder(75, Packet.Type.VARIABLE);
        for (String name : names) {
            spb.putRS2String(name);
        }
        player.write(spb.toPacket());
    }

    public void clearGroundItem(final GroundItem item) {
        if (item != null) {
            sendArea(item.getPosition());
            PacketBuilder bldr = new PacketBuilder(39);
            bldr.putInt(item.getId());
            bldr.putByteS((byte) 0);
            player.write(bldr.toPacket());
        }
    }

    public void sendGroundItem(final GroundItem item) {
        if (item != null) {
            sendArea(item.getPosition());
            PacketBuilder bldr = new PacketBuilder(112);
            bldr.putInt(item.getId());
            bldr.putLEShort(item.getAmount());
            bldr.putByteS((byte) 0);
            player.write(bldr.toPacket());
        }
    }

    public void createObject(int objectId, Position loc, int face, int type) {
        sendArea(new Position(loc.getX(), loc.getY(), player.getPosition().getZ()));
        PacketBuilder spb = new PacketBuilder(17);
        int ot = ((type << 2) + (face & 3));
        spb.putByteA((byte) 0);
        spb.putInt(objectId);
        spb.putByteA((byte) ot);
        player.write(spb.toPacket());
    }

    public void removeObject(int type, int face, Position loc) {
        sendArea(new Position(loc.getX(), loc.getY(), player.getPosition().getZ()));
        PacketBuilder spb = new PacketBuilder(16);
        int ot = ((type << 2) + (face & 3));
        spb.putByteA((byte) ot);
        spb.putByteA((byte) 0);
        player.write(spb.toPacket());
    }

    public void createObject(GameObject object) {
        if (player.getPosition().getZ() != object.getPosition().getZ()) {
            return;
        }
        sendArea(object.getPosition());
        PacketBuilder spb = new PacketBuilder(17);
        spb.putByteA((byte) 0);
        spb.putInt(object.getId());
        spb.putByteA((byte) ((object.getType() << 2) + (object.getRotation() & 3)));
        player.write(spb.toPacket());
    }

    public void removeObject(GameObject object) {
        if (player.getPosition().getZ() != object.getPosition().getZ()) {
            return;
        }
        sendArea(object.getPosition());
        PacketBuilder spb = new PacketBuilder(16);
        int ot = ((object.getType() << 2) + (object.getRotation() & 3));
        spb.putByteA((byte) ot);
        spb.putByteA((byte) 0);
        player.write(spb.toPacket());
    }

    public void sendStillGraphics(Position loc, Graphic graphics, int tilesAway) {
        sendArea(loc);
        player.write(new PacketBuilder(186).put((byte) tilesAway).putShort(graphics.getId())
            .put((byte) graphics.getHeight()).putShort(graphics.getDelay()).toPacket());
    }

    public void sendNPCHead(int npcID, int interfaceId, int childId) {
        player.write(new PacketBuilder(207).putLEShortA(npcID).putInt(interfaceId << 16 | childId).toPacket());
    }

    public void itemOnInterface(int inter, int child, int size, int item) {
        if (size == 0)
            size = 1;
        player.write(new PacketBuilder(114).putLEInt(size).putShort(item).putLEInt(inter << 16 | child).toPacket());
    }

    public void sendChatboxInterface(int childId) {
        sendInterface(player.getSettings().getWindowScreen(), player.getSettings().isInResizable() ? 60 : 79, childId, false);
    }

    public void sendInterface(int windowId, int position, int interfaceId, boolean walkable) {
        PacketBuilder pb = new PacketBuilder(238);
        pb.putInt1((windowId << 16) | position);
        pb.putShort(interfaceId);
        pb.putByteC(walkable ? 1 : 0);
        player.write(pb.toPacket());
    }

    public void sendEnergy() {
        player.write(new PacketBuilder(163).put((byte) player.getSettings().getEnergy()).toPacket());
    }

    /**
     * Sends the player the specified weight.
     *
     * @param weight The weight to be sent to the player.
     */
    public void sendWeight(final double weight) {
        final PacketBuilder bldr = new PacketBuilder(PacketConstants.UPDATE_WEIGHT_OPCODE, Packet.Type.FIXED);
        bldr.putShort((int) Math.floor(weight));

        player.write(bldr.toPacket());
    }

    /**
     * Calculates the player's total item weight and sends it.
     */
    public void sendWeight() {
        player.calculateWeight();
        sendWeight(player.getWeight());
    }

    public void sendPlayerHead(int interfaceID, int childID) {
        player.write(new PacketBuilder(8).putLEInt(interfaceID << 16 | childID).toPacket());
    }

    public void animateInterface(int animID, int interfaceId, int childId) {
        PacketBuilder bldr = new PacketBuilder(63);
        bldr.putInt2((interfaceId << 16) + childId);
        bldr.putLEShort(animID);
        player.write(bldr.toPacket());
    }

    public void sendPlayerOption(String option, int slot, boolean top) {
        PacketBuilder bldr = new PacketBuilder(72, Packet.Type.VARIABLE);
        bldr.putRS2String(option);
        bldr.putByteS((byte) slot);
        bldr.putByteC(top ? (byte) 1 : (byte) 0);
        player.write(bldr.toPacket());
    }

    public void tradeWarning(int slot) {
        Object[] opt = new Object[]{slot, 7, 4, 335 << 16 | 51};
        sendClientScript(143, opt, "iiii");
    }

    public void sendClientScript(int id, Object[] params, String types) {
        PacketBuilder bldr = new PacketBuilder(69, Packet.Type.VARIABLE_SHORT);
        bldr.putRS2String(types);
        if (params.length > 0) {
            int j = 0;
            for (int i = types.length() - 1; i >= 0; i--, j++) {
                if (types.charAt(i) == 115) {
                    bldr.putRS2String((String) params[j]);
                } else {
                    bldr.putInt((Integer) params[j]);
                }
            }
        }
        bldr.putInt(id);
        player.write(bldr.toPacket());
    }

    public void tradeWarning2(int slot) {
        Object[] opt = new Object[]{slot, 7, 4, 335 << 16 | 49};
        sendClientScript(143, opt, "iiii");
    }

    public void displayEnterName(String name) {
        Object[] o = {name};
        sendClientScript(109, o, "s");
    }

    public void clearMapFlag() {
        player.write(new PacketBuilder(68).toPacket());
    }

    public void sendUnlockInterfaceButtons(int interfaceId, int buttonId, int fromSlot, int toSlot,
                                           int... optionsSlots) {
        int settingsHash = 0;
        for (int slot : optionsSlots)
            settingsHash |= 2 << slot;
        sendClickMask(fromSlot, toSlot, interfaceId, buttonId, settingsHash);
    }

    public void displayInventoryInterface(int childId) {
        sendInterface(player.getSettings().getWindowScreen(), player.getSettings().isInResizable() ? 63 : 84, childId, false);
    }

    public void sendSkillLevels() {
        player.getSkills().getSkillSet().forEach(this::sendSkillLevel);
    }

    public void sendSkillLevel(SkillType skill) {
        PacketBuilder spb = new PacketBuilder(190);
        spb.putByteS((byte) skill.ordinal());
        spb.putLEInt((int) player.getSkills().getExperience(skill));
        if (skill == SkillType.PRAYER) {
            spb.put((byte) Math.ceil(player.getSettings().getPrayerPoints()));
        } else {
            spb.put((byte) player.getSkills().getCurrentLevel(skill));
        }
        player.write(spb.toPacket());
    }

    public void sendMessage(String message) {
        sendMessage(message, 0, "");
    }

    public void sendMessage(String message, int type) {
        sendMessage(message, type, "");
    }

    public void sendMessage(String message, int type, String message2) {
        player.write(new PacketBuilder(108, Packet.Type.VARIABLE).putRS2String(message).put((byte) type).putRS2String(message2).toPacket());
    }

    public void sendItem(int interfaceId, int childId, int type, int slot, Item item) {
        PacketBuilder bldr = new PacketBuilder(120, Packet.Type.VARIABLE_SHORT);
        bldr.putInt(interfaceId << 16 | childId);
        bldr.putShort(type);
        bldr.putSmart(slot);
        if (item != null) {
            bldr.putInt(item.getId() + 1);
            int count = item.getAmount();
            if (count > 254) {
                bldr.put((byte) 255);
                bldr.putInt(count);
            } else {
                bldr.put((byte) count);
            }
        } else {
            bldr.putInt(0);
            bldr.put((byte) -1);
        }
        player.write(bldr.toPacket());
    }

    public void toggleBankSearching() {
        if (!player.getAttributes().isSet("bank_searching")) {
            player.getFrames().sendVarp(558, 1);
            player.getFrames().sendClientScript(220, new Object[]{0}, "");
            player.getAttributes().set("bank_searching", true);
            player.getInterfaceSettings().setEnterAmountInterfaceId(12);
        } else {
            player.getFrames().sendVarp(558, 0);
            player.getFrames().sendClientScript(101, new Object[]{1}, "i");
            player.getAttributes().remove("bank_searching");
            player.getInterfaceSettings().setEnterAmountInterfaceId(-1);
        }
    }

    public void sendVarp(int id, int value) {
        if (value < 128 && value > -128) {
            PacketBuilder bldr = new PacketBuilder(245);
            bldr.putShortA(id);
            bldr.put((byte) value);
            player.write(bldr.toPacket());
        } else {
            PacketBuilder bldr = new PacketBuilder(37);
            bldr.putShortA(id);
            bldr.putLEInt(value);
            player.write(bldr.toPacket());
        }
    }

    public void sendBlankClientScript(int id, String s) {
        PacketBuilder packet = new PacketBuilder(69, Packet.Type.VARIABLE_SHORT).putRS2String(s);
        packet.putInt(id);
        player.write(packet.toPacket());
    }

    public void sendInterfaceVisibility(int interfaceId, int childId, boolean show) {
        PacketBuilder bldr = new PacketBuilder(142);
        bldr.putByteS((byte) (!show ? 1 : 0));
        bldr.putShort(childId);
        bldr.putShort(interfaceId);
        player.write(bldr.toPacket());
    }

    public void forceSendTab(int tab) {
        sendClientScript(115, new Object[]{tab}, "i");
    }

    public void sendBlankClientScript(int id) {
        PacketBuilder bldr = new PacketBuilder(253, Packet.Type.VARIABLE_SHORT);
        bldr.putShort(count++);
        bldr.putRS2String("");
        bldr.putInt(id);
        // player.write(bldr.toPacket());
        /*
         * PacketBuilder packet = new PacketBuilder(69, Type.VARIABLE_SHORT)
         * .putRS2String(""); packet.putInt(id);
         */
        // player.write(packet.toPacket());
    }

    public void modifyText(int interfaceId, int childId, String string) {
        modifyText(string, interfaceId, childId);
    }

    public void modifyText(String string, int interfaceId, int childId) {
        PacketBuilder bldr = new PacketBuilder(47, Packet.Type.VARIABLE_SHORT);
        bldr.putInt1(interfaceId << 16 | childId);
        bldr.putRS2String(string);
        player.write(bldr.toPacket());
    }

    public void sendWindowPane(int pane, boolean refresh) {
        player.write(new PacketBuilder(77).putLEShortA(pane).toPacket());
    }

    public void sendTab(int tabId, int childId) {
        sendInterface(player.getSettings().getWindowScreen(), tabId, childId, true);
    }

    public void closeGameScreen() {
        player.write(new PacketBuilder(137).putInt(player.getSettings().getWindowScreen() << 16 | (player.getSettings().isInResizable() ? 89 : 62)).toPacket());
    }

    public void closeOverlay() {
        player.write(new PacketBuilder(137).putInt(player.getSettings().getWindowScreen() << 16 | (player.getSettings().isInResizable() ? 96 : 64)).toPacket());
    }

    public void sendSideBarInterfaces(boolean finishedTut) {
        sendTab(player.getSettings().isInResizable() ? 58 : 77, 137);// chatbox
        sendTab(player.getSettings().isInResizable() ? 75 : 96, 182);
        if (player.getAttributes().is("tutorial_completed") || finishedTut) {
            sendTab(player.getSettings().isInResizable() ? 65 : 86, 92);
            sendTab(player.getSettings().isInResizable() ? 66 : 87, 613);
            sendTab(player.getSettings().isInResizable() ? 67 : 88, 274);
            sendTab(player.getSettings().isInResizable() ? 68 : 89, 149);
            sendTab(player.getSettings().isInResizable() ? 69 : 90, 387);
            sendTab(player.getSettings().isInResizable() ? 70 : 91, player.getSettings().isCursesEnabled() ? 597 : 271);
            sendTab(player.getSettings().isInResizable() ? 71 : 92, player.getSettings().getMagicType() == 1 ? 192 : player.getSettings().getMagicType() == 2 ? 193 : 430);
            sendTab(player.getSettings().isInResizable() ? 72 : 93, 611);
            sendTab(player.getSettings().isInResizable() ? 73 : 94, 550);
            sendTab(player.getSettings().isInResizable() ? 74 : 95, 551);
            sendTab(player.getSettings().isInResizable() ? 76 : 97, 261);
            sendTab(player.getSettings().isInResizable() ? 77 : 98, 464);
        }
    }

    /**
     * Sends all the login packets.
     *
     * @return The action sender instance, for chaining.
     */
    public Frames sendLogin() {
        player.loadMapRegions();
        player.getAttributes().set("active", true);
        player.getFrames().sendMessage("Welcome to " + Config.SERVER_NAME + ".");
        if (!player.getSettings().isInResizable()) {
            sendGameScreen();
        }
        return this;
    }

    public void sendLoginSettings() {
        Areas.handle_login(player);
        refreshContainerInterfaces();
        sendEnergy();
        sendSkillLevels();
        sendVarp(281, 1001);
        player.getFrames().sendVarp(172, !player.getSettings().isAutoRetaliating() ? 1 : 0);
        sendVarp(168, 4);
        sendVarp(169, 4);
        sendVarp(872, 4);
        sendPlayerOption("Follow", 3, false);
        sendPlayerOption("Trade with", 4, false);
        player.getSettings().refresh();
        sendVarp(181, 0);
        player.getFriendsAndIgnores().refresh();
        player.getFriendsAndIgnores().login();
        player.getFrames().sendWeight();
        Equipment.setWeapon(player, true);
        long deductXP = (long) Math.floor((System.currentTimeMillis() - player.getAttributes().getLong("last_login")) / 600);
        int bonusXPRemaining = (int) (player.getAttributes().getInt("bonus_xp_ticks") - deductXP);
        if (bonusXPRemaining < 0) bonusXPRemaining = 0;
        if (bonusXPRemaining <= 0) {
            player.getAttributes().set("xp_multiplier", 1);
        }
        player.getAttributes().set("bonus_xp_ticks", bonusXPRemaining);
        player.getControllerManager().login();
    }

    public void refreshContainerInterfaces() {
        sendSideBarInterfaces(false);
        player.getEquipment().refresh();
        Equipment.setWeapon(player, true);
        player.getInventory().refresh();
        player.getBonuses().recalc();
    }

    public void forceLogout() {
        if (!player.getAttributes().isSet("force_logout")) {
            player.getAttributes().set("force_logout", true);
        }
        sendLogout();
    }

    /**
     * Sends the logout packet.
     *
     * @return The action sender instance, for chaining.
     */
    public Frames sendLogout() {
        player.write(new PacketBuilder(167).toPacket());
        /*player.getSession().write(new PacketBuilder(167).toPacket()).addListener(new IoFutureListener() {
            @Override
            public void operationComplete(IoFuture arg0) {
                arg0.getSession().closeOnFlush();
            }
        });*/
        return this;
    }

    /**
     * Sends a projectile to a position.
     *
     * @param start       The starting position.
     * @param finish      The finishing position.
     * @param id          The graphic id.
     * @param delay       The delay before showing the projectile.
     * @param angle       The angle the projectile is coming from.
     * @param speed       The speed the projectile travels at.
     * @param startHeight The starting height of the projectile.
     * @param endHeight   The ending height of the projectile.
     * @param lockon      The lockon index of the projectile, so it follows them if they
     *                    move.
     * @param slope       The slope at which the projectile moves.
     * @param radius      The radius from the centre of the tile to display the
     *                    projectile from.
     * @return The action sender instance, for chaining.
     */
    public Frames sendProjectile(Position start, Position finish, int id, int delay, int angle, int speed,
                                 int startHeight, int endHeight, Mob lockon, int slope, int radius) {
        int offsetX = (start.getX() - finish.getX()) * -1;
        int offsetY = (start.getY() - finish.getY()) * -1;
        sendProjectileCoords(start);
        PacketBuilder bldr = new PacketBuilder(218);
        bldr.put((byte) angle);
        bldr.put((byte) offsetX);
        bldr.put((byte) offsetY);
        bldr.putShort(lockon == null ? 0 : lockon.isPlayer() ? -(lockon.getIndex() + 1) : lockon.getIndex() + 1);
        bldr.putInt(id);
        bldr.put((byte) startHeight);
        bldr.put((byte) endHeight);
        bldr.putShort(delay);
        bldr.putShort(speed);
        bldr.put((byte) slope);
        bldr.put((byte) radius);
        player.write(bldr.toPacket());
        return this;
    }

    public void sendProjectileCoords(Position position) {
        PacketBuilder spb = new PacketBuilder(132);
        int regionX = player.getLastKnownRegion().getZoneX();
        int regionY = player.getLastKnownRegion().getZoneY();
        spb.put((byte) (position.getY() - ((regionY - 6) * 8) - 2));
        spb.put((byte) (position.getX() - ((regionX - 6) * 8) - 3));
        player.write(spb.toPacket());
    }
}
